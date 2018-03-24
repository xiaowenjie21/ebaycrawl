package sample.crawl.ebay.http

trait RestInterface extends PostsOperations {
  val routes = {
    postsRoutes
  }
}