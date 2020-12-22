package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import org.github.felipegutierrez.explore.akka.classic.http.lowlevel.{Guitar, GuitarDB, GuitarStoreJsonProtocol}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._

object GuitarRestHighLevelApi extends GuitarStoreJsonProtocol {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    println("http GET localhost:8080/api/guitar")
    println("http GET localhost:8080/api/guitar?id=2")
    println("http GET localhost:8080/api/guitar?id=10")
    println("http GET localhost:8080/api/guitar/inventory?inStock=false|true")
    println("http POST localhost:8080/api/guitar < src/main/resources/json/guitar.json")
    println("http POST \"localhost:8080/api/guitar/inventory?id=1&quantity=3\"")
    implicit val system = ActorSystem("GuitarRestHighLevelApi")
    import GuitarDB._
    import system.dispatcher
    val guitarDbActor = system.actorOf(Props[GuitarDB], "LowLevelGuitarDB")
    val guitarList = List(
      Guitar("Fender", "Stratocaster"),
      Guitar("Gibson", "Les Paul"),
      Guitar("Martin", "LX1")
    )
    guitarList.foreach { guitar =>
      guitarDbActor ! CreateGuitar(guitar)
    }

    implicit val defaultTimeout = Timeout(2 seconds)

    val guitarServerRoutes =
      pathPrefix("api" / "guitar") {
        (path(IntNumber) | parameter('id.as[Int])) { (guitarId: Int) => {
          get {
            // println(s"I found the guitar $guitarId")
            val guitarsFuture: Future[Option[Guitar]] = (guitarDbActor ? FindGuitar(guitarId)).mapTo[Option[Guitar]]
            val entityFuture = guitarsFuture.map {
              case None => HttpResponse(StatusCodes.NotFound)
              case Some(guitar) =>
                HttpResponse(
                  entity = HttpEntity(
                    ContentTypes.`application/json`,
                    guitar.toJson.prettyPrint
                  )
                )
            }
            complete(entityFuture)
          }
        }
        } ~ get {
          val guitarsFuture: Future[List[Guitar]] = (guitarDbActor ? FindAllGuitars).mapTo[List[Guitar]]
          val entityFuture = guitarsFuture.map { guitars =>
            HttpResponse(
              entity = HttpEntity(
                ContentTypes.`application/json`,
                guitars.toJson.prettyPrint
              )
            )
          }
          complete(entityFuture)
        }
      }
    Http()
      .newServerAt("localhost", 8080)
      // .enableHttps(HttpsServerContext.httpsConnectionContext)
      .bindFlow(guitarServerRoutes)
  }
}
