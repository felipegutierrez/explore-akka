package org.github.felipegutierrez.explore.akka.classic.falttolerance

import akka.actor.{ActorKilledException, ActorSystem, Kill, PoisonPill, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.classic.falttolerance.StartingStoppingActors.{Child, Parent}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class StartingStoppingActorsSpec extends TestKit(ActorSystem("StartingStoppingActorsSpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "a child actor" should {
    "stop when its parent sends a message to stop" in {
      val childName = "child1"
      EventFilter.info(pattern = s"Stopping child: $childName", occurrences = 1) intercept {
        import Parent._

        val parentActor = system.actorOf(Props[Parent], "parent")

        ///*  stopping child after some time
        parentActor ! StartChild(childName)
        val child = system.actorSelection(s"/user/parent/$childName")
        child ! "hi kid!"
        Thread.sleep(1000)
        parentActor ! StopChild(childName)
        for (_ <- 1 to 50) child ! "are you still there?"
      }
    }
  }

  "stopping parent" should {
    "makes all its children stop as well" in {
      EventFilter.warning(pattern = s"Parent, are you still there?", occurrences = 10) intercept {
        import Parent._

        val parentActor = system.actorOf(Props[Parent], "parent2")

        parentActor ! StartChild("child2")
        val child2 = system.actorSelection("/user/parent2/child2")
        child2 ! "hi kid 2!"
        parentActor ! Stop
        for (_ <- 1 to 10) parentActor ! "Parent, are you still there?"
        for (i <- 1 to 5) child2 ! s"[$i] are you still there second kid?"
      }
    }
  }

  "stopping an actor with poison pill" should {
    "make this actor unavailable" in {
      val msg03 = "are you still...... there?"
      EventFilter.warning(pattern = msg03, occurrences = 1) intercept {
        val childWithoutParent = system.actorOf(Props[Child], "childWithoutParent")
        childWithoutParent ! "hello child without a parent, are you there?"
        childWithoutParent ! PoisonPill
        childWithoutParent ! msg03
      }
    }
  }

  "killing an actor with Kill message" should {
    "also make this actor unavailable" in {
      EventFilter[ActorKilledException](occurrences = 1) intercept {
        val abruptlyTerminatedActor = system.actorOf(Props[Child])
        abruptlyTerminatedActor ! "you are about to be terminated!"
        abruptlyTerminatedActor ! Kill
        abruptlyTerminatedActor ! "have you been terminated?"
      }
    }
  }
}