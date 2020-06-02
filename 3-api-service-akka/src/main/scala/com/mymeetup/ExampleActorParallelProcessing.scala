//package com.mymeetup
//
//import akka.NotUsed
//import akka.actor.{Actor, ActorSystem, Props, _}
//import akka.stream.scaladsl.{Sink, Source}
//import akka.util.Timeout
//
//import scala.concurrent.duration._
//
//
//object ExampleActorParallelProcessing {
//
//  def apply() = {
//
//    implicit val system = ActorSystem("Server")
//    //implicit val mat = ActorMaterializer()
//
//    val kafkaActor = system.actorOf(Props[KafkaActor], name = "kafkaactor")
//    //kafkaActor ! "hello"
//
//    implicit val askTimeout = Timeout(5.seconds)
//    val words: Source[String, NotUsed] = Source(List("hello", "hi", "hi", "hi", "hi", "hi"))
//    words
//      .ask[String](parallelism = 1)(kafkaActor)
//      .map(_.toLowerCase)
//      .runWith(Sink.ignore)
//  }
//
//}
//
///** *****************************************
// * MODEL
// */
//
//class KafkaActor extends Actor {
//  def receive = {
//    //case CaseClass(name) =>
//    case message: String =>
//      println("Message recieved from " + sender.path.name + " massage: " + message);
//      println("Replying to " + sender().path.name);
//      sender() ! "I got your message";
//    case _ => println(s"Parent got some other message.")
//  }
//}
