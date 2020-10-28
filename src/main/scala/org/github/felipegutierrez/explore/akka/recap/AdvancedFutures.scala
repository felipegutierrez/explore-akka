package org.github.felipegutierrez.explore.akka.recap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Random, Success, Try}

object AdvancedFutures extends App {

  run()

  def run() = {

    val advancedFutures = new AdvancedFutures()
    println(advancedFutures.createTheFuture())

    val res = advancedFutures.callTheFuture() // : Future[Int]
    res.onComplete {
      case Success(meaningOfLife) => println(s"meaning of the life: $meaningOfLife")
      case Failure(e) => println(s"fail: $e")
    }

    println()

    // client: mark to poke bill
    val mark: Future[Profile] = SocialNetwork.fetchProfile("fb.id.1-zuck")
    // this is not a best practice because of the nested future calls onComplete
    mark.onComplete {
      case Success(markProfile) => {
        val bill = SocialNetwork.fetchBestFriend(markProfile)
        bill.onComplete {
          case Success(billProfile) => markProfile.poke(billProfile)
          case Failure(e) => e.printStackTrace()
        }
      }
      case Failure(ex) => ex.printStackTrace()
    }
    Thread.sleep(5000)

    println()
    println("we can do the above nested future cases using map and flatmap")
    val nameOnTheWall: Future[String] = mark.map(profile => profile.name)
    nameOnTheWall.onComplete {
      case Success(value) => println(s"name on the wall: $value")
      case Failure(exception) => println(s"name on the wall fail: $exception")
    }
    val markBestFriend: Future[Profile] = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
    val zuckBestFriendRestricted: Future[Profile] = markBestFriend.filter(profile => profile.name.startsWith("Z"))
    zuckBestFriendRestricted.onComplete {
      case Success(value) => println(s"zuckBestFriendRestricted: ${value.name}")
      case Failure(exception) => println(s"zuckBestFriendRestricted fail: $exception")
    }
    Thread.sleep(5000)

    println()
    println("even better --> we can use for-comprehensions")
    for {
      mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
      bill <- SocialNetwork.fetchBestFriend(mark)
    } mark.poke(bill)
    Thread.sleep(5000)

    println()
    println("fallbacks")
    val aProfileNoMatterWhat: Future[Profile] = SocialNetwork.fetchProfile("unknown id").recover {
      case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
    }
    aProfileNoMatterWhat.onComplete {
      case Success(value) => println(s"aProfileNoMatterWhat: $value")
      case Failure(exception) => exception.printStackTrace()
    }

    val aFetchedProfileNoMatterWhat: Future[Profile] = SocialNetwork.fetchProfile("unknown id").recoverWith {
      case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
    }
    aFetchedProfileNoMatterWhat.onComplete {
      case Success(value) => println(s"aFetchedProfileNoMatterWhat: $value")
      case Failure(exception) => exception.printStackTrace()
    }

    val fallbackResult: Future[Profile] = SocialNetwork.fetchProfile("unknown id")
      .fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))
    fallbackResult.onComplete {
      case Success(value) => println(s"fallbackResult: $value")
      case Failure(exception) => exception.printStackTrace()
    }
    Thread.sleep(5000)
  }

  class AdvancedFutures {

    def createTheFuture(): Option[Try[Int]] = {
      val aFuture = Future {
        calculateMeaningOfLife // calculates the  meaning of  life on ANOTHER thread
      }
      aFuture.value // Option[Try[Int]]
    }

    def callTheFuture(): Future[Int] = {
      val aFuture = Future {
        calculateMeaningOfLife // calculates the  meaning of  life on ANOTHER thread
      }
      println("Waiting on the future")
      aFuture.onComplete {
        case Success(meaningOfLife) => s"the meaning of life is $meaningOfLife"
        case Failure(e) => s"I have failed with $e"
      }
      aFuture
    }

    def calculateMeaningOfLife: Int = {
      Thread.sleep(2000)
      42
    }
  }

  // mini social network
  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // "database"
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )
    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API: fetching profile from the DB
    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    // API: fetching best friend from the DB
    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

}
