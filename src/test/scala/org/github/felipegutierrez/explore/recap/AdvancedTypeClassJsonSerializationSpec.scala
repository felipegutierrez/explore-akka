package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

import java.util.Date

class AdvancedTypeClassJsonSerializationSpec extends AnyFlatSpec {

  import AdvancedTypeClassJsonSerialization._

  "a type class in Scala that converts to JSON" should
    "output a right JSON file" in {
    val map = Map("user" -> JSONString("Felipe"), "posts" -> JSONArray(List(JSONString("This is a scala type class"), JSONNumber(465))))
    val data = JSONObject(map)

    assert(data.stringfy == "{\"user\":\"Felipe\",\"posts\":[\"This is a scala type class\",465]}")
  }
  "a type class in Scala that converts to JSON" should
    "output a user with feed" in {
    val time = new Date()
    val johnUser = User("John", 38, "john@email.com")
    val feed = Feed(johnUser, List(Post("hello", time), Post("type class for John", time)))

    val expectedValue = "{\"user\":{\"name\":\"John\",\"age\":38,\"email\":\"john@email.com\"},\"posts\":[{\"content\":\"hello\",\"createdAt\":\"" + time.toString + "\"},{\"content\":\"type class for John\",\"createdAt\":\"" + time.toString + "\"}]}"
    assert(feed.toJSON.stringfy == expectedValue)
  }
}
