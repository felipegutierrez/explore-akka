package org.github.felipegutierrez.explore.akka.classic.clustering.k8s

import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory

object SimpleClusterK8sMain {
  def run(hostname: String = "localhost") = {
    var value = ""
    if (hostname.isEmpty) {
      println(s"HOST_NAME: ${System.getenv().get("HOST_NAME")}")
      value =
        s"""
           |akka.remote.artery.canonical.hostname = ${System.getenv().get("HOST_NAME")}
       """.stripMargin
    } else {
      value =
        s"""
           |akka.remote.artery.canonical.hostname = ${hostname}.akka-seed
       """.stripMargin
    }
    val config = ConfigFactory.parseString(value)
      .withFallback(ConfigFactory.load("clustering/k8sClusteringBasics.conf"))
    // all Actor Systems in a cluster must have the same name
    val system = ActorSystem("K8sClusterSystem", config)
    val cluster = Cluster(system)

    // joinExistingCluster
    val addressSeq = System.getenv().get("SEED_NODES").split(",")
      .map(ip => Address("akka", "K8sClusterSystem", ip, 2551))
      .to[collection.immutable.Seq]
    cluster.joinSeedNodes(addressSeq)

    val simpleClusterK8sListener = system.actorOf(Props[SimpleClusterK8sListener], "simpleClusterK8sListener")
  }
}
