package org.github.felipegutierrez.explore.akka.classic.http.lowlevel

import akka.actor.{Actor, ActorLogging, ActorSystem}
import spray.json._

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
