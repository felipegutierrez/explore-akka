package org.github.felipegutierrez.explore.akka.patterns

import akka.actor.{ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class StashDemoSpec
  extends TestKit(ActorSystem("StashDemoSpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
    with ImplicitSender
    with AnyWordSpecLike
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import StashDemo._

  "An actor with Stash trait" should {
    "round robin messages evenly" in {
      EventFilter.info(pattern = s"I have read I love stash", occurrences = 1) intercept {

        val resourceActor = system.actorOf(Props[ResourceActor])
        resourceActor ! Read // stashed
        resourceActor ! Open // switch to open; I have read ""
        resourceActor ! Open // stashed
        resourceActor ! Write("I love stash") // I am writing I love stash
        resourceActor ! Close // switch to closed; switch to open
        resourceActor ! Read // I have read I love stash
      }
    }
  }
}
