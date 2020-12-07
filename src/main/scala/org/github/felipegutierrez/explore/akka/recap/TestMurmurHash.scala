package org.github.felipegutierrez.explore.akka.recap

import scala.util.hashing.MurmurHash3

object TestMurmurHash extends App {

  override def main(args: Array[String]): Unit = {
    val email = MyObject("my_email@google.com")
    println(s"This is my email hash: $email and this is my hash: ${email.hashCode()}")

  }

  case class MyObject(val email: String) {
    override def hashCode(): Int = {
      MurmurHash3.stringHash(email)
    }
  }
}
