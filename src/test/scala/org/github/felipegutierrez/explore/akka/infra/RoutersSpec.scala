package org.github.felipegutierrez.explore.akka.infra

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.{BroadcastRoutingLogic, RoundRobinGroup, RoundRobinRoutingLogic}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class RoutersSpec extends TestKit(ActorSystem("RoutersSpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import Routers._

  "A router" should {
    "round robin messages evenly" in {
      EventFilter.info(pattern = s"hello [0-9]", occurrences = 6) intercept {
        val manualRoundRobinMaster = system.actorOf(Props(classOf[ManualMaster], 3, RoundRobinRoutingLogic()), "manualRoundRobinMaster")
        for (i <- 1 to 6) {
          manualRoundRobinMaster ! s"hello $i"
        }
      }
    }
    "broadcast messages" in {
      EventFilter.info(pattern = s"hello [0-9]", occurrences = 18) intercept {
        val manualBroadcastMaster = system.actorOf(Props(classOf[ManualMaster], 3, BroadcastRoutingLogic()), "manualBroadcastMaster")
        for (i <- 1 to 6) {
          manualBroadcastMaster ! s"hello $i"
        }
      }
    }
  }
  "A group router" should {
    "round robin messages evenly to actors imported from another system" in {
      EventFilter.info(pattern = s"[0-9] hello group actors", occurrences = 5) intercept {
        val workerList: List[ActorRef] = (1 to 5).map(i => system.actorOf(Props[Worker], s"another_worker_$i")).toList
        val workersPath: List[String] = workerList.map(workerRef => workerRef.path.toString)
        val groupMaster: ActorRef = system.actorOf(RoundRobinGroup(workersPath).props())
        for (i <- 1 to 5) {
          groupMaster ! s"$i hello group actors"
        }
      }
    }
  }
}
