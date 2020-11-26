package org.github.felipegutierrez.explore.akka.classic.clustering.sharding

import akka.cluster.sharding.ShardRegion

/**
 * Sharding settings
 */
object TurnstileSettings {
  val numberOfShards = 10 // use 10x number of nodes in your cluster
  val numberOfEntities = 100 // 10x number of shards

  import TurnstileMessages._

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case attempt@EntryAttempt(OysterCard(cardId, _), _) =>
      val entityId = cardId.hashCode.abs % numberOfEntities
      (entityId.toString, attempt)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case EntryAttempt(OysterCard(cardId, _), _) =>
      val shardId = cardId.hashCode.abs % numberOfShards
      shardId.toString
    case ShardRegion.StartEntity(entityId) =>
      (entityId.toLong % numberOfShards).toString

    /**
     * There must be NO two messages M1 and M2 for which
     * extractEntityId(M1) == extractEntityId(M2) and extractShardId(M1) != extractShardId(M2)
     * OTHERWISE BAD. VERY BAD.
     * entityId -> shardId, then FORALL messages M, if extractEntityId(M) = entityId, then extractShardId(M) MUST BE shardId
     */
  }
}
