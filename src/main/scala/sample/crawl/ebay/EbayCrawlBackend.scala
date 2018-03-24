package sample.crawl.ebay

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.BalancingPool
import com.typesafe.config.ConfigFactory
import sample.crawl.ebay.parser.{IndexParserActor, ShopParserActor}
import sample.crawl.ebay.message._

// 爬虫后端
class EbayCrawlBackend extends Actor with ActorLogging {

  val IndexParserActor = context.actorOf(BalancingPool(8).props(Props(classOf[IndexParserActor])), "index-pool-router")
  val ShopParserActor = context.actorOf(BalancingPool(8).props(Props(classOf[ShopParserActor])), "shop-pool-route")

  def receive = {
    // 接受一个关键词，然后调用首页解析器爬取数据
    case IndexCrawlKeyword(url, kd) =>
      IndexParserActor ! IndexCrawlKeyword(url, kd)

    case IndexCrawlShopname(url, name) =>
      ShopParserActor ! IndexCrawlShopname(url, name)
  }
}


object EbayCrawlBackend {
  def main(args: Array[String]): Unit = {
    val port = if (args.isEmpty) "0" else args(0)
    println(s"开启的一个端口是 $port")
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(ConfigFactory.load("ebay"))

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[EbayCrawlBackend], name = "EbayCrawlBackend")
    system.actorOf(Props[EbayCrawlListener], name = "EbayCrawlListener")
  }
}
