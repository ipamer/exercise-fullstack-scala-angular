package com.example

import akka.Done
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.mymeetup.{MeetupActor, MeetupDataSource, ReadResults, StartCalculating}
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatestplus.junit.JUnitRunner

import scala.concurrent.Future


@RunWith(classOf[JUnitRunner])
class MeetupActorSpec extends TestKit(ActorSystem("MyMeetupSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  "MeetupActor" must {

    "[positive] read aggregated results" in {
      val probe = TestProbe()
      val underTest = system.actorOf(Props(new MeetupActor(new TestDataSource(
        records = List(
          """{"group_city":"London","group_country":"uk","group_lat":40.32,"group_lon":91.45}""",
          """{"group_city":"Taipei","group_country":"tw","group_lat":30.32,"group_lon":121.45}""",
        ),
        repeat = 3,
      ))))

      underTest.tell(StartCalculating, probe.ref)
      underTest.tell(ReadResults, probe.ref)
      val response = probe.expectMsgType[String]

      response shouldBe """[{"group_city":"London","group_country":"uk","group_lat":40.32,"group_lon":91.45,"numberOfMeetups":3},{"group_city":"Taipei","group_country":"tw","group_lat":30.32,"group_lon":121.45,"numberOfMeetups":3}]"""
    }

    "[negative] expect no results on broken records" in {
      val probe = TestProbe()
      val underTest = system.actorOf(Props(new MeetupActor(new TestDataSource(
        records = List("""{"abc_country":"tw","group_lat":30.32,"group_lon":121.45}"""),
        repeat = 3,
      ))))

      underTest.tell(StartCalculating, probe.ref)
      underTest.tell(ReadResults, probe.ref)
      val response = probe.expectMsgType[String]

      response shouldBe """[]"""
    }

  }

}

class TestDataSource(records: List[String], repeat: Int) extends MeetupDataSource {
  override def start(processRecord: (String, String) => Future[Done]): Unit = {
    (1 to repeat).foreach(x => {
      records.foreach(record => {
        processRecord("", record)
      })
    })
  }
}
