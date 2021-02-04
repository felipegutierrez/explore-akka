package org.github.felipegutierrez.explore.recap


import org.scalatest.flatspec.AnyFlatSpec

class BasicStringSpec extends AnyFlatSpec {
  "a method that reverse the order of names" should
    "return first names in the end of the string" in {

    val nameStringList = List("John Kennedy", "Mary B H Kennedy")
    val expectResult = List("Kennedy John ", "Kennedy Mary B H ")
    nameStringList.foreach(s => println(s"original: $s"))

    val result = BasicString.reverseNames(nameStringList)

    result.foreach(s => println(s"result: $s"))
    assertResult(expectResult)(result)
  }
}
