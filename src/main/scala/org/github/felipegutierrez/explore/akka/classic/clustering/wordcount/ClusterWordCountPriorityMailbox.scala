package org.github.felipegutierrez.explore.akka.classic.clustering.wordcount

import akka.actor.ActorSystem
import akka.cluster.ClusterEvent.MemberEvent
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

/**
 * This is a priority mail box for the Cluster Akka Word Count that
 * makes it elastic in the sense of accepting new workers when the
 * computation is on going. The configuration is set on the
 * "master-dispatcher" property at clusteringExample.conf file
 *
 * @param settings
 * @param config
 */
class ClusterWordCountPriorityMailbox(settings: ActorSystem.Settings, config: Config)
  extends UnboundedPriorityMailbox(
    PriorityGenerator {
      case _: MemberEvent => 0
      case _ => 4
    }
  ) {

}
