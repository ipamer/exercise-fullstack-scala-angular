package com.mymeetup

import akka.NotUsed
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.ThrottleMode
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, RunnableGraph, Sink, Source}
import akka.util.Timeout

import scala.collection.immutable.Seq
import scala.concurrent.duration._


object App extends App {

  val HOST_PORT = 8080
  val ROUTE_PATH = "mymeetups"
  val BROADCAST_CHECK_INTERVAL = 3.second

  implicit val system = ActorSystem("Server")

  // - Source
  val kafkaActor = system.actorOf(Props(new MeetupActor(KafkaDataSource())), name = "meetup_actor")
  kafkaActor ! StartCalculating
  implicit val askTimeout = Timeout(5.seconds)
  private val dataSource = Source.repeat(ContinuousResults)
    .ask[String](parallelism = 1)(kafkaActor)
    .throttle(1, BROADCAST_CHECK_INTERVAL, 1, ThrottleMode.Shaping)
    .filter(msg => msg != null && msg != "")

  // Go via BroadcastHub to allow multiple clients to connect
  val runnableGraph: RunnableGraph[Source[String, NotUsed]] =
    dataSource.toMat(BroadcastHub.sink(bufferSize = 256))(Keep.right)

  val producer: Source[String, NotUsed] = runnableGraph.run()
  producer.runWith(Sink.ignore) // Optional - add sink to avoid backpressuring the original flow when no clients are attached

  private val wsHandler: Flow[Message, Message, NotUsed] =
    Flow[Message]
      .mapConcat(_ => Seq.empty[String]) // Ignore any data sent from the client
      .merge(Source.single(ReadResults).ask[String](parallelism = 1)(kafkaActor)) // send once on connect
      .merge(producer) // Stream the data to the client
      .map(msg => TextMessage(msg.toString))

  val route =
    path(ROUTE_PATH) {
      handleWebSocketMessages(wsHandler)
    }

  Http().bindAndHandle(route, "0.0.0.0", HOST_PORT)
  println(s"Started HTTP server on port $HOST_PORT")

}
