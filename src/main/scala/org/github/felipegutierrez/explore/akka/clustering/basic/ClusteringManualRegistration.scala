package org.github.felipegutierrez.explore.akka.clustering.basic

import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory

object ClusteringManualRegistration extends App {

  run()

  def run() = {
    val system = ActorSystem("RTJVMCluster",
      ConfigFactory.load("clustering/clusteringBasics.conf")
        .getConfig("manualRegistration"))

    val cluster = Cluster(system)

    // joinExistingCluster
    cluster.joinSeedNodes(List(
      Address("akka", "RTJVMCluster", "localhost", 2551), // akka://RTJVMCluster@localhost:2551
      Address("akka", "RTJVMCluster", "localhost", 2552) // equivalent with AddressFromURIString("akka://RTJVMCluster@localhost:2552")
    ))

    // joinExistingNode
    // cluster.join(Address("akka", "RTJVMCluster", "localhost", 37215))

    // joinMyself
    // cluster.join(Address("akka", "RTJVMCluster", "localhost", 2555))

    val clusterSubscriber = system.actorOf(Props[ClusterSubscriber], "clusterSubscriber")
  }
}
