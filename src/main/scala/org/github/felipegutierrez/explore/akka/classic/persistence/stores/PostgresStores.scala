package org.github.felipegutierrez.explore.akka.classic.persistence.stores

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
 * Start the PostgreSQL
 * $ cd scripts/akka-persistence/
 * $ docker-compose up
 * $ ./psql.sh
 * ==================     Help for psql   =========================
 * \dt		: Describe the current database
 * \t [table]	: Describe a table
 * \c		: Connect to a database
 * \h		: help with SQL commands
 * \?		: help with psql commands
 * \q		: quit
 * ==================================================================
 * psql (13.1 (Debian 13.1-1.pgdg100+1))
 * Type "help" for help.
 *
 * rtjvm=# select * from journal;
 * ordering | persistence_id | sequence_number | deleted | tags | message
 * ----------+----------------+-----------------+---------+------+---------
 * (0 rows)
 * rtjvm=# select * from snapshot;
 * persistence_id      | sequence_number |    created    |          snapshot
 * -------------------------+-----------------+---------------+----------------------------
 * (0 row)
 */
object PostgresStores extends App {

  run()

  def run() = {
    val postgresStoreSystem = ActorSystem("postgresStoreSystem", ConfigFactory.load().getConfig("postgresStore"))
    val persistentActor = postgresStoreSystem.actorOf(SimplePersistentActor.props("postgres-actor"), "persistentActor")

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
