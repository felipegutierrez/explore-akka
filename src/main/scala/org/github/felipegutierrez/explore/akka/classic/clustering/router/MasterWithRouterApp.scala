package org.github.felipegutierrez.explore.akka.classic.clustering.router

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object MasterWithRouterApp {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val config = ConfigFactory
      .load("clustering/clusterAwareRouters.conf")
      // .getConfig("masterWithRouterApp")
      .getConfig("masterWithGroupRouterApp")
      .withFallback(ConfigFactory.load("clustering/clusterAwareRouters.conf"))

    val system = ActorSystem("RTJVMCluster", config)
    val master = system.actorOf(Props[MasterWithRouter], "master")

    Thread.sleep(10000)
    master ! StartWork
  }
}
