package org.github.felipegutierrez.explore.akka.classic.http.server.lowlevel

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}

import java.io.InputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import scala.concurrent.Future

object HttpsServerContext {
  // Step 1 - key store
  val ks: KeyStore = KeyStore.getInstance("PKCS12")
  val keystoreFile: InputStream = getClass.getClassLoader.getResourceAsStream("certs/keystore.pkcs12")
  // alternative: new FileInputStream(new File("src/main/resources/certs/keystore.pkcs12"))
  val password = "akka-https".toCharArray // TODO: fetch the password from a secure place
  ks.load(keystoreFile, password)

  // Step 2 - initialize a key manager
  val keyManagerFactory = KeyManagerFactory.getInstance("SunX509") // PKI public key infrastructure
  keyManagerFactory.init(ks, password)

  // Step 3 - initialize a trust manager
  val trustmanagerFactory = TrustManagerFactory.getInstance("SunX509")
  trustmanagerFactory.init(ks)

  // Step 4 - initialize a SSL context
  val sslContext: SSLContext = SSLContext.getInstance("TLS")
  sslContext.init(keyManagerFactory.getKeyManagers, trustmanagerFactory.getTrustManagers, new SecureRandom())

  // Step 5 - return the HTTPS connection context
  val httpsConnectionContext: HttpsConnectionContext = ConnectionContext.httpsServer(sslContext)
}

object HttpsRestApi {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    implicit val system = ActorSystem("HttpsRestApi")

    // Step 6 - define the request handler
    val requestHandler: HttpRequest => HttpResponse = {
      case HttpRequest(HttpMethods.GET, uri, headers, entity, protocol) =>
        HttpResponse(
          StatusCodes.OK, // HTTP 200
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  Hello Akka HTTPS
              | </body>
              |</html>
              |""".stripMargin)
        )
      case request: HttpRequest =>
        request.discardEntityBytes()
        HttpResponse(
          StatusCodes.NotFound, // HTTP 404
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  OOPS! page not found =(
              | </body>
              |</html>
              |""".stripMargin)
        )
    }
    println("type on the browser: \"https://localhost:8443\"")
    val httpsBinding = Http()
      .newServerAt("localhost", 8443)
      .enableHttps(HttpsServerContext.httpsConnectionContext)
      .bindSync(requestHandler)

    import system.dispatcher
    val asyncRequestHandler: HttpRequest => Future[HttpResponse] = {
      case HttpRequest(HttpMethods.GET, Uri.Path("/home"), headers, entity, protocol) =>
        Future(HttpResponse(
          StatusCodes.OK, // HTTP 200
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  Async Hello Akka HTTPS
              | </body>
              |</html>
              |""".stripMargin)
        ))
      case HttpRequest(HttpMethods.GET, Uri.Path("/redirect"), headers, entity, protocol) =>
        Future(HttpResponse(
          StatusCodes.Found,
          headers = List(Location("http://www.google.com"))
        ))
      case request: HttpRequest =>
        request.discardEntityBytes()
        Future(HttpResponse(
          StatusCodes.NotFound, // HTTP 404
          entity = HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            """
              |<html>
              | <body>
              |  OOPS! async page not found =(<br>try https://localhost:8553/home
              | </body>
              |</html>
              |""".stripMargin)
        ))
    }
    println("type on the browser: \"https://localhost:8553/home\"")
    val httpsBindingAsync = Http()
      .newServerAt("localhost", 8553)
      .enableHttps(HttpsServerContext.httpsConnectionContext)
      .bind(asyncRequestHandler)
  }
}
