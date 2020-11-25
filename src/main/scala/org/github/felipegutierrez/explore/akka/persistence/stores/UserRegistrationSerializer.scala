package org.github.felipegutierrez.explore.akka.persistence.stores

import akka.serialization.Serializer

// COMMANDS
case class RegisterUser(email: String, name: String)

// EVENTS
case class UserRegistered(id: Int, email: String, name: String)

// SERIALIZER
class UserRegistrationSerializer extends Serializer {
  val SEPARATOR = "//"

  override def identifier: Int = 1234567890

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case event@UserRegistered(id, email, name) =>
      println(s"serializing $event")
      s"[$id$SEPARATOR$email$SEPARATOR$name]".getBytes()
    case _ => throw new IllegalArgumentException("Only UserRegister can be serialized")
  }

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val string = new String(bytes)
    val values = string.substring(1, string.length - 1).split(SEPARATOR)
    val id = values(0).toInt
    val email = values(1)
    val name = values(2)
    val result = UserRegistered(id, email, name)
    println(s"deserialized $string to $result")
    result
  }

  override def includeManifest: Boolean = false
}
