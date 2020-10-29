package org.github.felipegutierrez.explore.akka.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedParallelCollectionsSpec extends AnyFlatSpec {

  import AdvancedParallelCollections._

  val cores = Runtime.getRuntime.availableProcessors()

  if (cores >= 4) {
    "a parallel list of integers" should
      "not return the same result of all items when it is using reduce function" in {
      val advancedParallelCollections = new AdvancedParallelCollections()
      val myList = List.range(1, 10000000)
      val resSynch = advancedParallelCollections.getTotalSumWithReduce(myList)
      val resAsynch = advancedParallelCollections.getAsynchTotalSumWithReduce(myList)
      assert(resSynch != resAsynch)
    }
  }
}
