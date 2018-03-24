package sample.crawl.ebay.download

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.util.{Failure, Success, Try}

case class ParseContent(d: Document, url: String)

object GetUrl {
  def promiseGetUrl(times: Int = 30, delay: Long = 500)(url: String): ParseContent = {
    println(s"下载器正在下载 $url")
    Try(Jsoup.connect(url).timeout(3000).get()) match {
      case Failure(e) =>
        if (times != 0) {
          println(s"times: 失败中， 重试 $url, 重试次数 $times")
          println("times: 失败中，重试" + e.getMessage)
          Thread.sleep(delay);promiseGetUrl(times -1, delay)(url)
        } else throw e
      case Success(d) =>
        ParseContent(d, url)
    }
  }
}
