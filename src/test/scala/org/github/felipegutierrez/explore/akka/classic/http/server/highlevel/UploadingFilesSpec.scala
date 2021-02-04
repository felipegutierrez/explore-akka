package org.github.felipegutierrez.explore.akka.classic.http.server.highlevel

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.UnsupportedRequestContentTypeRejection
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
  "A POST request upload without a file" should {
    "return NOT OK" in {
      Post("/upload") ~> filesRoutes ~> check {
        handled should ===(false)
        rejections should not be empty // "natural language" style
        rejections.should(not).be(empty) // same

        val methodRejections = rejections.collect {
          case rejection: UnsupportedRequestContentTypeRejection => rejection
        }
        methodRejections.length shouldBe 1

        rejection should ===(UnsupportedRequestContentTypeRejection(
          Set(ContentTypeRange(MediaRange.apply(MediaTypes.`multipart/form-data`))),
          Some(ContentTypes.NoContentType))
        )
      }
    }
  }
}
