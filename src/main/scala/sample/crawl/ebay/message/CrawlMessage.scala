package sample.crawl.ebay.message

case class CrawlKeyword(kd: String)
case class NextPage(nexthref: String)
final case class IndexCrawlKeyword(url: String, keyword: String)
final case class IndexCrawlShopname(url: String, shopname: String)

object CrawlMessage {

}
