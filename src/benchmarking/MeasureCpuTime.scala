package benchmarking

import java.lang.management.ManagementFactory
import ManagementFactory._
import System._
import Thread._


/*** Example showing getCurrentThreadCpuTime measures correct cpu time. Sleep time is skipped in ThreadCpuTime
* :::Comparison 2 :::
* profile for time.benchmark.sum is 81 microseconds
* time for time.benchmark.sum is 10492 microseconds
* :::Comparison 3 :::
* profile for time.benchmark.sum is 80 microseconds
* time for time.benchmark.sum is 12432 microseconds
**/

class MeasureCpuTime {

  def sum(l: List[Int]): Int = {
    l.sum
  }
}

object MeasureCpuTime {

  def profile[T](metricName: String)(fn: => T): T = {
    val i = getThreadMXBean.getCurrentThreadCpuTime
    val a = nanoTime
    val r = fn
    sleep(10)
    val b = nanoTime - a
    val j = getThreadMXBean.getCurrentThreadCpuTime - i
    println(s"profile for $metricName is ${j/1000} microseconds" )
    println(s"time for $metricName is ${b/1000} microseconds" )
    r
  }


  def main(args: Array[String]) = {

    val l = List(12, 12, 34, 66, 777, 12, 12, -1215, -12, 56, -5656, 8888, -444, -3,-5)
    val a = new MeasureCpuTime
    (1 to 100).foreach(k => {
      println(s":::Comparison $k :::")
      profile("time.benchmark.sum")(a.sum(l))
    })
  }
}
