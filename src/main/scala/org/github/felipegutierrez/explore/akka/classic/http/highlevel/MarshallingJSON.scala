package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import org.github.felipegutierrez.explore.akka.classic.http.highlevel.GameAreaMap.AddPlayer

case class Player(nickname: String, characterClass: String, level: Int)

object GameAreaMap {
  case object GetAllPlayers
  case class GetPlayer(nickname: String)
  case class GetPlayerByClass(characterClass: String)
  case class AddPlayer(player: Player)
  case class RemovePlayer(player: Player)
  case object OperationSuccess
}

class GameAreaMap extends Actor with ActorLogging {
  import GameAreaMap._
  var players = Map[String, Player]()

  override def receive: Receive = {
    case GetAllPlayers =>
      log.info(s"getting all players")
      sender()! players.values.toList
    case GetPlayer(nickname) =>
      log.info(s"getting player with nickname $nickname")
      sender() ! players.get(nickname)
    case GetPlayerByClass(characterClass) =>
      log.info(s"getting all players with the character class $characterClass")
      sender() ! players.values.toList.filter(_.characterClass == characterClass)
    case AddPlayer(player) =>
      log.info(s"trying to add player $player")
      players = players + (player.nickname -> player)
      sender() ! OperationSuccess
    case RemovePlayer(player) =>
      log.info(s"trying to remove player $player")
      players = players - player.nickname
      sender() ! OperationSuccess
  }
}

object MarshallingJSON {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("MarshallingJSON")
    import system.dispatcher

    val gameMap = system.actorOf(Props[GameAreaMap], "gameMap")

    // boot strap some players
    val players = List(Player("rolandbraveheart", "Elf", 76), Player("felipeoguierrez", "Wizard", 30), Player("daniel", "Warrior", 55))
    players.foreach { player =>
      gameMap ! AddPlayer(player)
    }

    /*
      - GET /api/player, returns all the players in the map, as JSON
      - GET /api/player/(nickname), returns the player with the given nickname (as JSON)
      - GET /api/player?nickname=X, does the same
      - GET /api/player/class/(charClass), returns all the players with the given character class
      - POST /api/player with JSON payload, adds the player to the map
      - (Exercise) DELETE /api/player with JSON payload, removes the player from the map
     */
    // val gameRoutes =
  }
}
