package sample.crawl.ebay.parser

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

import akka.actor.{Actor, ActorLogging, Props}
import org.jsoup.nodes.Document
import sample.crawl.ebay.db.SaveEbayDetail
import sample.crawl.ebay.download.GetUrl.promiseGetUrl
import sample.crawl.ebay.download.ParseContent
import sample.crawl.ebay.store.DataStoreActor

import scala.concurrent.Future
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}


// detail 详情页面解析器
class DetailParserActor extends Actor with ActorLogging {

  val dataStoreActorRef = context.actorOf(Props.create(classOf[DataStoreActor]))

  import context.dispatcher

  def receive = {
    case url: String =>
      log.info(s"详情页解析器接受的url是: $url")
      Future(promiseGetUrl()(url)).onComplete {
        case Success(ParseContent(d, url)) =>
          log.info(s"详情页解析器正在解析的url是 $url")
          parseDocTo(d, url)
        case Failure(e) =>
          log.info(s"下载详情页失败 $e")
      }

      def parseDocTo(doc: Document, href: String): Unit = {
        val sdf = new SimpleDateFormat("  MMM d, yyyy HH:mm:ss", Locale.ENGLISH)
        val sold_record_sdf = new SimpleDateFormat("MMM-d-y HH:mm:ss", Locale.ENGLISH)

        var price_unit: Option[String] = None
        var price: Option[Int] = None
        if (!doc.select("span#convbinPrice").isEmpty) {
          val pattern = new Regex("(\\d+(,?\\d+\\.?\\d+)+)")
          price = Some(pattern.findFirstIn(doc.select("span#convbinPrice").text()).get.replace(",","").toDouble.toInt)
        }
        val title = doc.select("h1#itemTitle").text().replace("Details about", "")
        var shopname: Option[String] = None
        if (!doc.select("span.mbg-nw").isEmpty) {
          shopname = Some(doc.select("span.mbg-nw").text())
        }
        val sold_tag = doc.select("a:matches([\\d]+[\\s]+sold)")
        var sold: Option[Int] = None
        var sold_history: String = ""
        var sold_first_time: Option[String] = None
        if (!sold_tag.isEmpty) {
          // 阻塞运行找到首次出售记录
          sold = Some(sold_tag.text().replace(" sold", "").toInt)
          sold_history = sold_tag.attr("href")
          val sold_record: ParseContent = promiseGetUrl()(sold_history)
          try {
            val last_sold_record_text = sold_record.d.select("td.contentValueFont").last().text()
            val pattern = "(.*[\\d]{2}:[\\d]{2}:[\\d]{2}).*".r
            val pattern(last_sold_record) = last_sold_record_text
            sold_first_time = Some(new Timestamp(sold_record_sdf.parse(last_sold_record).getTime).toString)
          } catch {
            case ex: Exception =>
              println(s"last time error: $ex")
          }
        }

        var last_time: Option[String] = None // 如果时间为空的默认时间
        var last_time_tag = doc.select("div.vi-desc-revHistory").text()
        if (!last_time_tag.isEmpty) {
          val pattern = "Last updated on([^\\n]+).*View all revisions".r
          val pattern(data) = last_time_tag
          last_time = Some(new Timestamp(sdf.parse(data).getTime).toString)
        }
        log.info(s"title: $title, price: $price, shopname: $shopname")
        dataStoreActorRef ! SaveEbayDetail(href, title, sold, sold_history, last_time, shopname, sold_first_time, price, price_unit)
      }
  }
}

