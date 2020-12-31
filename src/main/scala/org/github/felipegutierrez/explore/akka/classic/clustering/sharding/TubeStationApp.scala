package org.github.felipegutierrez.explore.akka.classic.clustering.sharding

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import com.typesafe.config.ConfigFactory

import scala.util.Random

class TubeStationApp(port: Int, numberOfTurnstiles: Int) {
  val config = ConfigFactory.parseString(
    s"""
       |akka.remote.artery.canonical.port = $port
       |""".stripMargin)
    .withFallback(ConfigFactory.load("clustering/clusteringSharding.conf"))
  val system = ActorSystem("RTJVMCluster", config)

  /** setting up the cluster sharding.
   * This returns the sharding region that will be deployed on this node */
  val validatorShardingRegionRef: ActorRef = ClusterSharding(system).start(
    typeName = "OysterCardValidator",
    entityProps = Props[OysterCardValidatorActor],
    settings = ClusterShardingSettings(system).withRememberEntities(true),
    extractEntityId = TurnstileSettings.extractEntityId,
    extractShardId = TurnstileSettings.extractShardId
  )

  val turnstileActors: IndexedSeq[ActorRef] = (1 to numberOfTurnstiles).map(_ => system.actorOf(TurnstileActor.props(validatorShardingRegionRef)))

  import TurnstileMessages._

  Thread.sleep(10000)
  for (_ <- 1 to 1000) {
    val randomTurnstileIndex = Random.nextInt(numberOfTurnstiles)
    val randomTurnstileActor = turnstileActors(randomTurnstileIndex)
    randomTurnstileActor ! OysterCard(UUID.randomUUID().toString, Random.nextDouble() * 10)
    Thread.sleep(200)
  }
}

object PiccadillyCircus extends TubeStationApp(2551, 10)

object Westminster extends TubeStationApp(2561, 5)

object CharingCross extends TubeStationApp(2571, 15)
