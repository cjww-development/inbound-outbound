/*
 *  Copyright 2020 CJWW Development
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package dev.cjww.http.responses

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc.Results.Ok
import play.api.mvc.{Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApiResponseSpec extends PlaySpec {
  object TestResponse extends ApiResponse

  implicit val request: Request[_] = FakeRequest()
    .withHeaders("requestId" -> "testRequestId")

  "withJsonResponseBody" should {
    "construct and return a result with a successful json response body" in {
      val result = contentAsJson(Future(TestResponse.withJsonResponseBody[Result](OK, JsString("test"))(x => Ok(x))))
      result.\("uri").as[String]       mustBe "/"
      result.\("method").as[String]    mustBe "GET"
      result.\("status").as[Int]       mustBe OK
      result.\("requestId").as[String] mustBe "testRequestId"
      result.\("body").as[String]      mustBe "test"
    }

    "construct and return a result with a unsuccessful json response body" in {
      val result = contentAsJson(Future(TestResponse.withJsonResponseBody[Result](INTERNAL_SERVER_ERROR, JsString("test"))(x => Ok(x))))
      result.\("uri").as[String]          mustBe "/"
      result.\("method").as[String]       mustBe "GET"
      result.\("status").as[Int]          mustBe INTERNAL_SERVER_ERROR
      result.\("requestId").as[String]    mustBe "testRequestId"
      result.\("errorMessage").as[String] mustBe "test"
    }

    "construct and return a result with a unsuccessful json response body with extra errorBody" in {
      val result = contentAsJson(Future(TestResponse.withJsonResponseBody[Result](BAD_REQUEST, Json.obj("reason" -> "test"), "test")(x => Ok(x))))
      result.\("uri").as[String]          mustBe "/"
      result.\("method").as[String]       mustBe "GET"
      result.\("status").as[Int]          mustBe BAD_REQUEST
      result.\("requestId").as[String]    mustBe "testRequestId"
      result.\("errorMessage").as[String] mustBe "test"
      result.\("errorBody").as[JsValue]   mustBe Json.parse("""{ "reason" : "test" }""")
    }
  }
}
