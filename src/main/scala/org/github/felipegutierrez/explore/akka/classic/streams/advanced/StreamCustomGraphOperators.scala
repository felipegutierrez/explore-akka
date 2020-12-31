package org.github.felipegutierrez.explore.akka.classic.streams.advanced

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.stage._
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.util.Random

object StreamCustomGraphOperators {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val configString =
      """
        | akka {
        |   allow-java-serialization = on
        |   loglevel = "DEBUG"
        | }
      """.stripMargin
    val config = ConfigFactory.parseString(configString)
    implicit val system = ActorSystem("StreamCustomGraphOperators", ConfigFactory.load(config))

    val randomGeneratorSource = Source.fromGraph(new RandomNumberGenerator(1000))
    // randomGeneratorSource.runWith(Sink.foreach(println))
    val batchSink = Sink.fromGraph(new BatchSink(10))
    // randomGeneratorSource.to(batchSink).run()
    val filterFlow = Flow.fromGraph(new FilterFlow[Int](_ % 2 == 0))
    randomGeneratorSource
      .via(filterFlow)
      .to(batchSink)
      .run()

    val counterFlow = Flow.fromGraph(new CounterFlow[Int])
    //    val countFuture = Source(1 to 10)
    //      // .map(x => if (x == 7) throw new RuntimeException("gotcha!") else x)
    //      .viaMat(counterFlow)(Keep.right)
    //      // .to(Sink.foreach(x => if (x == 7) throw new RuntimeException("gotcha, sink!") else println(x)))
    //      .to(Sink.foreach[Int](println))
    //      .run()
    //    import system.dispatcher
    //    countFuture.onComplete {
    //      case Success(count) => println(s"The number of elements passed: $count")
    //      case Failure(ex) => println(s"Counting the elements failed: $ex")
    //    }
  }

  // 1 - a custom source which emits random numbers until canceled
  /** step 0: define the shape */
  class RandomNumberGenerator(max: Int) extends GraphStage[SourceShape[Int]] {
    // step 1: define the ports and the component-specific members
    val outPort = Outlet[Int]("randomGenerator")
    val random = new Random()

    // step 3: create the logic
    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
      // step 4: define mutable state implement my logic here
      setHandler(outPort, new OutHandler {
        // when there is demand from downstream emit a new element
        override def onPull(): Unit = {
          push(outPort, random.nextInt(max))
        }
      })
    }

    // step 2: construct a new shape
    override def shape: SourceShape[Int] = SourceShape(outPort)
  }

  class BatchSink(maxBatchSize: Int) extends GraphStage[SinkShape[Int]] {
    val inPort = Inlet[Int]("batch")

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
      // mutable state
      val batch = new mutable.Queue[Int]

      override def preStart(): Unit = pull(inPort)

      setHandler(inPort, new InHandler {
        // when the upstream wants to send some element to me
        override def onPush(): Unit = {
          val nextElement = grab(inPort)
          batch.enqueue(nextElement)

          Thread.sleep(100)
          if (batch.size >= maxBatchSize) {
            println("New batch: " + batch.dequeueAll(_ => true).mkString("[", ", ", "]"))
          }
          // send demand upstream signal
          pull(inPort)
        }

        override def onUpstreamFinish(): Unit = {
          if (batch.nonEmpty) {
            println("New batch: " + batch.dequeueAll(_ => true).mkString("[", ", ", "]"))
            println("Stream finished.")
          }
        }
      })
    }

    override def shape: SinkShape[Int] = SinkShape[Int](inPort)
  }

  /**
   * Exercise: a custom flow - a simple filter flow
   * - 2 ports: an input port and an output port
   */
  class FilterFlow[T](predicate: T => Boolean) extends GraphStage[FlowShape[T, T]] {
    // step 1: define the ports and the component-specific members
    val in = Inlet[T]("Filter.in")
    val out = Outlet[T]("Filter.out")

    // step 3: create the logic
    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
      // override def preStart(): Unit = pull(inPort)
      // step 4: define mutable state implement my logic here
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          try {
            val elem = grab(in)
            if (predicate(elem)) {
              // forward the element to the downstream operator
              push(out, elem)
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

  /**
   * Graph stage with materialized values
   */
  class CounterFlow[T] extends GraphStageWithMaterializedValue[FlowShape[T, T], Future[Int]] {

    override val shape = FlowShape(inPort, outPort)
    val inPort = Inlet[T]("counterInt")
    val outPort = Outlet[T]("counterOut")

    override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, Future[Int]) = {

      val promise = Promise[Int]
      val logic = new GraphStageLogic(shape) {
        // setting mutable state
        var counter = 0

        setHandler(outPort, new OutHandler {
          override def onPull(): Unit = pull(inPort)

          override def onDownstreamFinish(): Unit = {
            promise.success(counter)
            super.onDownstreamFinish()
          }
        })

        setHandler(inPort, new InHandler {
          override def onPush(): Unit = {
            // extract the element
            val nextElement = grab(inPort)
            counter += 1
            // pass it on
            push(outPort, nextElement)
          }

          override def onUpstreamFinish(): Unit = {
            promise.success(counter)
            super.onUpstreamFinish()
          }

          override def onUpstreamFailure(ex: Throwable): Unit = {
            promise.failure(ex)
            super.onUpstreamFailure(ex)
          }
        })
      }

      (logic, promise.future)
    }
  }

}
