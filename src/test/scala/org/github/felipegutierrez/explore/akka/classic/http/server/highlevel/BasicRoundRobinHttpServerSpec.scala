package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.github.felipegutierrez.explore.akka.classic.http.server.highlevel.BasicRoundRobinHttpServer.simpleRoute
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

class BasicRoundRobinHttpServerSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest {

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(2 seconds)

  "A basic GET request" should {
    "return OK [200]" in {
      Get("/reference") ~> simpleRoute ~> check {
        // assertions: inspect the response
        status shouldBe StatusCodes.OK
        // not working because we cannot inspect the actor reference
        //        val entity = HttpEntity(
        //          ContentTypes.`text/html(UTF-8)`,
        //          s"""
        //             |<html>
        //             | <body>I got the actor reference: ${ref} </body>
        //             |</html>
        //             |""".stripMargin
        //        )
        //        responseEntity shouldBe (entity)
      }
    }
    "return unknown " in {
      Get("/somethingelse") ~> simpleRoute ~> check {
        status shouldBe StatusCodes.Forbidden
      }
    }
  }
}
