package org.github.felipegutierrez.explore.akka.classic.streams.advanced

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.stage._
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.concurrent.duration._

object StreamBatchTimerFlowGraphOperators {
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
    implicit val system = ActorSystem("StreamBatchTimerFlowGraphOperators", ConfigFactory.load(config))

    val source = Source(Stream.from(1)).throttle(5, 1 second)
    val sink = Sink.foreach { v: Int => println(s"sink: $v") }
    val batchFlow = Flow.fromGraph(new BatchTimerFlow[Int](5 seconds))
    source.log("source")
      .via(batchFlow).log("batchTimerFlow")
      .to(sink)
      .run()
  }

  // step 0: define the shape
  class BatchTimerFlow[T](silencePeriod: FiniteDuration) extends GraphStage[FlowShape[T, T]] {
    // step 1: define the ports and the component-specific members
    val in = Inlet[T]("BatchTimerFlow.in")
    val out = Outlet[T]("BatchTimerFlow.out")

    // step 3: create the logic
    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) {
      // mutable state
      val batch = new mutable.Queue[T]
      var open = false

      // step 4: define mutable state implement my logic here
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          try {
            val nextElement = grab(in)
            batch.enqueue(nextElement)
            Thread.sleep(50) // simulate an expensive computation
            if (open) pull(in) // send demand upstream signal, asking for another element
            else {
              // forward the element to the downstream operator
              emitMultiple(out, batch.dequeueAll(_ => true).to[collection.immutable.Iterable])
              open = true
              scheduleOnce(None, silencePeriod)
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

      override protected def onTimer(timerKey: Any): Unit = {
        open = false
      }
    }

    // step 2: construct a new shape
    override def shape: FlowShape[T, T] = FlowShape[T, T](in, out)
  }

}
