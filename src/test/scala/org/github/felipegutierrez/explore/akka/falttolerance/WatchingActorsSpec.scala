package org.github.felipegutierrez.explore.akka.falttolerance

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class WatchingActorsSpec
  extends TestKit(ActorSystem("WatchingActorsSpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
    with ImplicitSender
    with AnyWordSpecLike
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import WatchingActors._

  "A checkout flow" should {
    "correctly log the dispatch of an order" in {
      val actorRefName = "child3"
      EventFilter.info(pattern = s"the reference that I am watching $actorRefName has been stopped", occurrences = 1) intercept {
        // our test code
        val parentWatcherActor = system.actorOf(Props[ParentWatcher], "parentWatcher")
        parentWatcherActor ! ParentWatcher.StartChild(actorRefName)
        val child3 = system.actorSelection(s"/user/parentWatcher/$actorRefName")
        // make sure that child3 has been created
        Thread.sleep(1000)
        child3 ! "hi kid!"
        child3 ! PoisonPill
      }
    }
  }
}
