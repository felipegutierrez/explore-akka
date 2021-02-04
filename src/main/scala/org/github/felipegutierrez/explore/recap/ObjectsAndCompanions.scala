package org.github.felipegutierrez.explore.recap

object ObjectsAndCompanions {

  class Person(name: String, age: Int) {
    def greet(): String = s"Hello, my name is $name and I am $age years old. Are you a human being? ${Person.HUMAN_BEING}"
  }

  object Person { // the companions object: Person.type
    private val HUMAN_BEING: Boolean = true
  }

}
