import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Suite}

trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: TestKit with Suite =>
  override def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }
}
