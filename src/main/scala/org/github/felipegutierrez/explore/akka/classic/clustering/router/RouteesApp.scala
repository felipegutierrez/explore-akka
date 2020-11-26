package org.github.felipegutierrez.explore.akka.classic.clustering.router

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object RouteesApp extends App {
  run()

  def run() = {
    startRouteeNode(2551)
    startRouteeNode(2552)
  }

  def startRouteeNode(port: Int) = {
    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port = $port
         |""".stripMargin)
      .withFallback(ConfigFactory.load("clustering/clusterAwareRouters.conf"))

    val system = ActorSystem("RTJVMCluster", config)
    system.actorOf(Props[WorkerRoutee], "worker")
  }
}
