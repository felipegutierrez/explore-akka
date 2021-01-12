package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel

import akka.actor.{Actor, ActorLogging, ActorPath, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

object BasicRoundRobinHttpServer {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    implicit val system = ActorSystem("BasicRoundRobinHttpServer")
    import system.dispatcher
    implicit val timeout = Timeout(3 seconds)

    val myServiceActor = system.actorOf(RoundRobinPool(5).props(Props[MyService]), "myServiceActor")
    //    for (i <- 1 to 10) {
    //      myServiceActor ! s"[$i] hello from a programmatically round-robin router Actor!"
    //    }

    val simpleRoute: Route =
      (path("reference") & get) {
        val validResponseFuture: Option[Future[HttpResponse]] = {
          // construct the HTTP response
          val actorPathResponse: Future[Option[ActorPath]] = (myServiceActor ? "reference").mapTo[Option[ActorPath]]
          Option(actorPathResponse.map(ref => HttpResponse(
            StatusCodes.OK,
            entity = HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              s"""
                 |<html>
                 | <body>I got the actor reference: ${ref} </body>
                 |</html>
                 |""".stripMargin
            ))))
        }
        val entityFuture: Future[HttpResponse] = validResponseFuture.getOrElse(Future(HttpResponse(StatusCodes.BadRequest)))
        complete(entityFuture)
      }
    println("http GET localhost:8080/reference")
    Http().newServerAt("localhost", 8080).bind(simpleRoute)
  }
}

class MyService extends Actor with ActorLogging {
  override def receive: Receive = {
    case "reference" =>
      log.info(s"request reference at actor: ${self}")
      sender() ! Option(self.path)
    case message =>
      log.info(s"unknown message: ${message.toString}")
  }
}
