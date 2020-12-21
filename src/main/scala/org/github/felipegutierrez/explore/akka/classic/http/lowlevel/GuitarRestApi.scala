package org.github.felipegutierrez.explore.akka.classic.http.lowlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.pattern.ask
import akka.util.Timeout
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._

trait GuitarStoreJsonProtocol extends DefaultJsonProtocol {
  implicit val guitarFormat = jsonFormat2(Guitar)
}

object GuitarRestApi extends GuitarStoreJsonProtocol {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("GuitarRestApi")
    /**
     * GET on localhost:8080/api/guitar => all the guitars in the store
     * POST on localhost:8080/api/guitar => insert the guitar into the store
     */
    // JSON -> marshalling
    val simpleGuitar = Guitar("Fender", "Stratocaster")
    println(simpleGuitar.toJson.prettyPrint)
    // unmarshalling JSON
    val simpleGuitarJsonString =
      """
        |{
        |  "make": "Fender",
        |  "model": "Stratocaster"
        |}
        |""".stripMargin
    println(simpleGuitarJsonString.parseJson.convertTo[Guitar])

    import GuitarDB._
    val guitarDb = system.actorOf(Props[GuitarDB], "LowLevelGuitarDB")
    val guitarList = List(
      Guitar("Fender", "Stratocaster"),
      Guitar("Gibson", "Les Paul"),
      Guitar("Martin", "LX1")
    )
    guitarList.foreach { guitar =>
      guitarDb ! CreateGuitar(guitar)
    }
    implicit val defaultTimeout = Timeout(2 seconds)
    import system.dispatcher
    val asyncRequestHandler: HttpRequest => Future[HttpResponse] = {
      case HttpRequest(HttpMethods.GET, Uri.Path("/api/guitar"), headers, entity, protocol) =>
        val guitarsFuture: Future[List[Guitar]] = (guitarDb ? FindAllGuitars).mapTo[List[Guitar]]
        guitarsFuture.map { guitars =>
          HttpResponse(
            entity = HttpEntity(
              ContentTypes.`application/json`,
              guitars.toJson.prettyPrint
            )
          )
        }
      case request: HttpRequest =>
        request.discardEntityBytes()
        Future(HttpResponse(StatusCodes.NotFound))
    }
    Http().newServerAt("localhost", 8080).bind(asyncRequestHandler)
  }
}

case class Guitar(make: String, model: String)

object GuitarDB {

  case class CreateGuitar(guitar: Guitar)

  case class GuitarCreated(id: Int)

  case class FindGuitar(id: Int)

  case object FindAllGuitars

}

class GuitarDB extends Actor with ActorLogging {

  import GuitarDB._

  var guitars: Map[Int, Guitar] = Map()
  var currentGuitarId: Int = 0

  override def receive: Receive = {
    case FindAllGuitars =>
      log.info(s"searching for all guitars")
      sender() ! guitars.values.toList
    case FindGuitar(id) =>
      log.info(s"searching guitar by id $id")
      sender() ! guitars.get(id)
    case CreateGuitar(guitar) =>
      log.info(s"adding guitar $guitar with id $currentGuitarId")
      guitars = guitars + (currentGuitarId -> guitar)
      sender() ! GuitarCreated(currentGuitarId)
      currentGuitarId += 1
  }
}
