/*
 * Copyright 2020 CJWW Development
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

package com.cjwwdev.http.request

import java.net.URI

import akka.util.ByteString
import org.scalatestplus.play.PlaySpec
import play.api.http.Status.NOT_FOUND
import play.api.http.Writeable
import play.api.libs.json.{JsValue, Json}
import play.api.libs.typedmap.TypedMap
import play.api.libs.ws.DefaultBodyWritables
import play.api.mvc.request.{RemoteConnection, RequestTarget}
import play.api.mvc.{Call, Headers, RequestHeader}
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.twirl.api.Html

class RequestErrorHandlerSpec extends PlaySpec with FutureAwaits with DefaultAwaitTimeout with DefaultBodyWritables {

  val testJsonRequestErrorHandler: RequestErrorHandler[JsValue] = new RequestErrorHandler[JsValue] {
    override implicit val writer: Writeable[JsValue] = Writeable.writeableOf_JsValue
    override def standardError(status: Int)(implicit rh: RequestHeader): JsValue = Json.parse("""{ "test" : "standard" }""")
    override def notFoundError(implicit rh: RequestHeader): JsValue = Json.parse("""{ "test" : "not found" }""")
    override def serverError(implicit rh: RequestHeader): JsValue = Json.parse("""{ "test" : "server error" }""")
    override def forbiddenError(implicit rh: RequestHeader): Either[JsValue, Call] = Left(Json.parse("""{ "test" : "forbidden" }"""))
  }

  val testHtmlRequestErrorHandler: RequestErrorHandler[Html] = new RequestErrorHandler[Html] {
    override implicit val writer: Writeable[Html] = Writeable(html => ByteString(html.body), contentType = Some("text/html"))
    override def standardError(status: Int)(implicit rh: RequestHeader): Html = Html("<p>Standard</p>")
    override def notFoundError(implicit rh: RequestHeader): Html = Html("<p>Not found</p>")
    override def serverError(implicit rh: RequestHeader): Html = Html("<p>Server error</p>")
    override def forbiddenError(implicit rh: RequestHeader): Either[Html, Call] = Right(Call("GET", "/test/redirect"))
  }

  val requestHeader: RequestHeader = new RequestHeader {
    override def connection: RemoteConnection = ???
    override def method: String = ???
    override def target: RequestTarget = new RequestTarget {
      override def uri: URI = ???
      override def uriString: String = "/test/uri"
      override def path: String = ???
      override def queryMap: Map[String, Seq[String]] = ???
    }
    override def version: String = ???
    override def headers: Headers = ???
    override def attrs: TypedMap = ???
  }

  "onClientError" should {
    "return a NOT FOUND" when {
      "calling a frontend" in {
        val res = testHtmlRequestErrorHandler.onClientError(requestHeader, NOT_FOUND)
        status(res) mustBe NOT_FOUND
        contentAsString(res) mustBe "<p>Not found</p>"
      }

      "calling a REST api" in {
        val res = testJsonRequestErrorHandler.onClientError(requestHeader, NOT_FOUND)
        status(res) mustBe NOT_FOUND
        contentAsJson(res) mustBe Json.parse("""{ "test" : "not found" }""")
      }
    }

    "return a BAD REQUEST" when {
      "calling a frontend" in {
        val res = testHtmlRequestErrorHandler.onClientError(requestHeader, BAD_REQUEST)
        status(res) mustBe BAD_REQUEST
        contentAsString(res) mustBe "<p>Standard</p>"
      }

      "calling a REST api" in {
        val res = testJsonRequestErrorHandler.onClientError(requestHeader, BAD_REQUEST)
        status(res) mustBe BAD_REQUEST
        contentAsJson(res) mustBe Json.parse("""{ "test" : "standard" }""")
      }
    }
  }

  "onServerError" should {
    "return an INTERNAL SERVER ERROR" when {
      "calling a frontend" in {
        val res = testHtmlRequestErrorHandler.onServerError(requestHeader, new Exception("Testing testing"))
        status(res) mustBe INTERNAL_SERVER_ERROR
        contentAsString(res) mustBe "<p>Server error</p>"
      }

      "calling a REST api" in {
        val res = testJsonRequestErrorHandler.onServerError(requestHeader, new Exception("Testing testing"))
        status(res) mustBe INTERNAL_SERVER_ERROR
        contentAsJson(res) mustBe Json.parse("""{ "test" : "server error" }""")
      }
    }
  }
}
