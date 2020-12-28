package org.github.felipegutierrez.explore.akka.classic.http.server.lowlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.pattern.ask
import akka.util.Timeout
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._

trait GuitarStoreJsonProtocol extends DefaultJsonProtocol {
  implicit val guitarFormat = jsonFormat3(Guitar)
}

object GuitarRestApi extends GuitarStoreJsonProtocol {
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
    implicit val system = ActorSystem("GuitarRestLowLevelApi")
    import system.dispatcher
    /**
     * GET on localhost:8080/api/guitar => all the guitars in the store
     * GET on localhost:8080/api/guitar?id=X => fetches the guitar associated with id X
     * POST on localhost:8080/api/guitar => insert the guitar into the store
     * GET to /api/guitar/inventory?inStock=true/false which returns the guitars in stock as a JSON
     * POST to /api/guitar/inventory?id=X&quantity=Y which adds Y guitars to the stock for guitar with id X
     */
    // JSON -> marshalling
    val simpleGuitar = Guitar("Fender", "Stratocaster")
    println(simpleGuitar.toJson.prettyPrint)
    // unmarshalling JSON
    val simpleGuitarJsonString =
      """
        |{
        |  "make": "Fender",
        |  "model": "Stratocaster",
        |  "quantity": 3
        |}
        |""".stripMargin
    println(simpleGuitarJsonString.parseJson.convertTo[Guitar])

    import GuitarDB._
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

    def getGuitars(): Future[HttpResponse] = {
      val guitarsFuture: Future[List[Guitar]] = (guitarDbActor ? FindAllGuitars).mapTo[List[Guitar]]
      guitarsFuture.map { guitars =>
        HttpResponse(
          entity = HttpEntity(
            ContentTypes.`application/json`,
            guitars.toJson.prettyPrint
          )
        )
      }
    }

    def getGuitar(query: Query): Future[HttpResponse] = {
      val guitarId = query.get("id").map(_.toInt) // Option[Int]
      guitarId match {
        case None => Future(HttpResponse(StatusCodes.NotFound)) // /api/guitar?id=
        case Some(id: Int) =>
          val guitarsFuture: Future[Option[Guitar]] = (guitarDbActor ? FindGuitar(id)).mapTo[Option[Guitar]]
          guitarsFuture.map {
            case None => HttpResponse(StatusCodes.NotFound)
            case Some(guitar) =>
              HttpResponse(
                entity = HttpEntity(
                  ContentTypes.`application/json`,
                  guitar.toJson.prettyPrint
                )
              )
          }
      }
    }

    def getGuitarsInStock(query: Query): Future[HttpResponse] = {
      val inStock = query.get("inStock").map(_.toBoolean) // Option[Boolean]
      inStock match {
        case None => Future(HttpResponse(StatusCodes.BadRequest))
        case Some(inStock: Boolean) =>
          val guitarsFuture: Future[List[Guitar]] = (guitarDbActor ? FindGuitarsInStock(inStock)).mapTo[List[Guitar]]
          guitarsFuture.map { guitars =>
            HttpResponse(
              entity = HttpEntity(
                ContentTypes.`application/json`,
                guitars.toJson.prettyPrint
              )
            )
          }
      }
    }

    val asyncRequestHandler: HttpRequest => Future[HttpResponse] = {
      case HttpRequest(HttpMethods.GET, uri@Uri.Path("/api/guitar"), headers, entity, protocol) =>
        if (uri.query().isEmpty) {
          getGuitars() // all guitars
        } else {
          getGuitar(uri.query()) // get the guitar id
        }
      case HttpRequest(HttpMethods.GET, uri@Uri.Path("/api/guitar/inventory"), headers, entity, protocol) =>
        if (uri.query().isEmpty) {
          getGuitars() // all guitars
        } else {
          getGuitarsInStock(uri.query())
        }
      case HttpRequest(HttpMethods.POST, uri@Uri.Path("/api/guitar/inventory"), headers, entity, protocol) =>
        val query = uri.query()
        val guitarId: Option[Int] = query.get("id").map(_.toInt)
        val guitarQuantity: Option[Int] = query.get("quantity").map(_.toInt)

        val validGuitarResponseFuture: Option[Future[HttpResponse]] = for {
          id <- guitarId
          quantity <- guitarQuantity
        } yield {
          // construct the HTTP response
          val newGuitarFuture: Future[Option[Guitar]] = (guitarDbActor ? AddQuantity(id, quantity)).mapTo[Option[Guitar]]
          newGuitarFuture.map(_ => HttpResponse(StatusCodes.OK))
        }
        validGuitarResponseFuture.getOrElse(Future(HttpResponse(StatusCodes.BadRequest)))
      case HttpRequest(HttpMethods.POST, uri, headers, entity, protocol) =>
        val strictEntityFuture: Future[HttpEntity.Strict] = entity.toStrict(3 seconds)
        strictEntityFuture.flatMap { strictEntity =>
          val guitarJsonString: String = strictEntity.data.utf8String
          val guitar: Guitar = guitarJsonString.parseJson.convertTo[Guitar]
          val guitarCreatedFuture: Future[GuitarCreated] = (guitarDbActor ? CreateGuitar(guitar)).mapTo[GuitarCreated]
          guitarCreatedFuture.map { msg =>
            HttpResponse(StatusCodes.OK)
          }
        }
      case request: HttpRequest =>
        request.discardEntityBytes()
        Future(HttpResponse(StatusCodes.NotFound))
    }
    Http().newServerAt("localhost", 8080).bind(asyncRequestHandler)
  }
}

case class Guitar(make: String, model: String, quantity: Int = 0)

object GuitarDB {

  case class CreateGuitar(guitar: Guitar)

  case class GuitarCreated(id: Int)

  case class FindGuitar(id: Int)

  case class AddQuantity(id: Int, quantity: Int)

  case class FindGuitarsInStock(inStock: Boolean)

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
    case FindGuitarsInStock(inStock) =>
      log.info(s"searching guitars in stock = $inStock")
      if (inStock) {
        sender() ! guitars.values.filter(guitar => guitar.quantity > 0)
      } else {
        sender() ! guitars.values.filter(guitar => guitar.quantity == 0)
      }
    case AddQuantity(id, qtd) =>
      log.info(s"adding qtd $qtd for guitar id $id")
      val guitar: Option[Guitar] = guitars.get(id)
      val newGuitar = guitar.map {
        case Guitar(make, model, q) => Guitar(make, model, q + qtd)
      }
      newGuitar.foreach(guitar => guitars = guitars + (id -> guitar))
      sender() ! newGuitar
  }
}
