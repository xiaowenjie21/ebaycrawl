package sample.crawl.ebay.http

import akka.actor._
import akka.routing.FromConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import sample.crawl.ebay.message.{CrawlKeyword, IndexCrawlKeyword, IndexCrawlShopname}

class PostsSender extends Actor with ActorLogging {

  val backend = context.actorOf(FromConfig.props(), name = "CrawlBackendRouter")

  var postsActors = IndexedSeq.empty[ActorRef]

  val baseurl = "https://www.ebay.com/sch/i.html?_sacat=0&_sop=10&_nkw=%s&rt=nc&LH_BIN=1&_pgn=%d"
  val  more_shop_url = "https://www.ebay.com/sch/m.html?_nkw=&_armrs=1&_from=&LH_BIN=1&_ssn=%s&_sop=10&_pgn=%d&_skc=50&rt=nc"
  val shop_url = "https://www.ebay.com/sch/%s/m.html?_nkw=&_armrs=1&_ipg=&_from=&_sop=10&rt=nc&LH_BIN=1"
  var jobCounter = 0

  override def receive: Receive = {
    case CrawlJob(keyword: Keyword) =>
      val kd = keyword.kd
      log.info(s"创建了一个爬虫任务 关键词是 $kd")
      val doc = Jsoup.connect(baseurl.format(kd, 1)).get()
      val page = PostsSender.getPages(doc)
      sender() ! page // 得到总页数后返回它
      (1 to page).par.foreach { x =>
        backend ! IndexCrawlKeyword(baseurl.format(kd, x), kd)
      }

    case CrawlShop(shopname: Shopname) =>
      val name = shopname.name
      log.info(s"创建了一个爬虫任务 店铺名称是 $name")
      val doc = Jsoup.connect(shop_url.format(name)).get()
      val page = PostsSender.getPages(doc)
      sender() ! page // 得到总页数后返回它
      if (page > 1) {
        (1 to page).par.foreach { x =>
          backend ! IndexCrawlShopname(more_shop_url.format(name, x), name)
        }
      } else {
        backend ! IndexCrawlShopname(shop_url.format(name), name)
      }

    case job: PostsCreateJob if postsActors.isEmpty =>
      sender() ! JobFailed("Service unavailable, try again later")

    case job: PostsGetJob if postsActors.isEmpty =>
      sender() ! JobFailed("Service unavailable, try again later")

    case job: PostsCreateJob =>
      jobCounter += 1
      postsActors(jobCounter % postsActors.size) forward job

    case job: PostsGetJob =>
      jobCounter += 1
      postsActors(jobCounter % postsActors.size) forward job

    //後端註冊服務
    case PostsActorRegister if !postsActors.contains(sender()) =>
      context watch sender()
      postsActors = postsActors :+ sender()

    //後端離開服務
    case Terminated(a) =>
      postsActors = postsActors.filterNot(_ == a)
  }
}

object PostsSender {
  private def getPages(doc: Document): Int = {
    val cnt = doc.select("span.rcnt").text().replace(",", "").toInt
    if (cnt < 10000) {
      if (cnt % 50 > 0) cnt /50 + 1 else cnt /50
    }
    else {
      200
    }
  }
}