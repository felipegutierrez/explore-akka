package org.github.felipegutierrez.explore.akka.classic.basics

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._

class TimeAssertionsSpec
  extends TestKit(ActorSystem("TimeAssertionsSpec", ConfigFactory.load().getConfig("specialTimedAssertionsConfig")))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TimeAssertions._

  "a worker actor" should {
    val workerActor = system.actorOf(WorkerActor.propsWorkerActor)
    "(a time box test) reply with the meaning of life in a timely manner, at least in 500 ms or up to 1 second" in {
      within(500 millis, 1 second) {
        workerActor ! "work"
        expectMsg(WorkerActor.WorkResult(43))
      }
    }
    "reply with valid work at a reasonable cadence" in {
      within(1 second) {
        workerActor ! "workSequence"
        val results: Seq[Int] = receiveWhile(max = 2 seconds, idle = 500 millis, messages = 10) {
          case WorkerActor.WorkResult(result) => result
        }
        assert(results.sum > 5)
      }
    }
    "reply to a test probe in a timely manner" in {
      // timeout of 0.5 seconds from the special config, and it overwrites the the within(1 second)
      within(1 second) {
        val probe = TestProbe()
        probe.send(workerActor, "work")
        // this fails
        // probe.expectMsg(WorkerActor.WorkResult(43))
        probe.expectNoMessage(400 millis)
      }
    }
  }
}
