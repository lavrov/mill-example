import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}

object Main extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val routes = new Routes


  val (host, port) = ("localhost", 9000)

  val bindingFuture: Future[ServerBinding] = {
    Http().bindAndHandle(routes.handler, host, port)
  }

  val binding = Await.result(bindingFuture, Duration.Inf)
  println(s"Server listerning on $host:$port")

  val shutdownRequest = Promise[Unit]
  sys.addShutdownHook {
    shutdownRequest.success(())
  }
  Await.ready(shutdownRequest.future, Duration.Inf)
  println("Server exits")
}
