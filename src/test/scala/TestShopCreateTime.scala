import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}
import sample.crawl.ebay.message.IndexCrawlShopname
import sample.crawl.ebay.parser.ShopParserActor
import sample.crawl.ebay.parser.ShopParserActor.GetState

class TestShopCreateTime extends TestKit(ActorSystem("testactor"))
  with WordSpecLike with MustMatchers with StopSystemAfterAll {
  "test product create_time" must {
    "shop product createTime years question" in {
      val shopActor = system.actorOf(Props[ShopParserActor], "s3")
      shopActor ! IndexCrawlShopname("https://www.ebay.com/sch/limetropic/m.html?_nkw=&_armrs=1&_ipg=&_from=&_sop=10&rt=nc&LH_BIN=1", "test")
      shopActor ! GetState(testActor)
//      expectMsg(Vector("2017-01-09"))
    }
  }
}
