package org.github.felipegutierrez.explore.akka.tests

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.DurationInt
import scala.util.Random

class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A simple actor" should {
    "send back the same message" in {
      val echoActor = system.actorOf(BasicSpec.propsSimpleActor)
      val message = "hello, test"
      echoActor ! message

      expectMsg(message) // akka.test.single-expect-default
    }
  }

  "A blackhole actor" should {
    "send back some message" in {
      val blackhole = system.actorOf(BasicSpec.propsBlackHoleActor)
      val message = "hello, test"
      blackhole ! message

      expectNoMessage(1 second)
    }
  }

  "a lab test actor" should {
    val labTestActor = system.actorOf(BasicSpec.propsLabActor)
    "turn a string to uppercase" in {
      labTestActor ! "I love Akka"
      val reply = expectMsgType[String]
      assert(reply == "I LOVE AKKA")
    }
    "reply to a greeting" in {
      labTestActor ! "greeting"
      expectMsgAnyOf("hi", "hello")
    }
    "reply with favoriteTech" in {
      labTestActor ! "favoriteTech"
      expectMsgAllOf("Scala", "Akka")
    }
    "reply with cool tech in a different way" in {
      labTestActor ! "favoriteTech"
      val messages = receiveN(2) // Seq[Any]

    }
    "reply with a cool tech in a fancy way" in {
      labTestActor ! "favoriteTech"
      expectMsgPF() {
        case "Scala" =>
        case "Akka" =>
      }
    }
  }


  object BasicSpec {

    class SimpleActor extends Actor {
      override def receive = {
        case message => sender() ! message
      }
    }

    val propsSimpleActor = {
      Props(new SimpleActor)
    }

    class BlackHoleActor extends Actor {
      override def receive: Receive = Actor.emptyBehavior
    }

    val propsBlackHoleActor = {
      Props(new BlackHoleActor)
    }

    class LabActor extends Actor {
      val random = new Random()

      override def receive: Receive = {
        case "greeting" =>
          if (random.nextBoolean()) sender() ! "hi" else sender() ! "hello"
        case "favoriteTech" =>
          sender() ! "Scala"
          sender() ! "Akka"
        case message: String => sender() ! message.toUpperCase()
      }
    }

    val propsLabActor = {
      Props(new LabActor)
    }
  }

}
