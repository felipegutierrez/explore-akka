package org.github.felipegutierrez.explore.akka.clustering.wordcount

import akka.actor.{Actor, ActorLogging, ReceiveTimeout}

import scala.concurrent.duration._

class WordCountAggregator extends Actor with ActorLogging {

  import ClusteringWordCount.ClusteringExampleDomain._

  context.setReceiveTimeout(3 seconds)

  override def receive: Receive = online(0)

  def online(totalCount: Int): Receive = {
    case ProcessLineResult(count) =>
      context.become(online(totalCount + count))
    case ReceiveTimeout =>
      log.info(s"TOTAL COUNT: $totalCount")
      context.setReceiveTimeout(Duration.Undefined)
  }
}
