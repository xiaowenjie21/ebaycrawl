package sample.crawl.ebay


object EbayCrawlApp {
  def main(args: Array[String]): Unit = {
    EbayCrawlBackend.main(Seq("2551").toArray)
    EbayCrawlBackend.main(Seq("2552").toArray)
    EbayCrawlBackend.main(Seq("2553").toArray)
//    EbayCrawlFrontend.main(Array.empty)
  }
}
