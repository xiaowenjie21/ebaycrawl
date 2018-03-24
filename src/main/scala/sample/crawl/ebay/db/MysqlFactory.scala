package sample.crawl.ebay.db

import java.sql.{Connection, Date, DriverManager, SQLException}



object MysqlFactory {

  val driverClass = "com.mysql.jdbc.Driver"
  val jdbcUrl = "jdbc:mysql://127.0.0.1:3306/ebay"
  val user = "root"
  val password = "yourpassword"

  try {
    Class.forName(driverClass)
  } catch {
    case e: ClassNotFoundException => throw e
    case e: Exception => throw e
  }

  @throws(classOf[SQLException])
  def getConnection: Connection = {
    DriverManager.getConnection(jdbcUrl, user, password)
  }

  @throws(classOf[SQLException])
  def doTrancation(transactions: Set[String]) : Unit = {
    val connection = getConnection
    connection.setAutoCommit(false)
    transactions.foreach {
      connection.createStatement.execute(_)
    }
    connection.commit()
    connection.close()
  }
}

