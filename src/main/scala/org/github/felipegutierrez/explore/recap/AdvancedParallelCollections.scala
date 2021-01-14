package org.github.felipegutierrez.explore.recap

import scala.collection.parallel.immutable.ParVector

object AdvancedParallelCollections {

  //  def main(args: Array[String]): Unit = {
  //    run()
  //  }

  def run() = {
    // 1 - parallel collections

    val parList = List(1, 2, 3).par

    val aParVector = ParVector[Int](1, 2, 3)

    /*
      Seq
      Vector
      Array
      Map - Hash, Trie
      Set - Hash, Trie
     */

    def measure[T](operation: => T): Long = {
      val time = System.currentTimeMillis()
      operation
      System.currentTimeMillis() - time
    }

    val list = (1 to 10000).toList
    val serialTime = measure {
      list.map(_ + 1)
    }
    println("serial time: " + serialTime)

    val parallelTime = measure {
      list.par.map(_ + 1)
    }

    println("parallel time: " + parallelTime)

    /*
      Map-reduce model
      - split the elements into chunks - Splitter
      - operation
      - recombine - Combiner
     */

    // map, flatMap, filter, foreach, reduce, fold

    // fold, reduce with non-associative operators
    println(List(1, 2, 3).reduce(_ - _))
    println(List(1, 2, 3).par.reduce(_ - _))

    // synchronization
    val myList = List.range(1, 1000)
    var sumSync = 0
    var sumAsync = 0
    myList.foreach(sumSync += _)
    println(s"synchronous sum : $sumSync")
    myList.par.foreach(sumAsync += _) // race conditions!
    println(s"asynchronous sum : $sumAsync")
  }

  class AdvancedParallelCollections {
    def getTotalSumWithReduce(list: List[Int]): Int = {
      var sumSync: Int = 0
      list.foreach(sumSync += _)
      sumSync
    }

    // this method does not guarantee right results due to concurrency
    def getAsynchTotalSumWithReduce(list: List[Int]): Int = {
      var sumSync: Int = 0
      list.par.foreach(sumSync += _)
      sumSync
    }
  }

}
