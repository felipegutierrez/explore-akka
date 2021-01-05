package org.github.felipegutierrez.explore.akka.classic.clustering.k8s

import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory

object SimpleClusterK8sMain {
  def run(hostname: String = "localhost") = {
    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.hostname = ${hostname}.akka-seed
       """.stripMargin)
      .withFallback(ConfigFactory.load("clustering/k8sClusteringBasics.conf"))
    // all Actor Systems in a cluster must have the same name
    val system = ActorSystem("K8sClusterSystem", config)
    val cluster = Cluster(system)

    // joinExistingCluster
    val addressSeq = System.getenv().get("SEED_NODES").split(",")
      .map(ip => Address("akka", "K8sClusterSystem", ip, 2551))
      .to[collection.immutable.Seq]
    cluster.joinSeedNodes(addressSeq)

    //    cluster.joinSeedNodes(List(
    //      Address("akka", "K8sClusterSystem", "localhost", 2551), // akka://RTJVMCluster@localhost:2551
    //      Address("akka", "K8sClusterSystem", "localhost", 2552) // equivalent with AddressFromURIString("akka://RTJVMCluster@localhost:2552")
    //    ))

    val simpleClusterK8sListener = system.actorOf(Props[SimpleClusterK8sListener], "simpleClusterK8sListener")
  }
}
