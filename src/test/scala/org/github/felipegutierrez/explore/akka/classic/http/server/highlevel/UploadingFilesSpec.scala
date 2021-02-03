package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.github.felipegutierrez.explore.akka.classic.http.server.highlevel.UploadingFiles.filesRoutes
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

class UploadingFilesSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest {

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(2 seconds)

  "A basic GET request to open the html form" should {
    "return OK [200]" in {
      Get("/") ~> filesRoutes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
  "A POST request upload the file" should {
    "return OK" in {
      Post("/upload") ~> filesRoutes ~> check {
        handled should ===(false)

        // rejection should ===(UnsupportedRequestContentTypeRejection(Set(ContentTypes.`text/plain(UTF-8)`)))

        //        println(rejections)
        //        rejections.foreach { e: Rejection =>
        //          println(e.toString)
        //          println(e.asInstanceOf[UnsupportedRequestContentTypeRejection].contentType)
        //        }
        //        rejections should contain (
        //          UnsupportedRequestContentTypeRejection(
        //            Set(ContentTypes.`text/plain(UTF-8)`),
        //            Some(ContentTypes.`text/plain(UTF-8)`)
        //          )
        //        )
      }
    }
  }
}
