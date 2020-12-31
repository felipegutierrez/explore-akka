package org.github.felipegutierrez.explore.akka.classic.clustering.voting

import akka.actor.ActorSystem
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.typesafe.config.ConfigFactory

class VotingStationApp(port: Int) {
  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    val config = ConfigFactory.parseString(
    s"""
       |akka.remote.artery.canonical.port = $port
       |""".stripMargin)
      .withFallback(ConfigFactory.load("clustering/votingSystemSingleton.conf"))

    val system = ActorSystem("RTJVMCluster", config)

    // TODO 2: set up the communication on the cluster singleton
    val votingSystemProxy = system.actorOf(
    ClusterSingletonProxy.props(
    singletonManagerPath = "/user/votingAggregatorActor",
    settings = ClusterSingletonProxySettings(system)
    ),
    "votingSystemProxy"
    )

    /**
     * The votingSystemProxy is the central-point actor.
     * It is potentially a bottleneck of the voting system because it is a singleton.
     * However, it has high availability.
     */
    val votingStationActor = system.actorOf(VotingStation.props(votingSystemProxy))

    // TODO 3: read lines from stdin

    import VoteMessages._

    scala.io.Source.stdin.getLines().foreach { line =>
      votingStationActor ! Vote(Person.generate(), line)
    }
  }
}

object Germany extends VotingStationApp(2561)

object France extends VotingStationApp(2562)

object Italy extends VotingStationApp(2563)
