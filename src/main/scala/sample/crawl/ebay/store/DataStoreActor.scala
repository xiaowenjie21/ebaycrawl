package sample.crawl.ebay.store

import java.io.{File, PrintWriter}

import akka.actor.Actor
import akka.actor.ActorLogging
import sample.crawl.ebay.db.{SaveEbay, SaveEbayDetail, SaveShopEbay}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import sample.crawl.ebay.db.SlickMysql.{InsertDetailProduct, InsertIndexProduct, InsertShopProduct}

class DataStoreActor extends Actor with ActorLogging {

  val ec: ExecutionContext = context.system.dispatchers.lookup("blocking-io-dispatcher")

  def receive = {
    case SaveEbay(url, title, subtitle, rank, price, itemid, ratting, ratting_href, from,
    create_time, format, shipping, src, keyword, create_date) =>
      val future = Future {
        Try {
          log.info("缓存首页数据到mysql")
          InsertIndexProduct(url, title, subtitle, rank, create_time, price, itemid, ratting
            , ratting_href, format, from, shipping, src, keyword, create_date)
        } match {
          case Success(d: Unit) =>
            log.info("保存首页数据成功")
          case Failure(e) =>
            log.info(s"保存首页数据失败: $e")
        }
      }(ec)

    case SaveEbayDetail(href, title, sold, sold_history, last_update, shopname, sold_firsttime, price, price_unit) =>
      val future = Future {
        Try {
          log.info("缓存详情页数据到mysql")
          InsertDetailProduct(href, title, sold, sold_history, last_update, shopname, sold_firsttime, price, price_unit)
        } match {
          case Success(d: Unit) =>
            log.info("保存详情页面数据成功")
          case Failure(e) =>
            log.info(s"保存详情页面数据失败 $e")
        }
      }(ec)

    case SaveShopEbay(detail_href, title, subtitle, rank, create_time, price, itemid, ratting, ratting_href, from,
    format, ship, src, name, create_date) =>
      val future = Future {
        Try {
          log.info(s"缓存店铺数据到mysql, url: $detail_href")
          InsertShopProduct(detail_href, title, subtitle, rank, create_time, price, itemid, ratting, ratting_href,
            format, from, ship, src, name, create_date)
        } match {
          case Success(d: Unit) =>
            log.info("保存店铺数据成功")
          case Failure(e) =>
            log.info(s"保存店铺数据失败 $e")
        }
      }(ec)
  }
}

