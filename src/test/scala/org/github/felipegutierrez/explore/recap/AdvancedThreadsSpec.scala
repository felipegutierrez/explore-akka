package org.github.felipegutierrez.explore.recap

import org.scalatest.flatspec.AnyFlatSpec

class AdvancedThreadsSpec extends AnyFlatSpec {

  import AdvancedThreads._

  "a thread pool" should
    "run threads in parallel" in {
    val myThread = new AdvancedThreads()
    val time = System.currentTimeMillis()
    myThread.usingThePool()
    val elapse = System.currentTimeMillis() - time
    println(elapse)
  }
}
