package com.reactive.server

import java.util.concurrent.Executors

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

  val counter = new Counter

  val route: Route = get {
    path("") {
      get {
        complete {
          akka.pattern.after(100.millis, system.scheduler) {
            counter.next().map(x => s"request$x")
          }
        }
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8082)
}

class Counter {
  private implicit val ec: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  private var value = 0

  def next(): Future[Int] = Future {
    value += 1
    value
  }

  def shutdown(): Unit = ec.shutdown()
}
