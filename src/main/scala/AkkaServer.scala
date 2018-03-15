import java.io.{File, FileOutputStream}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object AkkaServer extends App {

  implicit val system: ActorSystem = ActorSystem("StreamServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  var counter = 0
  val route = get {
    path("") {
      get {
        complete({

          val file = new File("/Users/salonivithalani/Desktop/tw_tmt/extra/non-blocking-server/src/main/scala/test1.txt")
          file.createNewFile
          val os = new FileOutputStream(file)
          var i = 0
          while ( {
            i < 900000
          }) {
            os.write(i)

            {
              i += 1; i - 1
            }
          }
          os.flush()
          os.close()

          counter = counter + 1
          HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"request$counter")
        })
      }
    }

  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine()

  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
