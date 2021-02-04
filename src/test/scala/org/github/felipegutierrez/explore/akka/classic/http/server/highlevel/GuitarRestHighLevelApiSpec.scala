package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel


import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.github.felipegutierrez.explore.akka.classic.http.server.lowlevel.{Guitar, GuitarStoreJsonProtocol}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

class GuitarRestHighLevelApiSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with GuitarStoreJsonProtocol {

  import GuitarRestHighLevelApi._

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(2 seconds)
  "A Guitar store backend" should {
    "return all guitars in the store" in {
      Get("/api/guitar") ~> guitarServerRoutes ~> check {
        // assertions: inspect the response
        status shouldBe StatusCodes.OK
        entityAs[List[Guitar]] shouldBe guitarList
      }
    }
    "return a guitar by its id" in {
      Get("/api/guitar?id=2") ~> guitarServerRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Option[Guitar]] shouldBe Some(guitarList.filter(_.make == "Martin")(0))
      }
    }
    //    "not return a guitar that does not exist" in {
    //      Get("/api/guitar?id=10") ~> guitarServerRoutes ~> check {
    //        status shouldBe StatusCodes.OK
    //        responseAs[Option[Guitar]] shouldBe Some(guitarList.filter(_.make == "doesnotexist")(0))
    //      }
    //    }
    "return guitars NOT IN stock" in {
      Get("/api/guitar/inventory?inStock=false") ~> guitarServerRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Option[List[Guitar]]] shouldBe Some(guitarList.filter(_.quantity == 0))
      }
    }
    "return guitars IN stock" in {
      Get("/api/guitar/inventory?inStock=true") ~> guitarServerRoutes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Option[List[Guitar]]] shouldBe Some(guitarList.filter(_.quantity > 0))
      }
    }
  }
}
