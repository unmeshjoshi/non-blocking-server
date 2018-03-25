import akka.actor.{ActorSystem, Cancellable}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

object AkkaServer extends App {

  implicit val system: ActorSystem = ActorSystem("StreamServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  var counter = 0

  val route: Route = get {
    path("") {
      get {
        complete({
          val source: Source[HttpEntity.Strict, Cancellable] = Source.tick(100.millis, 10.millis, ()).map { _ ⇒
              counter = counter + 1
              HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"request$counter")
          }
          source.toMat(Sink.head)(Keep.right).run()
        })
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route,"localhost", 8082)

  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine()

  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
