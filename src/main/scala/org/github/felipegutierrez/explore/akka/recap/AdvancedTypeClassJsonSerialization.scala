package org.github.felipegutierrez.explore.akka.recap

import java.util.Date

object AdvancedTypeClassJsonSerialization {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, posts: List[Post])

  /**
   * A sealed trait can be extended only in the same file as its declaration.
   * They are often used to provide an alternative to enums. Since they can be only extended in a single file, the compiler knows every possible subtypes and can reason about it.
   * intermediate data type
   */
  sealed trait JSONValue {
    def stringfy: String
  }

  final case class JSONString(value: String) extends JSONValue {
    def stringfy: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    def stringfy: String = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    def stringfy: String = values.map(_.stringfy).mkString("[", ",", "]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    def stringfy: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringfy
    }.mkString("{", ",", "}")
  }

  /** defining the type classes instances using implicit */
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  /** existing data types */
  implicit object StringConverter extends JSONConverter[String] {
    def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    def convert(value: Int): JSONValue = JSONNumber(value)
  }

  /** custom data types */
  implicit object UserConverter extends JSONConverter[User] {
    override def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "createdAt" -> JSONString(post.createdAt.toString)
    ))
  }

  /** implement the conversion */
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue = converter.convert(value)
  }

  implicit object FeedConverter extends JSONConverter[Feed] {
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
      //      "user" -> UserConverter.convert(feed.user),
      //      "posts" -> JSONArray(feed.posts.map(p => PostConverter.convert(p)))
      "user" -> feed.user.toJSON,
      "posts" -> JSONArray(feed.posts.map(p => p.toJSON))
    ))
  }

  def run() = {

    val map = Map("user" -> JSONString("Felipe"), "posts" -> JSONArray(List(JSONString("This is a scala type class"), JSONNumber(465))))
    val data = JSONObject(map)
    println(data.stringfy)

    val time = new Date()
    val johnUser = User("John", 38, "john@email.com")
    val feed = Feed(johnUser, List(Post("hello", time), Post("type class for John", time)))
    println(feed.toJSON.stringfy)
  }
}
