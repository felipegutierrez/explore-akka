package org.github.felipegutierrez.explore.akka.classic.streams.advanced

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.stage._
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.concurrent.duration._

object StreamBatchFlowGraphOperators {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    val configString =
      """
        | akka {
        |   allow-java-serialization = on
        |   loglevel = "DEBUG"
        | }
      """.stripMargin
    val config = ConfigFactory.parseString(configString)
    implicit val system = ActorSystem("StreamBatchFlowGraphOperators", ConfigFactory.load(config))

    val source = Source(Stream.from(1)).throttle(5, 1 second)
    val sink = Sink.foreach { v: Int => println(s"sink: $v") }
    val batchFlow = Flow.fromGraph(new BatchFlow[Int](10))
    source.log("source")
      .via(batchFlow).log("batchFlow")
      .to(sink)
      .run()
  }

  // step 0: define the shape
  class BatchFlow[T](maxBatchSize: Int) extends GraphStage[FlowShape[T, T]] {
    // step 1: define the ports and the component-specific members
    val in = Inlet[T]("BatchFlow.in")
    val out = Outlet[T]("BatchFlow.out")

    // step 3: create the logic
    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
      // mutable state
      val batch = new mutable.Queue[T]

      // step 4: define mutable state implement my logic here
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          try {
            val nextElement = grab(in)
            batch.enqueue(nextElement)
            Thread.sleep(50) // simulate an expensive computation
            if (batch.size >= maxBatchSize) {
              // forward the element to the downstream operator
              emitMultiple(out, batch.dequeueAll(_ => true).to[collection.immutable.Iterable])
            } else {
              // send demand upstream signal, asking for another element
              pull(in)
            }
          } catch {
            case e: Throwable => failStage(e)
          }
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }

    // step 2: construct a new shape
    override def shape: FlowShape[T, T] = FlowShape[T, T](in, out)
  }

}
