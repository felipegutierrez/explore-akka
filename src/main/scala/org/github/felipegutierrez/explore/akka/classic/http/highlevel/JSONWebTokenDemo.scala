package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtSprayJson}
import spray.json._

import java.util.concurrent.TimeUnit
import scala.util.{Failure, Success}

object SecurityDomain extends DefaultJsonProtocol {

  case class LoginRequest(username: String, password: String)

  implicit val logingRequestFormat = jsonFormat2(LoginRequest)
}

object JSONWebTokenDemo extends SprayJsonSupport {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("JSONWebTokenDemo")
    import SecurityDomain._

    val superSecretPassordDb = Map("admin" -> "admin", "felipe" -> "Rockthejvm1!")

    val algorithm = JwtAlgorithm.HS256
    val secretKey = "rockthejvmsecret"

    def checkPassword(username: String, password: String): Boolean =
      superSecretPassordDb.contains(username) && superSecretPassordDb(username) == password

    def createToken(username: String, expirationPeriodInDays: Int): String = {
      val claims = JwtClaim(
        expiration = Some(System.currentTimeMillis() / 1000 + TimeUnit.DAYS.toSeconds(expirationPeriodInDays)),
        issuedAt = Some(System.currentTimeMillis() / 1000),
        issuer = Some("rockthejvm.com")
      )
      JwtSprayJson.encode(claims, secretKey, algorithm) // JWT string
    }

    def isTokenExpired(token: String): Boolean = JwtSprayJson.decode(token, secretKey, Seq(algorithm)) match {
      case Success(claims) => claims.expiration.getOrElse(0L) < System.currentTimeMillis() / 1000
      case Failure(exception) => true
    }

    def isTokenValid(token: String): Boolean = JwtSprayJson.isValid(token, secretKey, Seq(algorithm))

    val loginRoute = {
      post {
        entity(as[LoginRequest]) {
          case LoginRequest(username, password) if checkPassword(username, password) =>
            val token = createToken(username, 1)
            respondWithHeader(RawHeader("Access-Token", token)) {
              complete(StatusCodes.OK)
            }
          case _ => complete(StatusCodes.Unauthorized)
        }
      }
    }

    val authenticatedRoute = {
      (path("secureEndpoint") & get) {
        optionalHeaderValueByName("Authorization") {
          case Some(token) =>
            if (isTokenValid(token)) {
              if (isTokenExpired(token)) {
                complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "token expired =("))
              } else {
                complete("user accessed authorized endpoint =)")
              }
            } else {
              complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "token invalid or has been tampered with =("))
            }
          case _ => complete(HttpResponse(status = StatusCodes.Unauthorized, entity = "no token provided =("))
        }
      }
    }

    val route = loginRoute ~ authenticatedRoute

    println("http POST localhost:8080 < src/main/resources/json/login.json")
    println("http GET localhost:8080/secureEndpoint \"Authorization: COPY_THE_TOKEN\"")
    Http()
      .newServerAt("localhost", 8080)
      .bindFlow(route)
  }
}
