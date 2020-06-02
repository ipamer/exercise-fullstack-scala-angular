package com.mymeetup

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Keep, Sink}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.Future

object ExampleConsumeKafkaWithAkkaStream {

  val kafkaBootstrap: String = "localhost:9092"
  val kafkaTopic: String = "meetups"

  def calculate(): Unit = {
    implicit val system = ActorSystem("Server")
    //implicit val mat = ActorMaterializer()

    val config = system.settings.config.getConfig("akka.kafka.consumer")
    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
        .withBootstrapServers(kafkaBootstrap)
        .withGroupId("group1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    //val control: DrainingControl[Seq[Done]] =
    val control =
      Consumer
        .atMostOnceSource(consumerSettings, Subscriptions.topics(kafkaTopic))
        .mapAsync(1)(record => business(record))
        .toMat(Sink.seq)(Keep.both)
        .mapMaterializedValue(DrainingControl.apply)
        .run()

    //control.stop()
  }

  def business(rec: ConsumerRecord[String, Array[Byte]]) = {
    println(rec.toString)
    Future.successful(Done)
  }

}
