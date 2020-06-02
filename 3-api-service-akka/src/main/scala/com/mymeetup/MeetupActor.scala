package com.mymeetup

import akka.Done
import akka.actor.Actor
import net.liftweb.json.Serialization.write
import net.liftweb.json.{DefaultFormats, parse, _}

import scala.collection.mutable
import scala.concurrent.Future


case object StartCalculating

case object ReadResults

case object ContinuousResults

case class Meetup(group_city: String, group_country: String, group_lat: Double, group_lon: Double)

case class MeetupResponse(group_city: String, group_country: String, group_lat: Double, group_lon: Double, numberOfMeetups: Int)


class MeetupActor(dataSource: MeetupDataSource) extends Actor {

  type City = String
  type NumberOfMeetups = Int

  private var started = false
  private var cacheSent = false
  private val cache = mutable.HashMap[City, (NumberOfMeetups, Meetup)]()
  private var top10 = List[(City, NumberOfMeetups)]()

  def receive = {
    case ContinuousResults =>
      if (!cacheSent) {
        cacheSent = true
        sender() ! getJsonResponse
      } else {
        sender() ! ""
      }
    case ReadResults =>
      sender() ! getJsonResponse
    case StartCalculating =>
      if (!started) {
        started = true
        dataSource.start(processRecord)
      }
    case _ =>
      println("Skip me.")
  }

  private def processRecord(key: String, value: String): Future[Done] = {
    //println(" > Processing: " + value)
    convertStrToMeetup(value) match {
      case Some(meetup) => storeMeetup(meetup)
      case None =>
    }
    Future.successful(Done.done())
  }

  private def convertStrToMeetup(jsonString: String): Option[Meetup] = {
    try {
      val json = parse(jsonString)
      implicit val formats = DefaultFormats
      val meetup = json.extract[Meetup]
      Some(meetup)
    } catch {
      case e: Throwable => None
    }
  }

  private def storeMeetup(meetup: Meetup): Unit = {
    val key = meetup.group_city + "/" + meetup.group_country
    val value: NumberOfMeetups = this.cache.getOrElse(key, (0, meetup))._1 + 1
    this.cache.put(key, (value, meetup))
    val greater = top10.filter(x => x._2 > value)
    if (greater.length != 10) {
      top10 = top10.toMap.+((key, value)).toList.sortWith((e1, e2) => e1._2 > e2._2).take(10)
      cacheSent = false
      val meetupJsonString = write(this.top10.toMap)(Serialization.formats(NoTypeHints))
      println(" > Current Top10: " + meetupJsonString) // TODO remove me - kept for showcasing
    }
  }

  private def getJsonResponse: String = {
    try {
      val data = this.top10.map { case (city, _) =>
        val (n, m) = this.cache.get(city).get
        MeetupResponse(m.group_city, m.group_country, m.group_lat, m.group_lon, n)
      }
      val meetupJsonString = write(data)(Serialization.formats(NoTypeHints))
      //println(" > Sending: " + meetupJsonString)
      meetupJsonString
    } catch {
      case e: Throwable =>
        println(e)
        ""
    }
  }

}
