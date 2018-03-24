package sample.crawl.ebay.db

import com.mongodb.casbah.MongoConnection

/**
  * Created by Administrator on 2017/9/4.
  */
object MongoFactory {
  private val SERVER = "139.159.212.35"
  private val PORT = 27017
  private val DATABASE = "akkadata"
  private val COLLECTION = "akkaebay"

  val connection = MongoConnection(SERVER)
  val collection = connection(DATABASE)(COLLECTION)

}
