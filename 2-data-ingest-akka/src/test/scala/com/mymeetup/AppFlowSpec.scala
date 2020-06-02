package com.mymeetup

import net.liftweb.json.Serialization.write
import net.liftweb.json.{NoTypeHints, Serialization}
import org.junit.runner.RunWith
import org.scalatest.WordSpecLike
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatestplus.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class AppFlowSpec extends WordSpecLike {

  "getMeetupJsonFromInput" must {
    "process - null" in {
      val result = AppFlow.getMeetupJsonFromInput(null)
      assert(result.isEmpty)
    }
    "process - empty" in {
      val result = AppFlow.getMeetupJsonFromInput("")
      assert(result.isEmpty)
    }
    "process - real example" in {
      val str = """{"venue":{"venue_name":"Datong High School","lon":0,"lat":0,"venue_id":23779799},"visibility":"public","response":"no","guests":0,"member":{"member_id":120119272,"photo":"http:\/\/photos3.meetupstatic.com\/photos\/member\/b\/2\/b\/c\/thumb_262125756.jpeg","member_name":"Allen Wang"},"rsvp_id":1658733801,"mtime":1489925470960,"event":{"event_name":"Play Intermediate Volleyball","event_id":"jkpwmlywgbmb","time":1491613200000,"event_url":"https:\/\/www.meetup.com\/Taipei-Sports-and-Social-Club\/events\/236786445\/"},"group":{"group_topics":[{"urlkey":"fitness","topic_name":"Fitness"},{"urlkey":"mountain-biking","topic_name":"Mountain Biking"},{"urlkey":"sports","topic_name":"Sports and Recreation"},{"urlkey":"outdoors","topic_name":"Outdoors"},{"urlkey":"fun-times","topic_name":"Fun Times"},{"urlkey":"winter-and-summer-sports","topic_name":"Winter and Summer Sports"},{"urlkey":"adventure","topic_name":"Adventure"},{"urlkey":"water-sports","topic_name":"Water Sports"},{"urlkey":"sports-and-socials","topic_name":"Sports and Socials"},{"urlkey":"hiking","topic_name":"Hiking"},{"urlkey":"excercise","topic_name":"Exercise"},{"urlkey":"recreational-sports","topic_name":"Recreational Sports"}],"group_city":"Taipei","group_country":"tw","group_id":16585312,"group_name":"Taipei Sports and Social Club","group_lon":121.45,"group_urlname":"Taipei-Sports-and-Social-Club","group_lat":25.02}}"""
      val result = AppFlow.getMeetupJsonFromInput(str)

      val meetup = Meetup(
        group_city = "Taipei",
        group_country = "tw",
        group_lat = 25.02,
        group_lon = 121.45,
      )
      val meetupJsonString = write(meetup)(Serialization.formats(NoTypeHints))

      assert(result.isDefined)
      assert(result.get == meetupJsonString)
    }
    "skip where any data is missing or broken" in {
      val missing =
        Table(
          ("str", "reason"),
          ("""{"venue:{"venue_name":"Datong High School","lon":0,"lat":0,"venue_id":23779799},"visibility":"public","response":"no","guests":0,"member":{"member_id":120119272,"photo":"http:\/\/photos3.meetupstatic.com\/photos\/member\/b\/2\/b\/c\/thumb_262125756.jpeg","member_name":"Allen Wang"},"rsvp_id":1658733801,"mtime":1489925470960,"event":{"event_name":"Play Intermediate Volleyball","event_id":"jkpwmlywgbmb","time":1491613200000,"event_url":"https:\/\/www.meetup.com\/Taipei-Sports-and-Social-Club\/events\/236786445\/"},"group":{"group_topics":[{"urlkey":"fitness","topic_name":"Fitness"},{"urlkey":"mountain-biking","topic_name":"Mountain Biking"},{"urlkey":"sports","topic_name":"Sports and Recreation"},{"urlkey":"outdoors","topic_name":"Outdoors"},{"urlkey":"fun-times","topic_name":"Fun Times"},{"urlkey":"winter-and-summer-sports","topic_name":"Winter and Summer Sports"},{"urlkey":"adventure","topic_name":"Adventure"},{"urlkey":"water-sports","topic_name":"Water Sports"},{"urlkey":"sports-and-socials","topic_name":"Sports and Socials"},{"urlkey":"hiking","topic_name":"Hiking"},{"urlkey":"excercise","topic_name":"Exercise"},{"urlkey":"recreational-sports","topic_name":"Recreational Sports"}],"group_city":"Taipei","group_country":"tw","group_id":16585312,"group_name":"Taipei Sports and Social Club","group_lon":121.45,"group_urlname":"Taipei-Sports-and-Social-Club","group_lat":25.02}}""",
            "broken json - venue has no closing quotes"),
          ("""{"group":{"group_city":"Taipei","group_country":"tw","group_id":16585312,"group_name":"Taipei Sports and Social Club","group_lon":121.45,"group_urlname":"Taipei-Sports-and-Social-Club"}}""", "lat missing"),
          ("""{"group":{"group_city":"Taipei","group_country":"tw","group_id":16585312,"group_name":"Taipei Sports and Social Club","group_urlname":"Taipei-Sports-and-Social-Club","group_lat":25.02}}""", "lon missing"),
          ("""{"group":{"group_city":"Taipei","group_id":16585312,"group_name":"Taipei Sports and Social Club","group_lon":121.45,"group_urlname":"Taipei-Sports-and-Social-Club","group_lat":25.02}}""", "country missing"),
          ("""{"group":{"group_country":"tw","group_id":16585312,"group_name":"Taipei Sports and Social Club","group_lon":121.45,"group_urlname":"Taipei-Sports-and-Social-Club","group_lat":25.02}}""", "city missing"),
        )
      forAll(missing) { (str, reason) =>
        val result = AppFlow.getMeetupJsonFromInput(str)
        assert(result.isEmpty)
      }

    }

  }

}
