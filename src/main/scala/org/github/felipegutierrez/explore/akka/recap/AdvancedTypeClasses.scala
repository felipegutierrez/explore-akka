package org.github.felipegutierrez.explore.akka.recap

import java.util.Date

object AdvancedTypeClasses {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {

    println(User("John", 32, "john@rockthejvm.com").toHtml)

    val john = User("John", 32, "john@rockthejvm.com")
    println(UserSerializer.serialize(john))

    println(PartialUserSerializer.serialize(john))

    val paul = User("Paul", 32, "john@rockthejvm.com")
    println(EqualName.equal(john, paul))
    println(EqualEmail.equal(john, paul))

    implicit object IntSerializer extends HTMLSerializer[Int] {
      override def serialize(value: Int): String = s"<div style: color=blue>$value</div>"
    }

    println(HTMLSerializer.serialize(42))
    println(HTMLSerializer.serialize(john))

    // access to the entire type class  interface
    println(HTMLSerializer[User].serialize(john))

    val anotherJohn = User("John", 32, "another_john@rockthejvm.com")

    println(Equal.apply(john, anotherJohn))

    println(new HTMLEnrichment[User](john).toHTML(UserSerializer))
    println(john.toHTML(UserSerializer))
    println(john.toHTML)

    /*
    john.===(anotherJohn)
    new TypeSafeEqual[User](john).===(anotherJohn)
    new TypeSafeEqual[User](john).===(anotherJohn)(NameEquality)
   */
    println(s"john === anotherJohn: ${john === anotherJohn}")
    println(s"john !== anotherJohn: ${john !== anotherJohn}")
    println(john == 43)
    // println(john === 43) // TYPE SAFE

    // implicitly
    case class Permissions(mask: String)
    implicit val defaultPermissions: Permissions = Permissions("0744")

    // in some other part of the  code
    val standardPerms = implicitly[Permissions]
  }

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  // option 2 - pattern matching
  /*
    1 - lost type safety
    2 - need to  modify the code every time
    3 - still ONE implementation
   */
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e) =>
      case _ =>
    }
  }

  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  // 1 - we can define serializers for other  types
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString()}</div>"
  }

  // 2 - we can define MULTIPLE serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    def serialize(user: User): String = s"<div>${user.name} </div>"
  }

  // exercise - equality
  trait Equal[T] {
    def equal(value1: T, value2: T): Boolean
  }

  implicit object EqualName extends Equal[User] {
    override def equal(value1: User, value2: User): Boolean = value1.name == value2.name
  }

  object EqualEmail extends Equal[User] {
    override def equal(value1: User, value2: User): Boolean = value1.email == value2.email
  }

  implicit class TypeSafeEqual[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = equalizer.equal(value, anotherValue)

    def !==(anotherValue: T)(implicit equalizer: Equal[T]): Boolean = !equalizer.equal(value, anotherValue)
  }

  // defining implicit serializers
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  object Equal {
    def apply[T](value1: T, value2: T)(implicit equalizer: Equal[T]): Boolean =
      equalizer.equal(value1, value2)
  }

  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }


}
