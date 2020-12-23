package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Directives.{parameter, _}
import akka.pattern.ask
import akka.util.Timeout
import org.github.felipegutierrez.explore.akka.classic.http.highlevel.PersonDomain.CreatePerson
import spray.json.{DefaultJsonProtocol, _}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Exercise:
 *
 * - GET /api/people: retrieve ALL the people you have registered
 * - GET /api/people/pin: retrieve the person with that PIN, return as JSON
 * - GET /api/people?pin=X (same)
 * - (harder) POST /api/people with a JSON payload denoting a Person, add that person to your database
 *   - extract the HTTP request's payload (entity)
 *     - extract the request
 *     - process the entity's data
 */

case class Person(pin: Int, name: String)

trait PersonJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat2(Person)
}

object PersonDomain {

  case class CreatePerson(person: Person)

  case class PersonCreated(pin: Int)

  case class FindPerson(pin: Int)

  case object FindAllPeople

}

class PersonActor extends Actor with ActorLogging {

  import PersonDomain._

  var people: Map[Int, Person] = Map()

  override def receive: Receive = {
    case CreatePerson(person) =>
      log.info(s"adding person $person")
      people = people + (person.pin -> person)
      sender() ! PersonCreated(person.pin)
    case FindPerson(pin) =>
      log.info(s"searching for person $pin")
      sender() ! people.get(pin)
    case FindAllPeople =>
      log.info(s"searching for all people")
      sender() ! people.values.toList
  }
}

object PersonRestApi extends PersonJsonProtocol {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("PersonRestApi")
    import system.dispatcher

    val personActor = system.actorOf(Props[PersonActor], "PersonActor")
    List(Person(1, "Alice"), Person(2, "Bob"), Person(3, "Charlie"))
      .foreach { p =>
        personActor ! CreatePerson(p)
      }

    def toHttpEntity(payload: String) = HttpEntity(ContentTypes.`application/json`, payload)

    implicit val defaultTimeout = Timeout(2 seconds)
    import PersonDomain._
    val personRoutes =
      (pathPrefix("api" / "people")) {
        get {
          (path(IntNumber) | parameter('pin.as[Int])) { pin: Int =>
            println(s"retrieve the person with that PIN $pin")
            val entityFuture: Future[HttpEntity.Strict] = (personActor ? FindPerson(pin))
              .mapTo[Option[Person]]
              .map(_.toJson.prettyPrint)
              .map(toHttpEntity)
            complete(entityFuture)
          } ~ pathEndOrSingleSlash {
            println(s"retrieve all people")
            val entityFuture: Future[HttpEntity.Strict] = (personActor ? FindAllPeople)
              .mapTo[List[Person]]
              .map(_.toJson.prettyPrint)
              .map(toHttpEntity)
            complete(entityFuture)
          }
        } ~ (post & extractRequest & extractLog) { (httpRequest: HttpRequest, log: LoggingAdapter) =>
          val strictEntityFuture: Future[HttpEntity.Strict] = httpRequest.entity.toStrict(3 seconds)
          val entity = strictEntityFuture.flatMap { strictEntity =>
            val personJsonString: String = strictEntity.data.utf8String
            val person: Person = personJsonString.parseJson.convertTo[Person]
            val personCreatedFuture: Future[PersonCreated] = (personActor ? CreatePerson(person)).mapTo[PersonCreated]
            personCreatedFuture.map { msg: PersonCreated =>
              println(s"add that person to your database. httpRequest: $httpRequest")
              toHttpEntity(person.toJson.prettyPrint)
            }
          }
          complete(entity)
        }
      }

    println("http GET localhost:8080/api/people")
    println("http GET localhost:8080/api/people/1")
    println("http GET localhost:8080/api/people?pin=1")
    println("http POST localhost:8080/api/people < src/main/resources/json/person.json")
    Http()
      .newServerAt("localhost", 8080)
      .bindFlow(personRoutes)
  }
}
