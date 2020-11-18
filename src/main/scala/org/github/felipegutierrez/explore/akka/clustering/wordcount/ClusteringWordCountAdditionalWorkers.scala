package org.github.felipegutierrez.explore.akka.clustering.wordcount

import akka.actor.Props

object ClusteringWordCountAdditionalWorkers extends App {

  run()

  def run() = {
    /** create 2 additional workers to join after the computation already started */
    SeedNodes.createNode(2554, "worker", Props[WordCountWorker], "worker")
    SeedNodes.createNode(2555, "worker", Props[WordCountWorker], "worker")
  }
}
