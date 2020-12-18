package org.github.felipegutierrez.explore.akka.classic.streams.monitoring

import akka.stream.stage._
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import kamon.Kamon
import kamon.metric.Metric

import scala.concurrent.duration._

/**
 * A Flow to monitor the throughput of a stream and export metrics to Kamon. Prometheus has to be configured to scrape Kamon metrics.
 *
 * @param interval
 * @tparam T
 */
// step 0: define the shape
class MonitorProcessingTimerFlow[T](interval: FiniteDuration)(metricName: String = "monitorFlow") extends GraphStage[FlowShape[T, T]] {
  // step 1: define the ports and the component-specific members
  val in = Inlet[T]("MonitorProcessingTimerFlow.in")
  val out = Outlet[T]("MonitorProcessingTimerFlow.out")

  // Kamon configuration
  Kamon.init()
  val kamonThroughputGauge: Metric.Gauge = Kamon.gauge("akka-stream-throughput-monitor")

  // step 3: create the logic
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) {
    // mutable state
    var open = false
    var count = 0
    var start = System.nanoTime
    // step 4: define mutable state implement my logic here
    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        try {
          push(out, grab(in))
          count += 1

          if (!open) {
            open = true
            scheduleOnce(None, interval)
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
      val duration = (System.nanoTime - start) / 1e9d
      val throughput = count / duration
      kamonThroughputGauge.withTag("name", metricName).update(throughput)
      println(s"duration: $duration | throughput: $throughput")
      count = 0
      start = System.nanoTime
    }
  }

  // step 2: construct a new shape
  override def shape: FlowShape[T, T] = FlowShape[T, T](in, out)
}
