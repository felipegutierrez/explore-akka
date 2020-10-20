package org.github.felipegutierrez.explore.akka.actors

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class WordCountUsingChildActorsTest
  extends TestKit(ActorSystem("WordCountUsingChildActorsSpec", ConfigFactory.load().getConfig("interceptingLogMessages")))
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

  // s"${self.path} I have received tasks $id with $text"
  val id = 0
  val text = "I LOVE AKKA BECAUSE IT HAS LOGS"
  val insecureText = "I want to use my credit card inside a program even it is insecure"
  val count = text.split(" ").length
  "the worker tasks" should {
    "correctly log the count words" in {
      EventFilter.info(pattern = s"I have received task [0-9]+ with $text which contains $count words", occurrences = 1) intercept {
        // our test code
        val master = system.actorOf(WordCounterMaster.propsMaster)
        master ! WordCounterMaster.Initialize(1)
        expectMsg(WordCounterMaster.InitializeAck)
        master ! text
      }
    }
    "freak out if the string has credit card" in {
      EventFilter[RuntimeException](occurrences = 1) intercept {
        val master = system.actorOf(WordCounterMaster.propsMaster)
        master ! WordCounterMaster.Initialize(1)
        master ! insecureText
      }
    }
  }
}
