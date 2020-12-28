package org.github.felipegutierrez.explore.akka.classic.http.server.lowlevel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.IncomingConnection
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.stream.scaladsl.{Flow, Sink}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object BasicServerLowLevel {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("BasicServerLowLevel")
    import system.dispatcher
    println(s"type on the console: 'curl localhost:8888'")
    val serverSource = Http().newServerAt("localhost", 8888).connectionSource()
    val connectionSink = Sink.foreach[IncomingConnection] { connection =>
      println(s"Accepted incoming connection from: ${connection.remoteAddress}")
    }
    val serverBindingFuture = serverSource.to(connectionSink).run()
    serverBindingFuture.onComplete {
      case Success(binding) =>
        println(s"server binding successful")
        binding.terminate(2 seconds)
      case Failure(ex) => println(s"server binding failed: $ex")
    }

    /** Method 1: synchronously serve HTTP responses */
    val requestHandler: HttpRequest => HttpResponse = {
      case HttpRequest(HttpMethods.GET, uri, headers, entity, protocol) =>
        HttpResponse(
          StatusCodes.OK, // HTTP 200
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  Hello Akka HTTP
              | </body>
              |</html>
              |""".stripMargin)
        )
      case request: HttpRequest =>
        request.discardEntityBytes()
        HttpResponse(
          StatusCodes.NotFound, // HTTP 404
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  OOPS! page not found =(
              | </body>
              |</html>
              |""".stripMargin)
        )
    }
    val httpSyncConnectionHandler = Sink.foreach[IncomingConnection] { connection =>
      connection.handleWithSyncHandler(requestHandler)
    }
    println(s"call on the console: 'http GET localhost:8080'")
    // manual version >>>
    // Http().newServerAt("localhost", 8080).connectionSource().runWith(httpSyncConnectionHandler)
    // short version >>>
    Http().newServerAt("localhost", 8080).bindSync(requestHandler)

    /** Method 2: serve back HTTP response ASYNCHRONOUSLY */
    val asyncRequestHandler: HttpRequest => Future[HttpResponse] = {
      case HttpRequest(HttpMethods.GET, Uri.Path("/home"), headers, entity, protocol) =>
        Future(HttpResponse(
          StatusCodes.OK, // HTTP 200
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  Async Hello Akka HTTP
              | </body>
              |</html>
              |""".stripMargin)
        ))
      case HttpRequest(HttpMethods.GET, Uri.Path("/redirect"), headers, entity, protocol) =>
        Future(HttpResponse(
          StatusCodes.Found,
          headers = List(Location("http://www.google.com"))
        ))
      case request: HttpRequest =>
        request.discardEntityBytes()
        Future(HttpResponse(
          StatusCodes.NotFound, // HTTP 404
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  OOPS! async page not found =(<br>try http://localhost:8081/home
              | </body>
              |</html>
              |""".stripMargin)
        ))
    }
    val httpAsyncConnectionHandler = Sink.foreach[IncomingConnection] { connection =>
      connection.handleWithAsyncHandler(asyncRequestHandler)
    }
    println(s"call on the console: 'http GET localhost:8081'")
    // manual version >>>
    // Http().newServerAt("localhost", 8081).connectionSource().runWith(httpAsyncConnectionHandler)
    // short version >>>
    Http().newServerAt("localhost", 8081).bind(asyncRequestHandler)

    /** Method 3: async via Akka streams */
    val streamRequestHandler: Flow[HttpRequest, HttpResponse, _] = Flow[HttpRequest].map {
      case HttpRequest(HttpMethods.GET, Uri.Path("/home"), headers, entity, protocol) =>
        HttpResponse(
          StatusCodes.OK, // HTTP 200
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  Async Hello Akka HTTP
              | </body>
              |</html>
              |""".stripMargin)
        )
      case request: HttpRequest =>
        request.discardEntityBytes()
        HttpResponse(
          StatusCodes.NotFound, // HTTP 404
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  OOPS! async page not found =(<br>try http://localhost:8082/home
              | </body>
              |</html>
              |""".stripMargin)
        )
    }

    println(s"call on the console: 'http GET localhost:8082'")
    // manual version >>>
    //    Http().newServerAt("localhost", 8082).connectionSource().runForeach { connection =>
    //      connection.handleWith(streamRequestHandler)
    //    }
    // short version >>>
    Http().newServerAt("localhost", 8082).bindFlow(streamRequestHandler)
  }
}
