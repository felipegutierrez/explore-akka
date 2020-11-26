package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ActorsIntroTest extends TestKit(ActorSystem("ActorsIntroSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "The ActorsIntro actor" should {
    "send back hi replay" in {
      val name = "Bob"
      val actorPerson = system.actorOf(ActorsIntro.Person.props(name))
      val hi = "hi"
      val hiReply = s"Hi, my name is $name"
      actorPerson ! hi
      expectMsg(hiReply)
    }

    "or send back the same message" in {
      val name = "Bob"
      val actorPerson = system.actorOf(ActorsIntro.Person.props(name))

      val message = "hello, test"
      actorPerson ! message
      expectMsg(message)
    }
  }
}
