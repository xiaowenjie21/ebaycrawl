package sample.crawl.ebay.http

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.pattern.ask
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives.pathEndOrSingleSlash
import akka.stream.Materializer
import akka.util.Timeout
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContext, Future}

trait PostsOperations extends Directives with JsonSupport {
  implicit val system: ActorSystem

  implicit val executor: ExecutionContext

  implicit val materializer: Materializer

  def config: Config

  val logger: LoggingAdapter

  implicit def postsSender: ActorRef

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  lazy val postsRoutes = {
    getRoute ~ route ~ route_shop
  }

//  val route =
//    path("keyword" / Segment) { keyword =>
//      get {
//        postsSender ! CrawlJob(keyword)
//        complete(StatusCodes.Created, Map("keyword" -> keyword))
//      }
//    }

  private val route =
    path("crawl" / "keyword") {
      pathEndOrSingleSlash
        post {
          logRequestResult("posts-post") {
            entity(as[Keyword]) { keyword =>
              val pages: Future[Int] = postsSender.ask(CrawlJob(keyword)).mapTo[Int]
              onSuccess(pages) { pages =>
                complete(StatusCodes.Created, Map("pages" -> pages))
              }
            }
          }
        }
    }

  private val route_shop =
    path("crawl" / "shopname") {
      pathEndOrSingleSlash
        post {
          logRequestResult("posts-post") {
            entity(as[Shopname]) { shopname =>
              val pages: Future[Int] = postsSender.ask(CrawlShop(shopname)).mapTo[Int]
              onSuccess(pages) { pages =>
                complete(StatusCodes.Created, Map("pages" -> pages))
              }
            }
          }
        }
    }



  private val getRoute =
    path("api" / "v1" / "posts" / Segment) { postid =>
      get {
        logRequestResult("posts-get") {
          val reply: Future[Any] = postsSender ? PostsGetJob(postid)
          onSuccess(reply) { x =>
            x match {
              case Right(posts: Posts) => complete(StatusCodes.OK, posts)
              case Left(msg: String) => complete(StatusCodes.InternalServerError, Map("msg" -> msg))
              case JobFailed(msg: String) => complete(StatusCodes.InternalServerError, Map("msg" -> msg))
            }
          }
        }
      }
    }

  private val postRoute =
    path("api" / "v1" / "posts") {
      post {
        logRequestResult("posts-post") {
          entity(as[Posts]) { posts =>
            val reply: Future[Any] = postsSender ? PostsCreateJob(posts)
            onSuccess(reply) { x =>
              x match {
                case Right(postid: String) => complete(StatusCodes.Created, Map("postid" -> postid))
                case Left(msg: String) => complete(StatusCodes.InternalServerError, Map("msg" -> msg))
                case JobFailed(msg: String) => complete(StatusCodes.InternalServerError, Map("msg" -> msg))
              }
            }
          }
        }
      }
    }
}