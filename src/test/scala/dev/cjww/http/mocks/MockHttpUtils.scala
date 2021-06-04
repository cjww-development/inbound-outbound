/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.cjww.http.mocks

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSResponse}

import java.net.URI
import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable}

trait MockHttpUtils extends MockitoSugar {
  implicit val duration: Duration = 5.seconds
  def await[T](future : Awaitable[T]) : T = Await.result(future, duration)

  val mockWsClient: WSClient = mock[WSClient]

  def mockResponse(bodyIn: JsValue, code: Int): WSResponse = new WSResponse {
    override def uri: URI = ???
    override def headers: Map[String, Seq[String]]   = ???
    override def bodyAsSource: Source[ByteString, _] = ???
    override def statusText                          = ""
    override def underlying[T]                       = ???
    override def xml                                 = ???
    override def body                                = ???
    override def header(key: String)                 = ???
    override def cookie(name: String)                = ???
    override def bodyAsBytes                         = ???
    override def cookies                             = ???
    override def status                              = code
    override def json                                = bodyIn
    override def allHeaders                          = ???
  }
}
