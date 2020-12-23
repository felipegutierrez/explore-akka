package org.github.felipegutierrez.explore.akka.classic.http.highlevel

import akka.actor.ActorSystem

/**
 * Exercise:
 *
 * - GET /api/people: retrieve ALL the people you have registered
 * - GET /api/people/pin: retrieve the person with that PIN, return as JSON
 * - GET /api/people?pin=X (same)
 * - (harder) POST /api/people with a JSON payload denoting a Person, add that person to your database
 *   - extract the HTTP request's payload (entity)
 *     - extract the request
 *     - process the entity's data
 */
object PersonRestApi {
  def main(args: Array[String]): Unit = {
    run()
  }
  def run() = {
    implicit val system = ActorSystem("PersonRestApi")
    import system.dispatcher

    var people = List(
      Person(1, "Alice"),
      Person(2, "Bob"),
      Person(3, "Charlie")
    )

  }
}

case class Person(pin: Int, name: String)
