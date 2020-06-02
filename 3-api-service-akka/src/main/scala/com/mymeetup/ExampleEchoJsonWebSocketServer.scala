package com.mymeetup

import java.util.concurrent.Semaphore

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.ws.{Message, TextMessage, UpgradeToWebSocket}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.scaladsl.Flow

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps


object ExampleEchoJsonWebSocketServer {

  def main(args: Array[String]): Unit = {

    val hostAddress = "localhost"
    val hostPort = 8080

    implicit val system = ActorSystem()
    //implicit val materializer = ActorMaterializer()

    //val createActorFlow: Flow[String, String, Any] = {
    //  // Set Up Actors as source and sink (not shown)
    //  Flow.fromSinkAndSource(in, out)
    //}

    //#websocket-handler
    // The Greeter WebSocket Service expects a "name" per message and
    // returns a greeting message for that name
    import system.dispatcher
    val greeterWebSocketService: Flow[Message, Message, Any] = Flow[Message]
      .collect {
        case TextMessage.Strict(msg) => Future.successful(msg)
        case TextMessage.Streamed(stream) => stream
          .limit(100) // Max frames we are willing to wait for
          .completionTimeout(5 seconds) // Max time until last frame
          .runFold("")(_ + _) // Merges the frames
          .flatMap(msg => Future.successful(msg))
      }
      .mapAsync(parallelism = 3)(identity)
      //.via(createActorFlow())
      .map {
        case msg: String => TextMessage.Strict(s"""{"name":$msg}""")
      }

    val requestHandler: HttpRequest => HttpResponse = {
      case req@HttpRequest(GET, Uri.Path("/greeter"), _, _, _) =>
        req.header[UpgradeToWebSocket] match {
          case Some(upgrade) => upgrade.handleMessages(greeterWebSocketService)
          case None => HttpResponse(400, entity = "Not a valid websocket request!")
        }
      case r: HttpRequest =>
        r.discardEntityBytes() // important to drain incoming HTTP Entity stream
        HttpResponse(404, entity = "Unknown resource!")
    }

    // - Custom server serrings for keepalive:
    //val defaultSettings = ServerSettings(system)
    //val pingCounter = new AtomicInteger()
    //val customWebsocketSettings = defaultSettings.websocketSettings
    //    .withPeriodicKeepAliveData(() => ByteString(s"debug-${pingCounter.incrementAndGet()}"))
    //val customServerSettings = defaultSettings.withWebsocketSettings(customWebsocketSettings)
    // Then use it: Http().bindAndHandleSync(...  , settings = customServerSettings)

    // - Start serving
    val bindingFuture = Http().bindAndHandleSync(requestHandler, interface = hostAddress, port = hostPort)
    println(s"Server online at http://$hostAddress:$hostPort/\nPress CTRL+C to stop...")

    // - Wait for termination
    val semaphore = new Semaphore(0)
    semaphore.acquire(1)

    // - Graceful shutdown
    import system.dispatcher // for the future transformations
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  }

}
