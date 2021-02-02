package org.github.felipegutierrez.explore.recap

import com.typesafe.scalalogging.Logger
import org.junit.runner.RunWith
import org.mockito.{Mockito, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.slf4j.{Logger => Underlying}

@RunWith(classOf[JUnitRunner])
class MultiThreadRecapSpec extends AnyFlatSpec with Matchers with MockitoSugar {

  def initTestableMultiThreadRecap(mocked: Underlying): MultiThreadRecap = {
    new MultiThreadRecap() {
      override lazy val logger = Logger(mocked)
    }
  }

  "two threads running in parallel" should
    "run in different order" in {
    val mocked = Mockito.mock(classOf[Underlying])
    when(mocked.isInfoEnabled()).thenReturn(true)

    val mainId = initTestableMultiThreadRecap(mocked).runTwoThreads(5)
    Thread.sleep(5000)
    verify(mocked).info("creating threads on JVM ...........")
    verify(mocked).info("main thread id: " + mainId)
    (1 to 3).foreach { i =>
      verify(mocked).info("I am running in parallel " + i)
      verify(mocked).info("I am running in parallel with syntax sugar " + i)
    }
  }
  "unsafe bank account" should
    "not synchronize withdraws with deposits" in {
    val amount: Int = 500000
    val result = MultiThreadRecap.unsafeWithdrawAndDeposit(amount)
    assert(amount != result)
  }
  "safe bank account" should
    "synchronize withdraws with deposits" in {
    val amount: Int = 500000
    val result = MultiThreadRecap.safeWithdrawAndDeposit(amount)
    assert(amount >= result)
  }
}
