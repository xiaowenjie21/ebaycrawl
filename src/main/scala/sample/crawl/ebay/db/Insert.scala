package sample.crawl.ebay.db

import com.mongodb.casbah.Imports._
import Stock._
/**
  * Created by Administrator on 2017/9/4.
  */
object Insert {
  //  val numbers = SaveMessage(List("1", "2", "3"), "f")
  //  val zms = SaveMessage(List("a", "b", "c"), "m")
  //
  //  saveStock(numbers)
  //  saveStock(zms)
  //
  def saveStock(saveMessage: SaveMessage): Unit = {
    val mongoObj = buildMongoDBObject(saveMessage)
    MongoFactory.collection.save(mongoObj)
  }

  def saveEbay(message: SaveEbay): Unit = {
    val mongoObj = buildMongoEbay(message)
    MongoFactory.collection.save(mongoObj)
  }

//  def saveEbayDetail(message: SaveEbayDetail): Unit = {
//    val mongoObj = builderMongoEbayDetail(message)
//    MongoFactory.collection.save(mongoObj)
//  }
}

