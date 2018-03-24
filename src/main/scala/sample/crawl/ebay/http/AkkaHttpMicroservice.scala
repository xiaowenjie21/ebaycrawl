package sample.crawl.ebay.http

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.util.Try


object AkkaHttpMicroservice extends App with RestInterface {
  override val config = ConfigFactory.parseString("akka.cluster.roles = [frontend]").
    withFallback(ConfigFactory.load("ebay"))

  override implicit val system = ActorSystem("ClusterSystem", config)

  override implicit val postsSender = system.actorOf(Props(classOf[PostsSender]), name = "httpfrontend")

  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val logger = Logging(system, getClass)

  Cluster(system).registerOnMemberUp {
    postsSender
  }

  Cluster(system).registerOnMemberRemoved {
    // exit JVM when ActorSystem has been terminated
    system.registerOnTermination(System.exit(0))
    // shut down ActorSystem
    system.terminate()

    // In case ActorSystem shutdown takes longer than 10 seconds,
    // exit the JVM forcefully anyway.
    // We must spawn a separate thread to not block current thread,
    // since that would have blocked the shutdown of the ActorSystem.
    new Thread {
      override def run(): Unit = {
        if (Try(Await.ready(system.whenTerminated, 30.seconds)).isFailure)
          System.exit(-1)
      }
    }.start()
  }

  val port = config.getInt("http.port")
  val interface = config.getString("http.interface")

  val binding = Http().bindAndHandle(routes, interface, port)
  logger.info(s"Bound to port $port on interface $interface")
  binding onFailure {
    case ex: Exception ⇒
      logger.error(s"Failed to bind to $interface:$port!", ex)
  }
  sys.addShutdownHook(system.terminate())
}