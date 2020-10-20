package org.github.felipegutierrez.explore.akka.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class WordCountUsingChildActorsTest extends TestKit(ActorSystem("WordCountUsingChildActorsSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import WordCountUsingChildActors._

  "a master actor" should {
    "register 3 workers" in {
      val master = system.actorOf(WordCounterMaster.propsMaster)
      master ! WordCounterMaster.Initialize(3)

      // we expect 3 messages InitializeAck and no more than 3
      expectMsg(WordCounterMaster.InitializeAck)
      expectMsg(WordCounterMaster.InitializeAck)
      expectMsg(WordCounterMaster.InitializeAck)
      expectNoMessage()
    }
    "count a text with 3 words" in {
      val master = system.actorOf(WordCounterMaster.propsMaster)
      val worker = TestProbe("worker")

      master ! WordCounterMaster.Initialize(1)
      expectMsg(WordCounterMaster.InitializeAck)

      val workloadString = "I love Akka"
      master ! workloadString
      expectMsg(workloadString.split(" ").length)
    }
  }
}
