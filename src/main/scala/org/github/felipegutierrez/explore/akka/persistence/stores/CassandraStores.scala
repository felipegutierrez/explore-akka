package org.github.felipegutierrez.explore.akka.persistence.stores

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
 * Start Cassandra
 * $ cd scripts/akka-persistence/
 * $ docker-compose up
 * $ ./cqlsh.sh
 * ==================     Help for cqlsh    =========================
 * DESCRIBE tables            : Prints all tables in the current keyspace
 * DESCRIBE keyspaces         : Prints all keyspaces in the current cluster
 * DESCRIBE <table_name>      : Prints table detail information
 * help                       : for more cqlsh commands
 * help [cqlsh command]       : Gives information about cqlsh commands
 * quit                       : quit
 * ==================================================================
 * Connected to OUR_DOCKERIZED_CASSANDRA_SINGLE_NODE_CLUSTER at 127.0.0.1:9042.
 * [cqlsh 5.0.1 | Cassandra 3.11.9 | CQL spec 3.4.4 | Native protocol v4]
 * Use HELP for help.
 * cqlsh>
 * cqlsh> select * from akka.messages;
 *
 * persistence_id          | partition_nr | sequence_nr | timestamp                            | timebucket    | used | event                          | event_manifest | message | meta | meta_ser_id | meta_ser_manifest | ser_id | ser_manifest | tags | writer_uuid
 * -------------------------+--------------+-------------+--------------------------------------+---------------+------+--------------------------------+----------------+---------+------+-------------+-------------------+--------+--------------+------+--------------------------------------
 * simple-persistent-actor |            0 |           1 | 7137ba50-2f0d-11eb-bb5e-7dbe0752d467 | 1606302000000 | True |   0x69206c6f766520616b6b612031
 *
 * cqlsh> select * from akka_snapshot.snapshots;
 *
 * persistence_id          | sequence_nr | meta | meta_ser_id | meta_ser_manifest | ser_id | ser_manifest | snapshot | snapshot_data | timestamp
 * -------------------------+-------------+------+-------------+-------------------+--------+--------------+----------+---------------+---------------
 * simple-persistent-actor |          30 | null |        null |              null |     19 |              |     null |    0x1e000000 | 1606302085064
 */
object CassandraStores extends App {

  run()

  def run() = {
    val cassandraStoreSystem = ActorSystem("cassandraStoreSystem", ConfigFactory.load().getConfig("cassandraStore"))
    val persistentActor = cassandraStoreSystem.actorOf(Props[SimplePersistentActor], "persistentActor")

    for (i <- 1 to 10) {
      persistentActor ! s"i love akka $i"
    }
    persistentActor ! "print"
    persistentActor ! "snapshot"
    for (i <- 11 to 20) {
      persistentActor ! s"i love akka $i"
    }
  }

}
