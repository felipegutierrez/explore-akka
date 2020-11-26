package org.github.felipegutierrez.explore.akka.classic.persistence.query

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.journal.{Tagged, WriteEventAdapter}
import akka.persistence.query.{Offset, PersistenceQuery}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.util.Random
import scala.concurrent.duration._

object PersistenceCassandraQuery extends App {

  run()

  def run() = {
    val system = ActorSystem("PersistenceCassandraQuery", ConfigFactory.load().getConfig("persistenceCassandraQuery"))

    // read journal from Cassandra DB
    val readJournal = PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

    // select all IDs from Cassandra
    val persistenceIds = readJournal.persistenceIds()

    implicit val materializer = ActorMaterializer()(system)
    persistenceIds.runForeach { persistenceId =>
      println(s"Fount persistence ID: $persistenceId")
    }

    val simpleActor = system.actorOf(Props[SimplePersistentActor], "simplePersistentActor")

    import system.dispatcher
      system.scheduler.scheduleOnce(5 seconds) {
        val message = "hello a second time"
        simpleActor ! message
      }

    // events by persistence ID
    val events = readJournal.eventsByPersistenceId("persistence-query-id-1", 0, Long.MaxValue)
    events.runForeach { event =>
      println(s"Read event: $event")
    }


    // events by tags
    val genres = Array("pop", "rock", "hip-hop", "jazz", "disco")

    val checkoutActor = system.actorOf(Props[MusicStoreCheckoutActor], "musicStoreActor")

    val r = new Random
    for (_ <- 1 to 10) {
      val maxSongs = r.nextInt(5)
      val songs = for (i <- 1 to maxSongs) yield {
        val randomGenre = genres(r.nextInt(5))
        Song(s"Artist $i", s"My Love Song $i", randomGenre)
      }

      checkoutActor ! Playlist(songs.toList)
    }

    val rockPlaylists = readJournal.eventsByTag("rock", Offset.noOffset)
    rockPlaylists.runForeach { event =>
      println(s"Found a playlist with a rock song: $event")
    }
  }


  case class Song(artist: String, title: String, genre: String)

  // command
  case class Playlist(songs: List[Song])

  // event
  case class PlaylistPurchased(id: Int, songs: List[Song])

  class SimplePersistentActor extends PersistentActor with ActorLogging {
    override def persistenceId: String = "persistence-query-id-1"

    override def receiveCommand: Receive = {
      case m => persist(m) { _ =>
        log.info(s"Persisted: $m")
      }
    }

    override def receiveRecover: Receive = {
      case e => log.info(s"Recovered: $e")
    }
  }

  class MusicStoreCheckoutActor extends PersistentActor with ActorLogging {
    var latestPlaylistId = 0

    override def persistenceId: String = "music-store-checkout"

    override def receiveCommand: Receive = {
      case Playlist(songs) =>
        persist(PlaylistPurchased(latestPlaylistId, songs)) { _ =>
          log.info(s"User purchased: $songs")
          latestPlaylistId += 1
        }
    }

    override def receiveRecover: Receive = {
      case event@PlaylistPurchased(id, _) =>
        log.info(s"Recovered: $event")
        latestPlaylistId = id
    }
  }

  class MusicStoreEventAdapter extends WriteEventAdapter {
    override def manifest(event: Any): String = "musicStore"

    override def toJournal(event: Any): Any = event match {
      case event@PlaylistPurchased(_, songs) =>
        val genres = songs.map(_.genre).toSet
        Tagged(event, genres)
      case event => event
    }
  }

}
