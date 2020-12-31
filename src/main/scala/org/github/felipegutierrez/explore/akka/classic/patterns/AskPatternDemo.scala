package org.github.felipegutierrez.explore.akka.classic.patterns

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object AskPatternDemo {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    import AskPatternDemo._
    val system = ActorSystem("AskPatternDemo")
    val authManager = system.actorOf(Props[AuthManager])

    // fails to authenticate
    authManager ! Authenticate("felipe", "passwordandusernot registered")

    // register but fails on authentication
    authManager ! RegisterUser("felipe", "mypass")
    authManager ! Authenticate("felipe", "iloveakka")

    // now we can log in
    authManager ! Authenticate("felipe", "mypass")

    val pipedAuthManager = system.actorOf(Props[PipedAuthManager])

    // fails to authenticate
    pipedAuthManager ! Authenticate("felipe", "passwordandusernot registered")

    // register but fails on authentication
    pipedAuthManager ! RegisterUser("felipe", "mypass")
    pipedAuthManager ! Authenticate("felipe", "iloveakka")

    // now we can log in
    pipedAuthManager ! Authenticate("felipe", "mypass")
  }

  object AskPatternDemo {

    case class Read(key: String)

    case class Write(key: String, value: String)

    class KVActor extends Actor with ActorLogging {

      override def receive: Receive = online(Map())

      def online(kv: Map[String, String]): Receive = {
        case Read(key) =>
          log.info(s"trying to read value from key $key")
          sender() ! kv.get(key)
        case Write(key, value) =>
          log.info(s"writing value: $value with key $key")
          context.become(online(kv + (key -> value)))
      }
    }

    // user authenticator actor
    case class RegisterUser(username: String, password: String)

    case class Authenticate(username: String, password: String)

    case class AuthFailure(message: String)

    case object AuthSuccess

    object AuthManager {
      val AUTH_FAILURE_NOT_FOUND = "username not found"
      val AUTH_FAILURE_PASSWORD_INCORRECT = "password incorrect"
      val AUTH_FAILURE_SYSTEM = "system error"
    }

    class AuthManager extends Actor with ActorLogging {

      import AuthManager._

      // step 2 - logistics
      implicit val timeout: Timeout = Timeout(1 second)
      implicit val executionContext: ExecutionContext = context.dispatcher

      protected val authDb = context.actorOf(Props[KVActor])

      override def receive: Receive = {
        case RegisterUser(username, password) => authDb ! Write(username, password)
        case Authenticate(username, password) => handleAuthentication(username, password)
      }

      def handleAuthentication(username: String, password: String) = {
        val originalSender = sender()
        // step 1 - import the ask pattern
        // step 3 - ask the actor
        val future = authDb ? Read(username)
        // step 4 - handle the future for e.g. with onComplete
        future.onComplete {
          // step 5 most important
          // NEVER CALL METHODS ON THE ACTOR INSTANCE OR ACCESS MUTABLE STATE IN ONCOMPLETE.
          // avoid closing over the actor instance or mutable state
          case Success(None) => originalSender ! AuthFailure(AUTH_FAILURE_NOT_FOUND)
          case Success(Some(dbPassword)) =>
            if (dbPassword == password) originalSender ! AuthSuccess
            else originalSender ! AuthFailure(AUTH_FAILURE_PASSWORD_INCORRECT)
          case Failure(_) => originalSender ! AuthFailure(AUTH_FAILURE_SYSTEM)
        }
      }
    }

    class PipedAuthManager extends AuthManager {

      import AuthManager._

      override def handleAuthentication(username: String, password: String): Unit = {
        // step 3 - ask the actor
        val future: Future[Any] = authDb ? Read(username) // Future[Any]
        // step 4 - process the future until you get the responses you will send back
        val passwordFuture: Future[Option[String]] = future.mapTo[Option[String]] // Future[Option[String]]
        val responseFuture: Future[Any] = passwordFuture.map {
          case None => AuthFailure(AUTH_FAILURE_NOT_FOUND)
          case Some(dbPassword) =>
            if (dbPassword == password) AuthSuccess
            else AuthFailure(AUTH_FAILURE_PASSWORD_INCORRECT)
        } // Future[Any] - will be completed with the response I will send back

        // step 5 - pipe the resulting future to the actor you want to send the result to
        /*
          When the future completes, send the response to the actor ref in the arg list.
         */
        responseFuture.pipeTo(sender())
      }
    }

  }

}
