package org.github.felipegutierrez.explore.akka.patterns

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class AskPatternDemoSpec
  extends TestKit(ActorSystem("AskPatternDemoSpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
    with ImplicitSender
    with AnyWordSpecLike
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import AskPatternDemo._

  "An actor using the ASK pattern" should {
    import AskPatternDemo._
    "not authenticate without registering before" in {
      val authManager = system.actorOf(Props[AuthManager])

      // fails to authenticate
      authManager ! Authenticate("felipe", "passwordandusernotregistered")
      expectMsg(AuthFailure(AuthManager.AUTH_FAILURE_NOT_FOUND))
    }
    "not authenticate with wrong password" in {
      val authManager = system.actorOf(Props[AuthManager])
      // register but fails on authentication
      authManager ! RegisterUser("felipe", "mypass")
      authManager ! Authenticate("felipe", "iloveakka")
      expectMsg(AuthFailure(AuthManager.AUTH_FAILURE_PASSWORD_INCORRECT))
    }
    "authenticate with correct password" in {
      val authManager = system.actorOf(Props[AuthManager])
      authManager ! RegisterUser("felipe", "mypass")
      authManager ! Authenticate("felipe", "mypass")
      expectMsg(AuthSuccess)
    }
  }
  "An actor using the ASK pattern with akka.pattern.pipe" should {
    import AskPatternDemo._
    "not authenticate without registering before" in {
      val pipedAuthManager = system.actorOf(Props[PipedAuthManager])

      // fails to authenticate
      pipedAuthManager ! Authenticate("felipe", "passwordandusernotregistered")
      expectMsg(AuthFailure(AuthManager.AUTH_FAILURE_NOT_FOUND))
    }
    "not authenticate with wrong password" in {
      val pipedAuthManager = system.actorOf(Props[PipedAuthManager])
      // register but fails on authentication
      pipedAuthManager ! RegisterUser("felipe", "mypass")
      pipedAuthManager ! Authenticate("felipe", "iloveakka")
      expectMsg(AuthFailure(AuthManager.AUTH_FAILURE_PASSWORD_INCORRECT))
    }
    "authenticate with correct password" in {
      val pipedAuthManager = system.actorOf(Props[PipedAuthManager])
      pipedAuthManager ! RegisterUser("felipe", "mypass")
      pipedAuthManager ! Authenticate("felipe", "mypass")
      expectMsg(AuthSuccess)
    }
  }
}
