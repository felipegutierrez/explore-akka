package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.http.scaladsl.model.StatusCodes
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
    }
}
