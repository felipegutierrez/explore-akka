package org.github.felipegutierrez.explore.recap

object AdvancedPatternMatching {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    println(aListWith(List(1)))

    val bob = new PersonIsNotCaseClass("Bob", 23)
    println(patternMatchingWithNotCaseClass(bob))
    println(legalStatus(bob))

    println(checkNumber(new MathProperty(1)))
    println(checkNumber(new MathProperty(12)))
    println(checkNumber(new MathProperty(13)))

    println(checkMyList(Cons(1, Cons(2, Cons(3, Empty)))))
    println(checkMyList(Cons(11, Cons(22, Cons(33, Empty)))))

    println(getName(new Person("Felipe")))
    println(getName(new Person("")))
  }

  def aListWith(numbers: List[Int]) = {
    val description = numbers match {
      case head :: Nil => s"the only element is ${head}"
      case more => s"anything else ${more.toString()}"
    }
    description
  }

  def patternMatchingWithNotCaseClass(person: PersonIsNotCaseClass) = {
    val greeting = person match {
      case PersonPatternIsNotCaseClass(n, a) => s"hi, my name is $n and I am $a years old."
    }
    greeting
  }

  def legalStatus(person: PersonIsNotCaseClass) = {
    val status = person.age match {
      case PersonPatternIsNotCaseClass(status) => s"my legal status is $status"
    }
    status
  }

  class PersonIsNotCaseClass(val name: String, val age: Int)

  object PersonPatternIsNotCaseClass {
    def unapply(person: PersonIsNotCaseClass): Option[(String, Int)] = {
      if (person.age < 16) None
      else Some(person.name, person.age)
    }

    def unapply(age: Int): Option[String] = Some(if (age < 16) "minor" else "major")
  }

  def checkNumber(n: MathProperty) = {
    val result = n match {
      case singleDigit() => s"this is single digit"
      case evenDigit() => s"this is even digit"
      case oddDigit() => s"this is odd digit"
      case _ => s"no property"
    }
    result
  }

  class MathProperty(val number: Int)

  object singleDigit {
    def unapply(n: MathProperty): Boolean = (-10 < n.number && n.number < 10)
  }

  object evenDigit {
    def unapply(n: MathProperty): Boolean = (n.number % 2 == 0)
  }

  object oddDigit {
    def unapply(n: MathProperty): Boolean = (n.number % 2 == 1)
  }

  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  def checkMyList(myList: MyList[Int]): String = {
    myList match {
      case MyList(1, 2, _*) => "starting with 1, 2"
      case _ => "anything else"
    }
  }

  case class Person(name: String)

  abstract class Wrapper[T] {
    def isEmpty: Boolean

    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false

      def get: String = person.name
    }
  }

  def getName(p: Person) = {
    p match {
      case PersonWrapper(n) => s"This person's name is $n"
      case _ => "anonymous"
    }
  }
}
