package org.github.felipegutierrez.explore.akka.classic.clustering.basic

import akka.actor.{ActorSystem, Address, Props}
import akka.cluster.Cluster
import akka.management.scaladsl.AkkaManagement
import com.typesafe.config.ConfigFactory

object ClusteringManualRegistration {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val system = ActorSystem("RTJVMCluster",
      ConfigFactory.load("clustering/clusteringBasics.conf")
        .getConfig("manualRegistration")
    )

    val cluster = Cluster(system)

    // Automatically loads Cluster Http Routes
    AkkaManagement(system).start()

    // joinExistingCluster
    //    cluster.joinSeedNodes(List(
    //      Address("akka", "RTJVMCluster", "localhost", 2551), // akka://RTJVMCluster@localhost:2551
    //      Address("akka", "RTJVMCluster", "localhost", 2552) // equivalent with AddressFromURIString("akka://RTJVMCluster@localhost:2552")
    //    ))

    // joinExistingNode
    // cluster.join(Address("akka", "RTJVMCluster", "localhost", 37215))

    // joinMyself
    cluster.join(Address("akka", "RTJVMCluster", "localhost", 2555))

    val clusterSubscriber = system.actorOf(Props[ClusterSubscriber], "clusterSubscriber")

    println("try:")
    println("http GET http://127.0.0.1:8558/cluster/members/")
    println("http GET http://127.0.0.1:8558/cluster/members/akka://RTJVMCluster@localhost:2555")
  }
}
