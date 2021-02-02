package org.github.felipegutierrez.explore.recap

import com.typesafe.scalalogging.Logger
import org.junit.runner.RunWith
import org.mockito.{Mockito, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.slf4j.{Logger => Underlying}

@RunWith(classOf[JUnitRunner])
class AdvancedThreadsSpec extends AnyFlatSpec with Matchers with MockitoSugar {

  import AdvancedThreads._

  def initTestable(mocked: Underlying): AdvancedThreads = {
    new AdvancedThreads() {
      override lazy val logger = Logger(mocked)
    }
  }

  "the thread pool" should
    "log something" in {
    val mocked = Mockito.mock(classOf[Underlying])
    when(mocked.isInfoEnabled()).thenReturn(true)

    initTestable(mocked).usingThePool()
    verify(mocked).info("using the thread pool")
  }

  "the inception Threads methods" should
    "print in reverse order" in {
    val myThread = new AdvancedThreads()
    myThread.inceptionThreads(50).start()
  }
  "a thread pool" should
    "run threads in parallel" in {
    val myThread = new AdvancedThreads()
    val time = System.currentTimeMillis()
    myThread.usingThePool()
    val elapse = System.currentTimeMillis() - time
    println(elapse)
  }
}
