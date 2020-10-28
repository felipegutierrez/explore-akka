package org.github.felipegutierrez.explore.akka.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedStreamLazyEvaluationSpec extends AnyFlatSpec {

  import AdvancedStreamLazyEvaluation._

  "a stream lazy evaluated" should
    "return the heads without stack overflow" in {
    val naturals = MyStream.from(1)(_ + 1)
    assert(naturals.head == 1)
    assert(naturals.tail.head == 2)
    assert(naturals.tail.tail.head == 3)
  }
  it should "return the head after pre appending" in {
    val naturals = MyStream.from(1)(_ + 1)
    val startFrom0 = 0 #:: naturals // naturals.#::(0)
    assert(startFrom0.head == 0)
  }
  it should "return 10000 numbers without stack overflow error" in {
    val naturals = MyStream.from(1)(_ + 1)
    var count = 0
    naturals.take(10000).foreach(_ => count += 1)
    assert(count == 10000)
  }
  it should "map all elements" in {
    val naturals = MyStream.from(1)(_ + 1)
    val res = naturals.map(_ * 2).take(10).toList().toString()
    assert(res.equals("List(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)"))
  }
}
