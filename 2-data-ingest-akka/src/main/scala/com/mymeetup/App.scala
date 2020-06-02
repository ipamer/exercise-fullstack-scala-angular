package com.mymeetup


import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.WebSocketRequest

import scala.language.postfixOps


object App {

  def main(args: Array[String]): Unit = {

    // TODO externalize
    val sourceHost: String = "ws://stream.meetup.com/2/rsvps"

    implicit val system: ActorSystem = IngestActorSystem.system

    /** **************************************************
     * Start reading
     */
    val (upgradeResponse, promise) =
      Http().singleWebSocketRequest(
        WebSocketRequest(sourceHost),
        AppFlow.mainFlow
      )

    import system.dispatcher
    val connected = upgradeResponse.map { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        Done
      } else {
        throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
      }
    }
    connected.onComplete(println)
  }

}
