package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.MethodRejection
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

class MarshallingJSONSpec
  extends AnyWordSpec
    with Matchers
    with ScalatestRouteTest
    with PlayerJsonProtocol {

  import MarshallingJSON._

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(2 seconds)
  //  "A Game area map backend" should {
  //    "return all the players in the game" in {
  //      // send an HTTP request through an endpoint that you want to test
  //      val request = Get("/api/player")
  //      val requestWithRoutes = request.~>(gameRoutes)(TildeArrow.injectIntoRoute)
  //      requestWithRoutes.~>(check {
  //        status shouldBe StatusCodes.OK
  //        entityAs[List[Player]] shouldBe players
  //      })
  //    }
  //  }
  "A Game area map backend" should {
    "return all the players in the game" in {
      // send an HTTP request through an endpoint that you want to test
      Get("/api/player") ~> gameRoutes ~> check {
        // assertions: inspect the response
        status shouldBe StatusCodes.OK
        entityAs[List[Player]] shouldBe players
      }
    }
    "return a player by its nickname" in {
      Get("/api/player/felipeogutierrez") ~> gameRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Option[Player]] shouldBe Some(players.filter(_.nickname == "felipeogutierrez")(0))
      }
    }
    "return a player by its nickname param" in {
      Get("/api/player?nickname=rolandbraveheart") ~> gameRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Option[Player]] shouldBe Some(players.filter(_.nickname == "rolandbraveheart")(0))
      }
    }
    "add a new player in the game area" in {
      val newPLayer = Player("bambam", "wolf", 33)
      Post("/api/player", newPLayer) ~> gameRoutes ~> check {
        status shouldBe StatusCodes.OK
        Get("/api/player") ~> gameRoutes ~> check {
          status shouldBe StatusCodes.OK
          entityAs[List[Player]] should contain(newPLayer)
        }
      }
    }
    "not accept other methods than POST and GET and DELETE" in {
      Put("/api/player") ~> gameRoutes ~> check {
        rejections should not be empty // "natural language" style
        rejections.should(not).be(empty) // same

        val methodRejections = rejections.collect {
          case rejection: MethodRejection => rejection
        }
        methodRejections.length shouldBe 3
      }
    }
  }
}
