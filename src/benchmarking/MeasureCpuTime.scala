package benchmarking

import java.lang.management.ManagementFactory
import ManagementFactory._
import System._
import Thread._
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}


/***
  * I am going to benchmark a simple example showing getCurrentThreadCpuTime measures correct cpu time.
  * Sleep time is skipped in ThreadCpuTime
  * And the profile time discards the sleep and wait time of a thread giving you a more accurate picture of
  * cpu utilization
  *
  * To write a highly optimized code, It can be a best practice to optimize the cpu time in unit testing itself.
  *
  * profile for time.benchmark.sum is 81 microseconds // more accurate
  * time for time.benchmark.sum is 10492 microseconds // has sleep and wait time
  * profile for time.benchmark.sum is 80 microseconds
  * time for time.benchmark.sum is 12432 microseconds
  */

class MeasureCpuTime {
  def sum(l: List[Int]): Int = {
    l.sum
  }
}

object MeasureCpuTime {

  def profile[T](metricName: String)(fn: => T): T = {
    val i = getCpuTimeInMicros
    val a = nanoTime
    val r = fn
    val b = nanoTime - a
    val j = getCpuTimeInMicros - i
    println(s"profile for $metricName is $j microseconds" )
    r
  }

  def profileWithSleep[T](metricName: String)(fn: => T): T = {
    val i = getCpuTimeInMicros
    val a = nanoTime
    val r = fn
    sleep(140)
    val b = nanoTime - a
    val j = getCpuTimeInMicros - i
    println(s"profile for $metricName is $j microseconds" )
    println(s"time for $metricName is ${b/1000} microseconds" )
    r
  }

  def profileWithWait[T](metricName: String)(fn: => T)(implicit executionContext: ExecutionContext): T = {
    val future = Future {Thread.sleep(140)}
    val i = getCpuTimeInMicros
    val a = nanoTime
    Await.result(future, Duration(200,TimeUnit.MILLISECONDS))
    val r = fn
    val b = nanoTime - a
    val j = getCpuTimeInMicros - i
    println(s"profile for $metricName is $j microseconds" )
    println(s"time for $metricName is ${b/1000} microseconds" )
    r
  }

  def getCpuTimeInMicros = getThreadMXBean.getCurrentThreadCpuTime/1000
  def getCpuTimeInMillis = getThreadMXBean.getCurrentThreadCpuTime/1000000





  def main(args: Array[String]) = {
    val l = List(12, 12, 34, 66, 777, 12, 12, -1215, -12, 56, -5656, 8888, -444, -3,-5)
    testSleep(l)
    testWait(l)
  }

  def testWait(l: List[Int]): Unit = {
    val a = new MeasureCpuTime
    (1 to 10).foreach(k => {
      val i = getCpuTimeInMicros
      profileWithWait(s"time.$k.sum")(a.sum(l))
      check(i, 140000)
    })
  }

  def testSleep(l: List[Int]): Unit = {
    val a = new MeasureCpuTime
    (1 to 10).foreach(k => {
      val i = getCpuTimeInMicros
      profileWithSleep(s"time.$k.sum")(a.sum(l))
      check(i, 140000)
    })
  }

  def check(i: Long, l:Long): Unit = {
    if (getCpuTimeInMicros - i > l) throw new Exception("Test case Failure :: more CPU time utilization than expected.")
  }
}
