package sample.crawl.ebay.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.json._

final case class Posts(text: String)

/**
  * 定义爬虫任务消息
  * @param kd 关键词
  */
final case class Keyword(kd: String)

final case class Shopname(name: String)

/**
  * 建立 post
  * @param posts
  */
final case class PostsCreateJob(posts: Posts)

/**
  * 取得 post
  * @param postid
  */
final case class PostsGetJob(postid: String)

/**
  * 如果沒有後端服務的話會發生失敗
  * @param text
  */
final case class JobFailed(text: String)

/**
  * 定义一个爬虫任务消息
  */

final case class CrawlJob(keyword: Keyword)
final case class CrawlShop(name: Shopname)

/**
  * 定義 Json 序列化跟反序列化
  */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val postsFormat = jsonFormat1(Posts)
  implicit val keyword_postsFormat = jsonFormat1(Keyword)
  implicit val shopname_postFormat = jsonFormat1(Shopname)
}

/**
  * 後端註冊 Post 服務
  */
case object PostsActorRegister