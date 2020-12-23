package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{parameter, _}
import akka.pattern.ask
import akka.util.Timeout
import spray.json._
// step 1 - import spray json
import scala.concurrent.Future
import scala.concurrent.duration._

case class Player(nickname: String, characterClass: String, level: Int)

object GameAreaMap {

  case class GetPlayer(nickname: String)

  case class GetPlayerByClass(characterClass: String)

  case class AddPlayer(player: Player)

  case class RemovePlayer(player: Player)

  case object GetAllPlayers

  case object OperationSuccess

}

class GameAreaMap extends Actor with ActorLogging {

  import GameAreaMap._

  var players = Map[String, Player]()

  override def receive: Receive = {
    case GetAllPlayers =>
      log.info(s"getting all players")
      sender() ! players.values.toList
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

// step 2 - the JSON protocol
trait PlayerJsonProtocol extends DefaultJsonProtocol {
  implicit val plauerFormat = jsonFormat3(Player)
}

// step 3 - extend PlayerJsonProtocol
// step 4 - add sprayJsonSupport
object MarshallingJSON extends PlayerJsonProtocol with SprayJsonSupport {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run() = {
    implicit val system = ActorSystem("MarshallingJSON")
    import GameAreaMap._
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

    import GameAreaMap._
    implicit val defaultTimeout = Timeout(2 seconds)
    val gameRoutes =
      pathPrefix("api" / "player") {
        get {
          path("class" / Segment) { charaterClass =>
            // 1: get all players with characterClass
            val playersByClassFuture: Future[List[Player]] = (gameMap ? GetPlayerByClass(charaterClass)).mapTo[List[Player]]
            complete(playersByClassFuture)
          } ~ (path(Segment) | parameter('nickname)) { nickname =>
            // 2: get the player with the nickname
            val playersFuture: Future[Option[Player]] = (gameMap ? GetPlayer(nickname)).mapTo[Option[Player]]
            complete(playersFuture)
          } ~ pathEndOrSingleSlash {
            // 3: get all the players
            val allPlayersFuture: Future[List[Player]] = (gameMap ? GetAllPlayers).mapTo[List[Player]]
            complete(allPlayersFuture)
          }
        } ~ post {
          // 4: add a player
          entity(as[Player]) { player =>
            complete((gameMap ? AddPlayer(player)).map(_ => StatusCodes.OK))
          }
        } ~ delete {
          // 5: delete a player
          entity(as[Player]) { player =>
            complete((gameMap ? RemovePlayer(player)).map(_ => StatusCodes.OK))
          }
        }
      }

    println("http GET localhost:8080/api/player")
    println("http GET localhost:8080/api/player/class/Warrior")
    println("http GET localhost:8080/api/player/class/Elf")
    println("http GET localhost:8080/api/player/class/Wizard")
    println("http GET localhost:8080/api/player/felipeoguierrez")
    println("http GET localhost:8080/api/player?nickname=rolandbraveheart")
    println("http POST localhost:8080/api/player < src/main/resources/json/player.json")
    println("http GET localhost:8080/api/player")
    println("http DELETE localhost:8080/api/player < src/main/resources/json/player.json")
    Http()
      .newServerAt("localhost", 8080)
      .bindFlow(gameRoutes)
  }
}
