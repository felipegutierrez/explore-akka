package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}

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
