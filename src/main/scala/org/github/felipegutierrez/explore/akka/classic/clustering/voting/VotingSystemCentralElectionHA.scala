package org.github.felipegutierrez.explore.akka.classic.clustering.voting

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import com.typesafe.config.ConfigFactory

/**
 * a voting system central election with High Availability using Akka cluster singleton
 */
object VotingSystemCentralElectionHA {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def startNode(port: Int) = {
    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port = $port
         |""".stripMargin)
      .withFallback(ConfigFactory.load("clustering/votingSystemSingleton.conf"))

    val system = ActorSystem("RTJVMCluster", config)

    // TODO 1: set up the cluster singleton
    val votingAggregatorActor = system.actorOf(ClusterSingletonManager.props(
      singletonProps = Props[VotingAggregator],
      terminationMessage = PoisonPill,
      ClusterSingletonManagerSettings(system)), "votingAggregatorActor")
  }

  (2551 to 2553).foreach(startNode)
}
