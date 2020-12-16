package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.stream.stage._
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}

import scala.concurrent.duration.FiniteDuration

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
