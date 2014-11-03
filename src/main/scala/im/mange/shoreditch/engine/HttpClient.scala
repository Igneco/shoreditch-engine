package im.mange.shoreditch.engine

import io.shaka.http.Http._
import io.shaka.http.Request.GET
import io.shaka.http.TrustAllSslCertificates

//TODO: I'm sure this can go no, use golden retriever instead
object HttpClient {
  TrustAllSslCertificates

  def unsafeGet(resource: String, useProxy: Boolean = false) = {
    val response = http(GET(resource))

    //if (useProxy) {
    //  val httpViaProxy = http(proxy("my.proxy.server", 8080))
    //  val response = httpViaProxy(GET(resource))
    //}

    //    http(GET()) //default to proxy = None
    //    http(GET(),proxy = Some(Proxy(url = "foo.bar.com:80"))) //default to auth = None
    //    http(GET(),proxy = Some(Proxy(url = "foo.bar.com:80", auth = Some(Auth(username = "x", password = "y")))))

    //TODO: should probably check response code
    response.entityAsString
  }
}