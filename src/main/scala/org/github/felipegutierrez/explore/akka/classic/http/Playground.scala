package org.github.felipegutierrez.explore.akka.classic.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.io.StdIn

object Playground {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("AkkaHttpPlayground")
    import system.dispatcher

    val simpleRoute: Route =
      pathEndOrSingleSlash {
        complete(HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   Hello world in Akka HTTP!
            | </body>
            |</html>
        """.stripMargin
        ))
      }
    
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(simpleRoute)
    // wait for a new line, then terminate the server
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

    val l = List(2).flatMap(Seq(_))
  }
}
