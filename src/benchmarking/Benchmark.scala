package benchmarking

import java.lang.management.ManagementFactory
import ManagementFactory._
import System._
import Thread._

class Benchmark {

  def sum(l: List[Int]): Int = {
    l.sum
  }
}

object Benchmark {

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
    val l1 = l ++l ++ l ++ l ++ l ++ l ++ l ++ l

    val a = new Benchmark
    (1 to 100).foreach(k => {
      println(s":::Comparison $k :::")
      profile("time.benchmark.sum")(a.sum(l))
    })
  }
}
