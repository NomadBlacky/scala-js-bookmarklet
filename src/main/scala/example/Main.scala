package example

import sttp.client3.{FetchBackend, Response, UriContext}

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object Main {
  def main(args: Array[String]): Unit = {
    val responseF: Future[Response[String]] =
      sttp.client3.quickRequest.get(uri"https://api.github.com/zen").send(FetchBackend())

    responseF.foreach { res =>
      org.scalajs.dom.window.alert(res.body)
    }
  }
}
