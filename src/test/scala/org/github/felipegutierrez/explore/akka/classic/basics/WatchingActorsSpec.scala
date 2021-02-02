package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.classic.falttolerance.WatchingActors.ParentWatcher
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class WatchingActorsSpec extends TestKit(ActorSystem("WatchingActorsSpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  val childName = "child3"

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "a parent actor with fault tolerance strategy to watch child" should {
    "receive a message from the child actor if it dies" in {
      EventFilter.info(pattern = "the reference that I am watching has been stopped", occurrences = 1) intercept {

        // Starting ParentWatcher
        // the reference that I am watching has been stopped

        import ParentWatcher._

        val parentWatcherActor = system.actorOf(Props[ParentWatcher], "parentWatcher")
        parentWatcherActor ! StartChild(childName)
        val child3 = system.actorSelection("akka://WatchingActorsSpec/user/parentWatcher/child3")
        // make sure that child3 has been created
        child3 ! s"[1] child3, are you still there?"
        Thread.sleep(1000)
        child3 ! s"[2] child3, are you still there?"
        child3 ! PoisonPill
        for (i <- 3 to 10) child3 ! s"[$i] child3, are you still there?"
      }
    }
  }
}
