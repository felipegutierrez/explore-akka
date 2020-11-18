package org.github.felipegutierrez.explore.akka.clustering.wordcount

import akka.actor.{Actor, ActorLogging}

class WordCountWorker extends Actor with ActorLogging {

  import ClusteringWordCount.ClusteringExampleDomain._

  override def receive: Receive = {
    case ProcessLine(line, aggregator) =>
      log.info(s"Processing: $line")
    case m => log.info(s"cannot handle message: $m")
  }
}
