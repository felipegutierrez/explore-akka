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

  def initTestableAdvancedThreads(mocked: Underlying): AdvancedThreads = {
    new AdvancedThreads() {
      override lazy val logger = Logger(mocked)
    }
  }

  "the thread pool" should
    "log something" in {
    val mocked = Mockito.mock(classOf[Underlying])
    when(mocked.isInfoEnabled()).thenReturn(true)

    initTestableAdvancedThreads(mocked).usingThePool()
    verify(mocked).info("using the thread pool")
  }
  "the inception Threads methods" should
    "print in reverse order" in {
    val mocked = Mockito.mock(classOf[Underlying])
    when(mocked.isInfoEnabled()).thenReturn(true)

    initTestableAdvancedThreads(mocked).inceptionThreads(10).start()
    Thread.sleep(2000)
    (10 to 1).foreach { i =>
      verify(mocked).info(s"Hello from thread $i")
    }
  }
  "a thread pool" should
    "run threads in parallel" in {
    val myThread = new AdvancedThreads()
    val time = System.currentTimeMillis()
    myThread.usingThePool()
    val elapse = System.currentTimeMillis() - time
    println(elapse)
  }
  "the naive producers and consumer" should
    "log something on the producer and consumer and compute the result" in {
    val mocked = Mockito.mock(classOf[Underlying])
    when(mocked.isInfoEnabled()).thenReturn(true)

    initTestableAdvancedThreads(mocked).naiveProdCons(new SimpleContainer(), 42)

    Thread.sleep(2000)
    verify(mocked).info("[consumer] waiting...")
    // verify(mocked).info("[consumer] actively waiting...")
    verify(mocked).info("[producer] computing...")
    verify(mocked).info("[producer] I have produced, after long work, the value 42")
  }
  "the smart producers and consumer" should
    "log something on the producer and consumer and compute the result with notify" in {
    val mocked = Mockito.mock(classOf[Underlying])
    when(mocked.isInfoEnabled()).thenReturn(true)

    initTestableAdvancedThreads(mocked).smartProdCons(new SimpleContainer(), 42)

    Thread.sleep(2000)
    verify(mocked).info("[consumer] waiting...")
    verify(mocked).info("[consumer] I have consumed 42")
    verify(mocked).info("[producer] Hard at work...")
    verify(mocked).info("[producer] I'm producing 42")
  }
}
