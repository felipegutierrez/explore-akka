package org.github.felipegutierrez.explore.akka.falttolerance

import akka.actor.{ActorRef, ActorSystem, Props, Terminated}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.github.felipegutierrez.explore.akka.falttolerance.OneForOneSupervisionStrategy.Supervisor.PropsMessage
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class OneForOneSupervisionStrategySpec
  extends TestKit(ActorSystem("OneForOneSupervisionStrategySpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import OneForOneSupervisionStrategy._

  "A supervisor" should {
    "resume its child in case of a minor fault" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! PropsMessage(Props[FussyWordCounter], "child")
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Supervisor.Report
      expectMsg(3)

      child ! "Akka is awesome because I am learning to think in a whole new way"
      child ! Supervisor.Report
      expectMsg(3)
    }
    "restart its child in case of an empty sentence" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! PropsMessage(Props[FussyWordCounter], "child")
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Supervisor.Report
      expectMsg(3)

      child ! ""
      child ! Supervisor.Report
      expectMsg(0)
    }
    "terminate its child in case of a major error" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! PropsMessage(Props[FussyWordCounter], "child")
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! "akka is nice"
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child)
    }
    "escalate an error when it doesn't know what to do" in {
      val supervisor = system.actorOf(Props[Supervisor], "supervisor")
      supervisor ! PropsMessage(Props[FussyWordCounter], "child")
      val child = expectMsgType[ActorRef]

      watch(child)
      child ! 43
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child)
    }
  }
  "A kinder supervisor" should {
    "not kill children in case it's restarted or escalates failures" in {
      val noDeathOnRestartSupervisor = system.actorOf(Props[NoDeathOnRestartSupervisor], "noDeathOnRestartSupervisor")
      noDeathOnRestartSupervisor ! PropsMessage(Props[FussyWordCounter], "child1")
      val child1 = expectMsgType[ActorRef]

      child1 ! "Akka is cool"
      child1 ! Supervisor.Report
      expectMsg(3)

      child1 ! 45
      child1 ! Supervisor.Report
      expectMsg(0)
    }
  }
}
