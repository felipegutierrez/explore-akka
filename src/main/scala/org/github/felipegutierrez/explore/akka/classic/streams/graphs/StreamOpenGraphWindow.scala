package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import akka.stream.stage._

import scala.concurrent.duration._

object StreamOpenGraphWindow {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphWindow")

    val sourceNegative = Source(Stream.from(0, -1)).throttle(1, 1 second)
    val sourcePositive = Source(Stream.from(0)).throttle(1, 1 second)

    // Step 1 - setting up the fundamental for a stream graph
    val windowRunnableGraph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._
        // Step 2 - create shapes
        val mergeShape = builder.add(Merge[Int](2))
        val windowTimeFlow = Flow.fromGraph(new WindowProcessingTimerFlow(5000 millisecond))
        val windowEventFlow = Flow.fromGraph(new WindowEventFlow(10))
        // val windowFlowShape = builder.add(windowTimeFlow)
        val windowFlowShape = builder.add(windowEventFlow)
        val sinkShape = builder.add(Sink.foreach[Int](x => println(s"\nsink: $x")))

        // Step 3 - tying up the components
        sourceNegative ~> mergeShape.in(0)
        sourcePositive ~> mergeShape.in(1)
        mergeShape.out ~> windowFlowShape ~> sinkShape

        // Step 4 - return the shape
        ClosedShape
      }
    )
    // run the graph and materialize it
    val graph = windowRunnableGraph.run()
  }

  // step 0: define the shape
  class WindowProcessingTimerFlow(silencePeriod: FiniteDuration) extends GraphStage[FlowShape[Int, Int]] {
    // step 1: define the ports and the component-specific members
    val in = Inlet[Int]("WindowProcessingTimerFlow.in")
    val out = Outlet[Int]("WindowProcessingTimerFlow.out")

    // step 3: create the logic
    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) {
      // mutable state
      var sum: Int = 0
      var open = false
      var count = 0
      // step 4: define mutable state implement my logic here
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          try {
            val nextElement = grab(in)
            print(s"time($count)[$sum,$nextElement] | ")
            sum = sum + nextElement
            count += 1
            if (open) {
              pull(in) // send demand upstream signal, asking for another element
            } else {
              push(out, sum)
              open = true
              sum = 0
              count = 0
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
    override def shape: FlowShape[Int, Int] = FlowShape[Int, Int](in, out)
  }

  // step 0: define the shape
  class WindowEventFlow(maxBatchSize: Int) extends GraphStage[FlowShape[Int, Int]] {
    // step 1: define the ports and the component-specific members
    val in = Inlet[Int]("WindowEventFlow.in")
    val out = Outlet[Int]("WindowEventFlow.out")

    // step 3: create the logic
    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
      // mutable state
      var sum: Int = 0
      var count = 0
      // step 4: define mutable state implement my logic here
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          try {
            val nextElement = grab(in)
            print(s"event($count)[$sum,$nextElement] | ")
            sum = sum + nextElement
            count += 1
            if (count >= maxBatchSize) {
              push(out, sum)
              sum = 0
              count = 0
            } else {
              pull(in) // send demand upstream signal, asking for another element
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
    override def shape: FlowShape[Int, Int] = FlowShape[Int, Int](in, out)
  }

}
