package org.github.felipegutierrez.explore.akka.clustering.wordcount

import akka.actor.{ActorRef, Props}

object ClusteringWordCount extends App {

  object ClusteringExampleDomain {

    case class ProcessFile(filename: String)

    case class ProcessLine(line: String, aggregator: ActorRef)

    case class ProcessLineResult(count: Int)

  }

  run()

  def run() = {
    val master = SeedNodes.createNode(2551, "master", Props[WordCountMaster], "master")
    SeedNodes.createNode(2552, "worker", Props[WordCountWorker], "worker")
    SeedNodes.createNode(2553, "worker", Props[WordCountWorker], "worker")

    Thread.sleep(10000)
    import ClusteringExampleDomain._
    master ! ProcessFile("src/main/resources/txt/hamlet.txt")
  }
}
