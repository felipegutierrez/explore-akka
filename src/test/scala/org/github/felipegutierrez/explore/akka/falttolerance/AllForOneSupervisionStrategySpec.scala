package org.github.felipegutierrez.explore.akka.falttolerance

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class AllForOneSupervisionStrategySpec
  extends TestKit(ActorSystem("AllForOneSupervisionStrategySpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import AllForOneSupervisionStrategy._

  "An all-for-one supervisor" should {
    "apply the all-for-one strategy" in {
      val supervisor = system.actorOf(Props[Supervisor], "allForOneSupervisor")
      supervisor ! Supervisor.PropsMessage(Props[FussyWordCounter], "child1")
      val child = expectMsgType[ActorRef]

      supervisor ! Supervisor.PropsMessage(Props[FussyWordCounter], "child2")
      val secondChild = expectMsgType[ActorRef]

      secondChild ! "Testing supervision"
      secondChild ! Supervisor.Report
      expectMsg(2)

      EventFilter[NullPointerException]() intercept {
        child ! ""
      }

      Thread.sleep(500)

      secondChild ! Supervisor.Report
      expectMsg(0)
    }
  }
}
