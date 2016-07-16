package im.mange.little

import java.util.concurrent.TimeUnit._

import io.shaka.http.Http._
import io.shaka.http._

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit._

import io.shaka.http.{Request, Response}
import io.shaka.http.Http._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration.Duration

//TODO: move this into little or little-client
object LittleClient {
  TrustAllSslCertificates

  def doRunRun(request: Request): Either[Exception, Response] = {
    try {
      //OR blocking
      val f = future { blocking {
        //TODO: should probably be scheduled
//        Thread.sleep(1000) //TODO: make me a config - sleep between probes
        Right(unsafeRun(request))
      } }
      f onSuccess { case status => status }
      f onFailure { case e => Left(e) }
      //TODO: make timeout be configurable

      Await.result(f, Duration(90, SECONDS))
    } catch {
//      case e: TimeoutException => {
//        println("### e:" + e + " with " + probe.description)
//        probeFailed("Probe took too long", probe)
//      }
//      //TODO: more gracefully handle ...
//      //net.liftweb.json.JsonParser.ParseException
//      //CancellationException
//      //InterruptedException
//      case e: FileNotFoundException => probeFailed("Probe does not exist", probe)
//      case e: ConnectException => probeFailed("Server not responding", probe)
      case e: Exception => Left(e)
    }
  }

  def unsafeRunAsString(request: Request) = unsafeRun(request).entityAsString

  private def unsafeRun(request: Request) = http(request)
}
