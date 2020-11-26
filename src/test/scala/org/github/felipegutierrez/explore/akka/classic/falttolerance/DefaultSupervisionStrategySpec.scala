package org.github.felipegutierrez.explore.akka.classic.falttolerance

import akka.actor.{ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class DefaultSupervisionStrategySpec
  extends TestKit(ActorSystem("DefaultSupervisionStrategySpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import DefaultSupervisionStrategy._

  val id = 0
  "the DefaultSupervisionStrategy" should {
    "throws a RuntimeException for Fail messages" in {
      EventFilter[RuntimeException](occurrences = 1) intercept {
        val supervisor = system.actorOf(Props[Parent], "supervisor")
        supervisor ! Parent.FailChild
      }
    }
    "continue to send messages to the child even if the child has been failed" in {
      EventFilter.info(pattern = s"alive and kicking $id", occurrences = 1) intercept {
        // our test code
        val anotherSupervisor = system.actorOf(Props[Parent], "anotherSupervisor")
        anotherSupervisor ! Parent.FailChild
        Thread.sleep(500)
        anotherSupervisor ! Parent.CheckChild(id)
      }
    }
  }
}
