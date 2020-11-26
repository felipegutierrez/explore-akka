package org.github.felipegutierrez.explore.akka.classic.clustering.voting

import java.util.UUID

import scala.util.Random

case class Person(id: String, age: Int)

object Person {
  def generate() = Person(UUID.randomUUID().toString, 16 + Random.nextInt(90))
}
