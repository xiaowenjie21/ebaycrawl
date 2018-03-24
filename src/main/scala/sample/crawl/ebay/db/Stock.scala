package sample.crawl.ebay.db


import com.mongodb.casbah.Imports._

import scala.collection.mutable

/**
  * Created by Administrator on 2017/9/4.
  */
case class SaveShopEbay(url: String, title: String, subtitle: String, rank: Int, create_time: String, price: String, itemid: String,
                        ratting: Int, ratting_href: String, from: String, format: String, ship: String,
                        src: String, shopname: String, create_date: String)

case class SaveEbay(url: String, title: String, subtitle: String, rank: Int, price: String, itemid: String, ratting: Int, ratting_href: String,
                    from: String, create_time: String, format: String, ship: String, src: String, keyword: String, create_date: String)

case class SaveMessage(address: mutable.Buffer[String])

case class SaveEbayDetail(href: String, title: String, sold: Option[Int], sold_history: String, last_update: Option[String]
                          , idx: Option[String], sold_firstTime: Option[String], price: Option[Int], price_unit: Option[String])

object Stock {
  def buildMongoDBObject(message: SaveMessage): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "address" -> message.address
    builder.result
  }

  def buildMongoEbay(message: SaveEbay): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "pageUrl" -> message.url
    builder += "title" -> message.title
    builder += "_id" -> Map( "keyword" -> message.keyword, "url" -> message.url)
    builder += "format" -> message.format
    builder += "create_time" -> message.create_time
    builder += "ship" -> message.ship
    builder.result()
  }

//  def builderMongoEbayDetail(message: SaveEbayDetail): MongoDBObject = {
//    val builder = MongoDBObject.newBuilder
//    builder += "title" -> message.title
//    builder += "href" -> message.href
//    builder += "category" -> message.category
//    builder += "ratting" -> message.ratting
//    builder += "price" -> message.price
//    builder += "shopname" -> message.shopname
//    builder.result
//  }
}
