package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

class ObjectsAndCompanionsSpec extends AnyFlatSpec {
  "a companion object" should
    "be a singleton" in {

    val person01 = ObjectsAndCompanions.Person
    val person02 = ObjectsAndCompanions.Person

    assert(person01.hashCode() == person02.hashCode())
  }
  "a class with new instance" should
    "not be a singleton" in {

    val name = "Felipe"
    val age = 38
    val person01 = new ObjectsAndCompanions.Person(name, age)
    val person02 = new ObjectsAndCompanions.Person(name, age)

    assert(person01.hashCode() != person02.hashCode())
  }
}
