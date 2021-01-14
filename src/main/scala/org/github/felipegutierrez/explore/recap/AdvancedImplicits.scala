package org.github.felipegutierrez.explore.recap

object AdvancedImplicits {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {

    val pair = "Daniel" -> "555"
    val intPair = 1 -> 2

    println("Peter".greet) // println(fromStringToPerson("Peter").greet)

    implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
    //  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)

    println(List(1, 4, 5, 3, 2).sorted)


    val persons = List(
      Person("Steve", 30),
      Person("Amy", 22),
      Person("John", 66)
    )

    implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)

    println(persons.sorted)
  }

  case class Person(name: String, age: Int)

  case class Person1(name: String) {
    def greet = s"Hi, my name is $name!"
  }

  implicit def fromStringToPerson(str: String): Person1 = Person1(str)


  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.nUnits * a.unitPrice < b.nUnits * b.unitPrice)
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }

}
