package sample.crawl.ebay.db


import slick.driver.MySQLDriver.simple._


object SlickMysql {

  val mysql = Database.forURL("jdbc:mysql://139.159.212.35:3306/ebay", "root","zhangxiao886",
    driver = "com.mysql.jdbc.Driver")

  class Tmp_ebay(tag: Tag) extends Table[(String, String, String, String, String, String)](tag, "tmp_ebay") {
    def title = column[String]("title")
    def href = column[String]("href", O.PrimaryKey)
    def ratting = column[String]("ratting")
    def price = column[String]("price")
    def shopname = column[String]("shopname")
    def category = column[String]("category")

    //每个Table中都应该有*方法，它的类型必须与前面定义的类型参数(Int, String)一致
    def * = (title, href, ratting, price, shopname, category)
  }

  class IndexProduct(tag: Tag) extends Table[(String, String, String, Int, String,
    String, String, Int, String, String, String, String, String, String, String)](tag, "IndexProduct") {
    def productUrl = column[String]("productUrl")
    def title = column[String]("title")
    def subtitle = column[String]("subtitle")
    def rank = column[Int]("rank")
    def create_time = column[String]("create_time")
    def price = column[String]("price")
    def itemid = column[String]("itemid")
    def ratting = column[Int]("ratting")
    def ratting_href = column[String]("ratting_href")
    def format = column[String]("format")
    def froms = column[String]("froms")
    def shipping = column[String]("shipping")
    def src = column[String]("src")
    def keyword = column[String]("keyword")
    def create_date = column[String]("create_date")

    def * = (productUrl, title, subtitle, rank, create_time, price, itemid, ratting, ratting_href
      , format, froms, shipping, src, keyword, create_date)
  }

  class ShopProduct(tag: Tag) extends Table[(String, String, String, Int, String,
    String, String, Int, String, String, String, String, String, String, String)](tag, "ShopProduct") {
    def productUrl = column[String]("productUrl")
    def title = column[String]("title")
    def subtitle = column[String]("subtitle")
    def rank = column[Int]("rank")
    def create_time = column[String]("create_time")
    def price = column[String]("price")
    def itemid = column[String]("itemid")
    def ratting = column[Int]("ratting")
    def ratting_href = column[String]("ratting_href")
    def format = column[String]("format")
    def froms = column[String]("froms")
    def shipping = column[String]("shipping")
    def src = column[String]("src")
    def shopname = column[String]("shopname")
    def create_date = column[String]("create_date")

    def * = (productUrl, title, subtitle, rank, create_time, price, itemid, ratting, ratting_href
      , format, froms, shipping, src, shopname, create_date)
  }


  class DetailProduct(tag: Tag) extends Table[(String, String, Option[Int], String, Option[String]
    , Option[String], Option[String], Option[Int], Option[String])](tag, "DetailProduct") {

    def productUrl = column[String]("productUrl")
    def title = column[String]("title")
    def sold = column[Option[Int]]("sold")
    def sold_history = column[String]("sold_history")
    def last_update = column[Option[String]]("last_update")
    def idx = column[Option[String]]("idx")
    def first_sold_record = column[Option[String]]("first_sold_record")
    def price = column[Option[Int]]("price")
    def price_unit = column[Option[String]]("price_unit")

    def * = (productUrl, title, sold, sold_history, last_update, idx, first_sold_record, price, price_unit)
  }

  def InsertDetailProduct(url: String, title: String, sold: Option[Int], sold_history: String, last_update: Option[String]
                          , idx: Option[String], sold_firsttime: Option[String], price: Option[Int], price_unit: Option[String]): Unit = {
    val T_detailProduct = TableQuery[DetailProduct]
    mysql withSession {
      implicit session  =>
        T_detailProduct += (url, title, sold, sold_history, last_update, idx, sold_firsttime, price, price_unit)
    }
  }




  def InsertShopProduct(url: String, title: String, subtitle: String, rank: Int, create_time: String, price: String,
                        itemid: String, ratting: Int, ratting_href: String, format: String, from: String, ship: String,
                        src: String, shopname: String, create_date: String): Unit = {
    val T_shopProduct = TableQuery[ShopProduct]
    mysql withSession {
      implicit session =>
        T_shopProduct += (url, title, subtitle, rank, create_time, price, itemid, ratting, ratting_href, format, from,
                          ship, src, shopname,create_date)
    }
  }


  def InsertIndexProduct(productUrl: String, title: String, subtitle: String, rank: Int, time: String, price: String,
                         itemid: String, ratting: Int, ratting_href: String, format: String, froms: String, shipping: String,
                         src: String, keyword: String, create_date: String): Unit = {
    val T_indexProduct = TableQuery[IndexProduct]
    mysql withSession {

      //后续方法中当做隐式参数传递
      implicit session =>
        T_indexProduct += (productUrl, title, subtitle, rank, time, price, itemid, ratting, ratting_href
          , format, froms, shipping, src, keyword, create_date)
        //数据库查询（这里返回所有数据）
//        ts.foreach { x => println("k="+x._1+" v="+x._2) }
//        ts.foreach { x => println(x)}

    }
  }
}
