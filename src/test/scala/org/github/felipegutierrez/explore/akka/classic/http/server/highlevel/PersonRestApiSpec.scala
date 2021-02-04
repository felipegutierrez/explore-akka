package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

class PersonRestApiSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with PersonJsonProtocol {

  import PersonRestApi._

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(2 seconds)

  "A person rest API backend" should {
    "return all the people in the Set" in {
      Get("/api/people") ~> personRoutes ~> check {
        status shouldBe StatusCodes.OK
        entityAs[List[Person]] shouldBe people
      }
    }
    "return a person by its id" in {
      Get("/api/people/1") ~> personRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Option[Person]] shouldBe Some(people.filter(_.pin == 1)(0))
      }
    }
    "add a new person in the set" in {
      val newPerson = Person(4, "Felipe")
      Post("/api/people", newPerson) ~> personRoutes ~> check {
        status shouldBe StatusCodes.OK
        Get("/api/people") ~> personRoutes ~> check {
          status shouldBe StatusCodes.OK
          entityAs[List[Person]] should contain(newPerson)
        }
      }
    }
    "not find other urls" in {
      Get("/api") ~> personRoutes ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }
}
