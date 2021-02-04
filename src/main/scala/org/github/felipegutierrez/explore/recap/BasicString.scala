package org.github.felipegutierrez.explore.recap

object BasicString {
//  def main(args: Array[String]): Unit = {
//    run()
//  }

  def run() = {
    val nameStringList = List("John Kennedy", "Mary B H Kennedy")
    nameStringList.foreach(s => println(s"original: $s"))

    val result = reverseNames(nameStringList)

    result.foreach(s => println(s"result: $s"))
  }

  def reverseNames(nameStringList: List[String]): List[String] = {
    nameStringList.map { name =>
      val nameArray = name.split(" ")
      val firstsName = for {i <- 0 until nameArray.length - 1} yield (nameArray(i) + " ")
      (nameArray(nameArray.length - 1) + " " + firstsName.mkString)
    }
  }
}
