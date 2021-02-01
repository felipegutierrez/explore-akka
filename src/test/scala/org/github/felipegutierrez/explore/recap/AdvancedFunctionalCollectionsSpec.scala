package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedFunctionalCollectionsSpec extends AnyFlatSpec {

  import AdvancedFunctionalCollections._

  "my set implementation of Set" should
    "add values and another set" in {
    val mySet = MySet(1, 2, 3)
    val res01 = mySet.contains(1)
    assert(res01)
    val res02 = mySet.contains(4)
    assert(res02 == false)

    val anotherSet = mySet + 4
    val res03 = anotherSet.contains(4)
    assert(res03)

    val againAnotherSet = mySet ++ MySet(5, 6, 7)
    val res04 = againAnotherSet.contains(6)
    assert(res04)
  }
  "MySet implementation of Set" should
    "remove values and another MySet" in {
    val mySetOriginal = MySet[Int](1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val res01 = mySetOriginal - 2
    assert(res01.contains(1))
    assert(res01.contains(2) == false)
    assert(res01.contains(3))
    val res02 = mySetOriginal -- MySet[Int](1, 2, 3)
    assert(res02.contains(1) == false)
    assert(res02.contains(2) == false)
    assert(res02.contains(3) == false)
    assert(res02.contains(4))
  }
  "my set" should
    "not contain duplicates" in {
    val mySet = MySet[Int](1, 2, 3, 1)
    val res01 = mySet.size()
    assert(res01 == 3)
  }
  "my set that has a map function" should
    "multiply all values" in {
    val mySet = MySet[Int](1, 2, 3)
    val newSet = mySet.map(x => x * 10)

    val res01 = newSet.contains(20)
    assert(res01)

    val res02 = newSet.contains(30)
    assert(res02)

    val res03 = newSet.contains(2)
    assert(res03 == false)
  }
  "MySet implementation of Set with intersection" should
    "return only values in both sets" in {
    val mySetOne = MySet[Int](1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val mySetTwo = MySet[Int](1, 2, 3)
    val mySetThree = mySetOne & mySetTwo
    assert(mySetThree.contains(1))
    assert(mySetThree.contains(2))
    assert(mySetThree.contains(3))
    assert(!mySetThree.contains(4))
    assert(!mySetThree.contains(5))
    assert(!mySetThree.contains(6))
    assert(!mySetThree.contains(7))
    assert(!mySetThree.contains(8))
    assert(!mySetThree.contains(9))
    assert(!mySetThree.contains(10))
    assert(!mySetThree.contains(11))
  }
}
