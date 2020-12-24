package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Multipart}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString

import java.io.File
import scala.concurrent.Future
import scala.util.{Failure, Success}

object UploadingFiles {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("UploadingFiles")
    
    val filesRoutes = {
      (pathEndOrSingleSlash & get) {
        complete(
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              |  <body>
              |    <form action="http://localhost:8080/upload" method="post" enctype="multipart/form-data">
              |      <input type="file" name="myFile">
              |      <button type="submit">Upload</button>
              |    </form>
              |  </body>
              |</html>
          """.stripMargin
          )
        )
      } ~ (path("upload") & post & extractLog) { log =>
        // handling uploading files using multipart/form-data
        entity(as[Multipart.FormData]) { formData =>
          // handle file payload
          val partsSource: Source[Multipart.FormData.BodyPart, Any] = formData.parts
          val filePartsSink: Sink[Multipart.FormData.BodyPart, Future[Done]] =
            Sink.foreach[Multipart.FormData.BodyPart] { bodyPart =>
              if (bodyPart.name == "myFile") {
                // create a file
                val filename = "download/" + bodyPart.filename.getOrElse("tempFile_" + System.currentTimeMillis())
                val file = new File(filename)

                log.info(s"writing to file: $filename")
                val fileContentsSource: Source[ByteString, _] = bodyPart.entity.dataBytes
                val fileContentsSink: Sink[ByteString, _] = FileIO.toPath(file.toPath)

                // writing the data to the file using akka-stream graph
                fileContentsSource.runWith(fileContentsSink)
              }
            }
          val writeOperationFuture = partsSource.runWith(filePartsSink)
          onComplete(writeOperationFuture) {
            case Success(value) => complete("file uploaded =)")
            case Failure(exception) => complete(s"file failed to upload: $exception")
          }
        }
      }
    }

    println("access the browser at: localhost:8080")
    Http()
      .newServerAt("localhost", 8080)
      .bindFlow(filesRoutes)
  }
}
