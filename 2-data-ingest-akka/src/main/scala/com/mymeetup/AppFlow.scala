package com.mymeetup

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Flow, Keep, Source}
import net.liftweb.json.Serialization.write
import net.liftweb.json.{DefaultFormats, NoTypeHints, Serialization, parse}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps


case class Meetup(group_city: String, group_country: String, group_lat: Double, group_lon: Double)

object AppFlow {

  val kafkaBootstrap: String = "localhost:9092"
  val kafkaTopic: String = "meetups"

  implicit val system: ActorSystem = IngestActorSystem.system

  // - Kafka settings
  val config = system.settings.config.getConfig("akka.kafka.producer")
  val producerSettings = ProducerSettings(config, new StringSerializer, new StringSerializer)
    .withBootstrapServers(kafkaBootstrap)

  /** **************************************************
   * Flow definitions
   */

  import system.dispatcher

  val messageToJsonFlow: Flow[Message, Option[String], NotUsed] =
    Flow[Message]
      .collect {
        case TextMessage.Strict(text) => Future.successful(text)
        case TextMessage.Streamed(stream) => stream
          .limit(100) // Max frames we are willing to wait for
          .completionTimeout(5 seconds) // Max time until last frame
          .runFold("")(_ + _) // Merges the frames
          .flatMap(msg => Future.successful(msg))
      }
      .mapAsync(1)(identity)
      .map { jsonString => getMeetupJsonFromInput(jsonString) }
      .filter(_.isDefined)

  val kafkaSink = Producer.plainSink(producerSettings)

  val mainFlow: Flow[Message, Message, Promise[Option[Message]]] =
    Flow.fromSinkAndSourceMat(

      messageToJsonFlow
        .map(meetup => new ProducerRecord[String, String](kafkaTopic, meetup.get))
        .to(kafkaSink),

      Source.maybe[Message]
    )(Keep.right)

  def getMeetupJsonFromInput(jsonString: String): Option[String] = {
    try {
      val json = parse(jsonString)
      implicit val formats = DefaultFormats
      val meetup = (json \ "group").extract[Meetup]
      val meetupJsonString = write(meetup)(Serialization.formats(NoTypeHints))
      println(meetupJsonString) // TODO remove log - kept for showcasing
      Some(meetupJsonString)
    } catch {
      case e: Throwable => None
    }
  }

}
