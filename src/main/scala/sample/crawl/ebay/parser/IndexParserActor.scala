package sample.crawl.ebay.parser

import java.text.SimpleDateFormat
import java.util.Locale

import akka.actor.{Actor, ActorLogging, Props}
import org.jsoup.nodes.Document
import sample.crawl.ebay.db.SaveEbay
import sample.crawl.ebay.store.DataStoreActor

import scala.concurrent.Future
import scala.util.Success
import scala.collection.JavaConversions._
import java.sql.Timestamp

import akka.routing.BalancingPool
import sample.crawl.ebay.download.GetUrl.promiseGetUrl
import sample.crawl.ebay.download.ParseContent
import sample.crawl.ebay.message.IndexCrawlKeyword
import sample.crawl.ebay.parser.ShopParserActor.get_ebay_years

import scala.util.matching.Regex

// detail 详情页面解析器
class IndexParserActor extends Actor with ActorLogging {

  val dataStoreActorRef = context.actorOf(Props.create(classOf[DataStoreActor]))
  val detailParserActorRef = context.actorOf(BalancingPool(8).props(Props(classOf[DetailParserActor])), "balancing-pool-router")

  import context.dispatcher

  def receive = {
    case IndexCrawlKeyword(url, kd) =>
      log.info(s"解析的url是 $url, 关键词: $kd")
      Future(promiseGetUrl()(url)).onComplete {
        case Success(ParseContent(d, url)) =>
          parseDocToIndex(d, url, kd)
      }
  }

  // 解析成功运行该函数
  def parseDocToIndex(doc: Document, url: String, kd: String): Unit = {
        //      解析搜索页面的数据
        val sdf = new SimpleDateFormat("MMM-d h:m", Locale.ENGLISH)
        for (e <- doc.select("li.sresult.lvresult")) {
          val title = e.select("a.vip").text()
          val detail_href = e.select("a.vip").attr("href")
          val subtitle = e.select("div.lvsubtitle").text()
          val rank = e.attr("r").toInt
          val price = e.select("li.lvprice.prc span").text()
          val itemid = e.attr("listingid")
          var ratting = 0
          var ratting_href = ""
          if (!e.select("a.star-ratings__review-num").isEmpty) {
            val pattern = new Regex("\\d+")
            ratting = pattern.findFirstIn(e.select("a.star-ratings__review-num").text()).get.toInt
            ratting_href = e.select("a.star-ratings__review-num").attr("href")
          }
          val from = e.select("ul.lvdetails.left.space-zero.full-width li:eq(1)").text().trim().replace("From", "")
          val get_year = get_ebay_years(e.select("span.tme span").text())
          val create_time = new Timestamp(sdf.parse(e.select("span.tme span").text()).getTime).toString.replace("1970", get_year)
          val format = e.select("li.lvformat span").text()
          val ship = e.select("span.ship").text()
          val src = e.select("img.img").attr("src")
          val create_date = create_time.split(" ")(0)

          dataStoreActorRef ! SaveEbay(detail_href, title, subtitle, rank, price, itemid, ratting, ratting_href, from, create_time
          , format, ship, src, kd, create_date)
          detailParserActorRef ! detail_href
        }
      }
}

