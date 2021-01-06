package org.github.felipegutierrez.explore.akka.classic.clustering.basic

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object ClusteringBasics {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    startCluster(List(2551, 2552, 0))
  }

  def startCluster(ports: List[Int]): Unit = {
    ports.foreach { port =>
      val config = ConfigFactory.parseString(
        s"""
           |akka.remote.artery.canonical.port = $port
           """.stripMargin)
        .withFallback(ConfigFactory.load("clustering/clusteringBasics.conf"))

      // all Actor Systems in a cluster must have the same name
      val clusterSystem = ActorSystem("RTJVMCluster", config)
      clusterSystem.actorOf(Props[ClusterSubscriber], "clusterSubscriber")
    }
  }
}
