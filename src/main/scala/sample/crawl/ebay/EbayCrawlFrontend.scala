package sample.crawl.ebay

import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import akka.cluster.Cluster
import akka.routing.FromConfig
import akka.actor.ReceiveTimeout
import sample.crawl.ebay.message.NextPage

import akka.actor.ActorSystem

import scala.util.Try
import scala.concurrent.Await

class EbayCrawlFrontend(root_url: String, repeat: Boolean) extends Actor with ActorLogging {

  val backend = context.actorOf(FromConfig.props(),
    name = "EbayCrawlBackendRouter")

  override def preStart(): Unit = {
    // 启动server
    sendJobs()
    // 设置超时秒数
    if (repeat) {
      context.setReceiveTimeout(15.seconds)
    }
  }


  def receive = {
    case (url: String, href: Array[String]) =>
      println(s"$url 前端解析的一个href是: $href")
      if (repeat) sendJobs()
      else context.stop(self)

    case NextPage(href: String) =>
      //接受下一页的链接,并发送到后台
      log.info(s"下一页的超链接是: $href, 发送到后台")
      backend ! href

    case ReceiveTimeout =>
      log.info("Timeout")
      // 如果超时则重新发送
      sendJobs()
  }

  def sendJobs(): Unit = {
    val base_url = "https://www.ebay.com/sch/i.html?LH_BIN=1&_nkw=gun&_sop=10&&_pgn=%d"
    log.info("开始发送url到爬虫后端 [{}]", root_url)
    (1 to 200).par.foreach { backend ! base_url.format(_)}
  }
}

//爬虫的调度等处理
object EbayCrawlFrontend {
  def main(args: Array[String]): Unit = {
    // 设置根节点url
    val root_url = "https://www.ebay.com/sch/i.html?_sacat=0&LH_BIN=1&_nkw=gun&_sop=10"

    val config = ConfigFactory.parseString("akka.cluster.roles = [frontend]").
      withFallback(ConfigFactory.load("ebay"))

    val system = ActorSystem("ClusterSystem", config)
    system.log.info("EbayCrawl 启动3个集群节点.")

    Cluster(system) registerOnMemberUp {
      system.actorOf(Props(classOf[EbayCrawlFrontend], root_url, false),
        name = "EbayCrawlFrontend")
    }

    Cluster(system).registerOnMemberRemoved {
      // exit JVM when ActorSystem has been terminated
      system.registerOnTermination(System.exit(0))
      // shut down ActorSystem
      system.terminate()

      // In case ActorSystem shutdown takes longer than 10 seconds,
      // exit the JVM forcefully anyway.
      // We must spawn a separate thread to not block current thread,
      // since that would have blocked the shutdown of the ActorSystem.
      new Thread {
        override def run(): Unit = {
          if (Try(Await.ready(system.whenTerminated, 30.seconds)).isFailure)
            System.exit(-1)
        }
      }.start()
    }
  }
}
