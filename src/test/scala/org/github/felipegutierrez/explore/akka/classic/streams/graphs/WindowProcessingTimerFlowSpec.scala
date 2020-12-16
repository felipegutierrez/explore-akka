package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.pattern.pipe
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._

class WindowProcessingTimerFlowSpec extends TestKit(ActorSystem("WindowProcessingTimerFlowSpec"))
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "The WindowProcessingTimerFlow that aggregates every 5 seconds" should {
    "sum the values correct" in {
      val simpleSource = Source[Int](1 to 10).throttle(1, 1 second)
      val windowProcessingTimeFlow = Flow.fromGraph(new WindowProcessingTimerFlow(3200 milliseconds))
      val probe = TestProbe()

      import system.dispatcher

      simpleSource
        .via(windowProcessingTimeFlow)
        .runWith(Sink.seq).pipeTo(probe.ref)
      probe.expectMsg(10 seconds, Seq(1, 14, 30))
    }
  }
}
