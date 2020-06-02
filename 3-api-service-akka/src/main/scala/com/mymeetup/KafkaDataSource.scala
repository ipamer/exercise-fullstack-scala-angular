package com.mymeetup

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Keep, Sink}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.collection.immutable
import scala.concurrent.Future


trait MeetupDataSource {
  def start(processRecord: (String, String) => Future[Done])
}

object KafkaDataSource {
  def apply(): KafkaDataSource = new KafkaDataSource()
}

class KafkaDataSource extends MeetupDataSource {

  // TODO externalize
  private val kafkaBootstrap: String = "localhost:9092"
  private val kafkaTopic: String = "meetups"
  private implicit val system = ActorSystem("Server")

  def start(processRecord: (String, String) => Future[Done]) = {
    val config = system.settings.config.getConfig("akka.kafka.consumer")
    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
        .withBootstrapServers(kafkaBootstrap)
        .withGroupId("group1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    val control: DrainingControl[immutable.Seq[Done]] =
      Consumer
        .atMostOnceSource(consumerSettings, Subscriptions.topics(kafkaTopic))
        .mapAsync(1)(record => processRecord(record.key(), record.value.map(_.toChar).mkString))
        .toMat(Sink.seq)(Keep.both)
        .mapMaterializedValue(DrainingControl.apply)
        .run()
  }

}
