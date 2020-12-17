package org.github.felipegutierrez.explore.akka.classic.streams.graphs

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Random

object WindowGroupEventFlow {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("WindowGroupEventFlow")
    
    val sourceA = Source.fromGraph(new RandomEventGenerator(20)).throttle(10, 1 second)
    val sourceB = Source.fromGraph(new RandomEventGenerator(20)).throttle(25, 1 second)
    val sourceC = Source.fromGraph(new RandomEventGenerator(20)).throttle(50, 1 second)

    // Step 1 - setting up the fundamental for a stream graph
    val windowRunnableGraph = RunnableGraph.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._
        // Step 2 - create shapes
        val mergeShape = builder.add(Merge[Domain.Z](3))
        val windowEventFlow = Flow.fromGraph(new WindowGroupEventFlow(50))
        val windowFlowShape = builder.add(windowEventFlow)
        val sinkShape = builder.add(Sink.foreach[Domain.Z](x => println(s"sink: $x")))

        // Step 3 - tying up the components
        sourceA ~> mergeShape.in(0)
        sourceB ~> mergeShape.in(1)
        sourceC ~> mergeShape.in(2)
        mergeShape.out ~> windowFlowShape ~> sinkShape

        // Step 4 - return the shape
        ClosedShape
      }
    )
    // run the graph and materialize it
    val graph = windowRunnableGraph.run()
  }
}

object Domain {

  sealed abstract class Z(val id: Int, val value: String)

  case class A(override val id: Int, override val value: String = "A") extends Z(id, value)

  case class B(override val id: Int, override val value: String = "B") extends Z(id, value)

  case class C(override val id: Int, override val value: String = "C") extends Z(id, value)

  case class ABC(override val id: Int, override val value: String) extends Z(id, value)

}

// step 0: define the shape
class WindowGroupEventFlow(maxBatchSize: Int) extends GraphStage[FlowShape[Domain.Z, Domain.Z]] {
  // step 1: define the ports and the component-specific members
  val in = Inlet[Domain.Z]("WindowGroupEventFlow.in")
  val out = Outlet[Domain.Z]("WindowGroupEventFlow.out")

  // step 3: create the logic
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    // mutable state
    val map = mutable.Map[Int, Domain.Z]()
    var count = 0
    // step 4: define mutable state implement my logic here
    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        try {
          val nextElement = grab(in)
          // batch.enqueue(nextElement)
          add(nextElement)
          count += 1

          // If window finished we have to dequeue all elements
          if (count >= maxBatchSize) {
            println("************ window finished - dequeuing elements ************")
            val result: collection.immutable.Iterable[Domain.Z] = map.map { pair =>
              pair._2
            }.to[collection.immutable.Iterable]
            map.clear()
            emitMultiple(out, result)
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

    def add(element: Domain.Z) = {
      if (map.contains(element.id)) {
        val currentElement = map.get(element.id).get
        map += (element.id -> Domain.ABC(element.id, currentElement.value + element.value))
      } else {
        map += (element.id -> Domain.ABC(element.id, element.value))
      }
    }
  }

  // step 2: construct a new shape
  override def shape: FlowShape[Domain.Z, Domain.Z] = FlowShape[Domain.Z, Domain.Z](in, out)
}

// 1 - a custom source which emits random numbers until canceled
class RandomEventGenerator(max: Int) extends GraphStage[SourceShape[Domain.Z]] {
  // step 1: define the ports and the component-specific members
  val outPort = Outlet[Domain.Z]("randomGenerator")
  val random = new Random()
  var count = 0

  // step 3: create the logic
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    // step 4: define mutable state implement my logic here
    setHandler(outPort, new OutHandler {
      // when there is demand from downstream emit a new element
      override def onPull(): Unit = {
        if (count == 0) {
          push(outPort, Domain.A(random.nextInt(max)))
          count += 1
        } else if (count == 1) {
          push(outPort, Domain.B(random.nextInt(max)))
          count += 1
        } else if (count == 2) {
          push(outPort, Domain.C(random.nextInt(max)));
          count = 0
        }
      }
    })
  }

  // step 2: construct a new shape
  override def shape: SourceShape[Domain.Z] = SourceShape(outPort)
}
