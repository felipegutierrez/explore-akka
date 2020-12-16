package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class WindowEventFlowSpec extends TestKit(ActorSystem("WindowEventFlowSpec"))
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "The WindowEventFlow that aggregates every 5 events" should {
    "sum the values correct" in {
      // describe our test

      val simpleSource = Source[Int](1 to 10)
      val windowEventFlow = Flow.fromGraph(new WindowEventFlow(5))
      // val simpleSink = Sink.foreach[Int](x => x)
      val testSink = TestSink.probe[Int]
      // val simpleSink = Sink.fold(0)((a: Int, b: Int) => a + b)

      val materializedTestValue = simpleSource
        .via(windowEventFlow)
        .runWith(testSink)
      materializedTestValue
        .request(2)
        .expectNext(15, 40)
        .expectComplete()
    }
  }
}
