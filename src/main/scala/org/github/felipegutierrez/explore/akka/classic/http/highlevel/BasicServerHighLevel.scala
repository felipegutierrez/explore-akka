package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object BasicServerHighLevel {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("BasicServerHighLevel")
    // import system.dispatcher

    val simpleRoute: Route =
      path("status") { // Directive
        get {
          complete(StatusCodes.OK) // Directive
        } ~ post {
          complete(StatusCodes.Forbidden)
        }
      } ~ path("home") {
        get {
          complete(HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>hello from Akka HTTP high level API</body>
              |</html>
              |""".stripMargin
          ))
        } ~ post {
          complete(StatusCodes.Forbidden)
        }
      } ~ path("api" / "guitars") {
        get {
          complete(HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>here are my guitars!</body>
              |</html>
              |""".stripMargin
          ))
        } ~ post {
          complete(StatusCodes.OK)
        }
      } ~ path("api" / "guitar" / IntNumber) { (itemNumber: Int) =>
        get {
          println(s"I found the guitar $itemNumber")
          complete(HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            s"""
               |<html>
               | <body>I found the guitar $itemNumber!</body>
               |</html>
               |""".stripMargin
          ))
        }
      } ~ path("api" / "guitar" / IntNumber / IntNumber) { (itemNumber: Int, qtd: Int) =>
        get {
          println(s"I found $qtd guitars with id $itemNumber")
          complete(HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            s"""
               |<html>
               | <body>I found $qtd guitars with id $itemNumber</body>
               |</html>
               |""".stripMargin
          ))
        }
      } ~ pathEndOrSingleSlash {
        complete(StatusCodes.OK)
      }

    println("try:")
    println("http GET localhost:8080/status")
    println("http POST localhost:8080/status")
    println("http GET localhost:8080/home")
    println("http POST localhost:8080/home")
    println("http GET localhost:8080/api/guitars")
    println("http POST localhost:8080/api/guitars")
    println("http GET localhost:8080/api/guitar/42")
    println("http GET localhost:8080/api/guitar/42/5")
    Http()
      .newServerAt("localhost", 8080)
      // .enableHttps(HttpsServerContext.httpsConnectionContext)
      .bindFlow(simpleRoute)
  }
}

