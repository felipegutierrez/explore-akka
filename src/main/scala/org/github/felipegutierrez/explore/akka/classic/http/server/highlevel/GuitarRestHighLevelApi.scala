package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{parameter, _}
import akka.pattern.ask
import akka.util.Timeout
import org.github.felipegutierrez.explore.akka.classic.http.server.lowlevel.{Guitar, GuitarDB, GuitarStoreJsonProtocol}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._

object GuitarRestHighLevelApi extends GuitarStoreJsonProtocol with SprayJsonSupport {
  implicit val system = ActorSystem("GuitarRestHighLevelApi")
  implicit val defaultTimeout = Timeout(2 seconds)

  import system.dispatcher
  import GuitarDB._

  val guitarDbActor = system.actorOf(Props[GuitarDB], "GuitarRestHighLevelApi")

  val guitarList = List(
    Guitar("Fender", "Stratocaster"),
    Guitar("Gibson", "Les Paul"),
    Guitar("Martin", "LX1")
  )
  val guitarServerRoutes =
    (pathPrefix("api" / "guitar") & get) {
      (path(IntNumber) | parameter('id.as[Int])) { (guitarId: Int) =>
        val entityFuture: Future[HttpEntity.Strict] = (guitarDbActor ? FindGuitar(guitarId))
          .mapTo[Option[Guitar]]
          .map(_.toJson.prettyPrint)
          .map(toHttpEntity)
        complete(entityFuture)
      } ~ (path("inventory") & parameter('inStock.as[Boolean])) { inStock =>
        val entityFuture: Future[HttpEntity.Strict] = (guitarDbActor ? FindGuitarsInStock(inStock))
          .mapTo[List[Guitar]]
          .map(_.toJson.prettyPrint)
          .map(toHttpEntity)
        complete(entityFuture)
      } ~ pathEndOrSingleSlash {
        val entityFuture: Future[HttpEntity.Strict] = (guitarDbActor ? FindAllGuitars)
          .mapTo[List[Guitar]]
          .map(_.toJson.prettyPrint)
          .map(toHttpEntity)
        complete(entityFuture)
      }
    } ~ (pathPrefix("api" / "guitar" / "inventory") & post) {
      (parameter('id.as[Option[Int]]) & parameter('quantity.as[Option[Int]])) { (guitarId, guitarQuantity) =>
        val validGuitarResponseFuture: Option[Future[HttpResponse]] = for {
          id <- guitarId
          quantity <- guitarQuantity
        } yield {
          // construct the HTTP response
          val newGuitarFuture: Future[Option[Guitar]] = (guitarDbActor ? AddQuantity(id, quantity)).mapTo[Option[Guitar]]
          newGuitarFuture.map(_ => HttpResponse(StatusCodes.OK))
        }
        val entityFuture: Future[HttpResponse] = validGuitarResponseFuture.getOrElse(Future(HttpResponse(StatusCodes.BadRequest)))
        complete(entityFuture)
      }
    }
  guitarList.foreach { guitar =>
    guitarDbActor ! CreateGuitar(guitar)
  }

//  def main(args: Array[String]): Unit = {
//    run()
//  }

  def run() = {
    println("http GET localhost:8080/api/guitar")
    println("http GET localhost:8080/api/guitar?id=2")
    println("http GET localhost:8080/api/guitar?id=10")
    println("http GET localhost:8080/api/guitar/inventory?inStock=false|true")
    println("http POST localhost:8080/api/guitar < src/main/resources/json/guitar.json")
    println("http POST \"localhost:8080/api/guitar/inventory?id=1&quantity=3\"")
    Http()
      .newServerAt("localhost", 8080)
      // .enableHttps(HttpsServerContext.httpsConnectionContext)
      .bindFlow(guitarServerRoutes)
  }

  def toHttpEntity(payload: String) = HttpEntity(ContentTypes.`application/json`, payload)
}
