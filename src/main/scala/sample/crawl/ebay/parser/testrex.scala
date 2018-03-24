import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConversions._
import java.text.SimpleDateFormat
import java.util.Locale
import java.sql.Timestamp

import akka.actor.Status.Success

import scala.collection.immutable.Stream.Empty
import scala.util.Try
import scala.util.matching.Regex


object testrex {
  def main(args: Array[String]): Unit = {
    val url = "https://www.ebay.com/itm/Iphone-4/192416997477?hash=item2cccf26065:g:n-QAAOSwnCFaTSfR"
    val doc = Jsoup.connect(url).get()
    parseDocDetail(doc, url)
    //    val str = "1 product rating"
    //    val pattern = "([\\d]+) product rating".r
    //    val pattern(ratting) = str
    //    println(ratting)
  }


  def parseSoldHistory(doc: Document, url: String): Unit = {
    val sold_record_sdf = new SimpleDateFormat("MMM-d-y HH:mm:ss", Locale.ENGLISH)
    val last_sold_record_text = doc.select("td.contentValueFont").last().text()
    val pattern = "(.*[\\d]{2}:[\\d]{2}:[\\d]{2}).*".r
    val pattern(last_sold_record) = last_sold_record_text
    println(sold_record_sdf.parse(last_sold_record))
    val sold_first_time = new Timestamp(sold_record_sdf.parse(last_sold_record).getTime).toString
    println(s"sold_first time $sold_first_time")
  }

  def parseDocToIndex(doc: Document, url: String): Unit = {
    //      解析搜索页面的数据
    val sdf = new SimpleDateFormat("MMM-d h:m", Locale.ENGLISH)

    for (e <- doc.select("li.sresult")) {
      val title = e.select("a.vip").text()
      val detail_href = e.select("a.vip").attr("href")
      val subtitle = e.select("div.lvsubtitle").text()
      val rank = e.attr("r")
      val price = e.select("li.lvprice.prc span").text()
      val itemid = e.attr("listingid")
      var ratting = 0
      var ratting_href = ""
      if (!e.select("a.star-ratings__review-num").isEmpty) {
        val pattern = new Regex("(\\d+)")
        ratting = pattern.findFirstIn(e.select("a.star-ratings__review-num").text()).get.toInt
        println(s"ratting $ratting")
        ratting_href = e.select("a.star-ratings__review-num").attr("href")
      }
      val from = e.select("ul.lvdetails.left.space-zero.full-width li:eq(1)").text()
      val create_time = new Timestamp(sdf.parse(e.select("span.tme span").text()).getTime).toString.replace("1970", "2017")
      val format = e.select("li.lvformat span").text()
      val ship = e.select("span.ship").text()
      val src = e.select("img.img").attr("src")
      val keyword = "sumsung"
      val craete_date = create_time.split(" ")(0)
      println(s"create date: $craete_date")

      println(ratting, ship, create_time)
    }

  }

  def parseDocDetail(doc: Document, href: String): Unit = {
    val sdf = new SimpleDateFormat("  MMM d, yyyy HH:mm:ss", Locale.ENGLISH)

    val title = doc.select("h1#itemTitle").text()
    val ratting = doc.select("a._rvwlnk").text()
    val shopname = doc.select("span.mbg-nw").text()
    val sold_tag = doc.select("a:matches([\\d]+[\\s]+sold)")
    var sold = 0
    var sold_history = ""
    if (!sold_tag.isEmpty) {
      sold = sold_tag.text().replace(" sold", "").toInt
      sold_history = sold_tag.attr("href")
    }

    var last_time: Option[String] = None
    var last_time_tag = doc.select("div.vi-desc-revHistory").text()
    if (!last_time_tag.isEmpty) {
      val pattern = "Last updated on([^\\n]+)PST  View all revisions".r
      val pattern(data) = last_time_tag
      var last_time = Some(new Timestamp(sdf.parse(data).getTime).toString)
    }
    var price: Option[Int] = None
    if (!doc.select("span#convbinPrice").isEmpty) {
      println("no empty")
      val pattern = new Regex("(\\d+(,?\\d+\\.?\\d+)+)")
      println(s"doc is ${doc.select("span#convbinPrice").text()}")
      val r_price = pattern.findFirstIn(doc.select("span#convbinPrice").text()).get
      println(s"r price $r_price")
      price = Some(r_price.replace(",", "").toDouble.toInt)
    }
//    val last_time = new Timestamp(sdf.parse(data).getTime)
    println(price, doc.select("span#convbidPrice").text())
  }
}