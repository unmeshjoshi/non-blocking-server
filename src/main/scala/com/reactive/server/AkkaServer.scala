package com.reactive.server

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, ExecutionContextExecutorService, Future}

object AkkaServer extends App {

  implicit val system: ActorSystem = ActorSystem("StreamServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val counter = new AtomicInteger(0)

  val route: Route = get {
    path("") {
      get {
        complete {
          akka.pattern.after(100.millis, system.scheduler) {
            Future(s"request$counter.getAndIncrement()")
          }
        }
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, new Networks().hostname(), 8082)
}
