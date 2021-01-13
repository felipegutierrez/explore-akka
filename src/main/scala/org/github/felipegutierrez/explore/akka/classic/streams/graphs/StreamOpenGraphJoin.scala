package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{Attributes, ClosedShape, FlowShape, Inlet, Outlet}
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler, TimerGraphStageLogic}

import scala.collection.mutable
import scala.concurrent.duration._

object StreamOpenGraphJoin {

  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("StreamOpenGraphJoin")

    val incrementSource: Source[Int, NotUsed] = Source(1 to 10).throttle(1, 1 second)
    val decrementSource: Source[Int, NotUsed] = Source(10 to 20).throttle(1, 1 second)

    def tokenizerSource(key: Int) = {
      Flow[Int].map { value =>
        (key, value)
      }
    }

    // Step 1 - setting up the fundamental for a stream graph
    val switchJoinStrategies = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        // Step 2 - add partition and merge strategy
        val tokenizerShape00 = builder.add(tokenizerSource(0))
        val tokenizerShape01 = builder.add(tokenizerSource(1))

        val mergeTupleShape = builder.add(Merge[(Int, Int)](2))
        val batchFlow = Flow.fromGraph(new BatchTimerFlow[(Int, Int)](5 seconds))
        val sinkShape = builder.add(Sink.foreach[(Int, Int)](x => println(s" > sink: $x")))

        // Step 3 - tying up the components
        incrementSource ~> tokenizerShape00 ~> mergeTupleShape.in(0)
        decrementSource ~> tokenizerShape01 ~> mergeTupleShape.in(1)
        mergeTupleShape.out ~> batchFlow ~> sinkShape

        // Step 4 - return the shape
        ClosedShape
      }
    )
    // run the graph and materialize it
    val graph = switchJoinStrategies.run()
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
