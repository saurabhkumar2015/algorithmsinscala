package benchmarking

import java.lang.management.{GarbageCollectorMXBean, ManagementFactory}
import ManagementFactory._
import scala.collection.JavaConverters._
import Thread._


/***
  * I am going to benchmark a simple example showcasing the difference between the memory footprint of a correct
  * usage of stringbuilder and of an incorrect usage.
  *
  * Here I have some random text in a list. I will fold this list to a single String using StringBuilder
  */

class MeasureHeapHotspot {

  def foldListToString(l: List[String]): String = {
    val a = new StringBuilder(2000)
    l.foreach(s => a.append(s + ","))
    val  b= a.toString()
    b.substring(0, b.length-1)
  }

  def foldListToStringV2(l: List[String]): String = {
    l.mkString(",")
  }
}

object MeasureHeapHotspot {

  def profileMemory[T](metricName: String)(fn: => T): T = {
    System.gc()
    sleep(1000)
    val g = getGarbageCollectorMXBeans
    val mu1 = getMemoryMXBean.getHeapMemoryUsage.getUsed
    val munh1 = getMemoryMXBean.getNonHeapMemoryUsage.getUsed
    val m1 = getGcTime(g)

    val r = fn

    val mu2 = getMemoryMXBean.getHeapMemoryUsage.getUsed
    val munh2 = getMemoryMXBean.getNonHeapMemoryUsage.getUsed
    println(s"Heap Memory Usage for $metricName by ${(mu2-mu1)/1000} kilobytes")
    println(s"Non Heap Memory Usage for $metricName by ${(munh2-munh1)/1000} kilobytes")
    System.gc()
    sleep(1000)
    val m2 = getGcTime(g)

    println(s"Total gc time has increased for $metricName by ${m2-m1} millis")
    r
  }

  def getGcTime(g: java.util.List[GarbageCollectorMXBean]): Long = {
    var m = 0L
    g.asScala.foreach(gc => {
      m = m + gc.getCollectionTime
    })
    m
  }



  def main(args: Array[String]) = {
    val l = List("Ratish","Mahesh","Geeta","Babita","Liverpool","Boston","FC Barcelona","Amazing","LadyFinger"
      ,"Awesome","The Shawshank Redemption is the best movie","Kindgom of Heaven has Awesome Lyrics","It is amazing!!!!!",
      "I am bored."," I want to code some algorithms","Hey have patience","Ladakh is a great place","lets chill in goa",
      "How are you ?","yipeeeeeeeee!!!!!!!!!!!", "This string is already very long~~~~!!!!","This is quite random")

    val l1 = l ++ l ++ l ++l ++ l ++ l ++ l ++ l ++ l ++ l ++ l ++ l ++ l ++ l ++ l ++ l ++ l ++ l ++ l ++l ++l
    val l2 = l1 ++ l1 ++ l1 ++l1 ++ l1
    val m = new MeasureHeapHotspot

    (1 to 10).foreach(
      k => {
        println(profileMemory("Optimized fold List to String")(m.foldListToStringV2(l2)))
        println(profileMemory("Unoptimized fold List to String")(m.foldListToString(l2)))
      }
    )
  }
}
