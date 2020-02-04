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

package com.cjwwdev.http.responses

import com.cjwwdev.http.mocks.MockHttpUtils
import com.cjwwdev.http.responses.ResponseEvaluator.ConnectorResponse
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsString, Json}
import play.api.libs.ws.WSResponse
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ResponseEvaluatorSpec extends PlaySpec with MockHttpUtils {

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  def testEvaluate(wsResponse: WSResponse): ConnectorResponse = ResponseEvaluator("/test/uri", "GET", wsResponse)

  "EvaluateResponse" should {
    "return a WsResponse" when {
      "the status code is in the 2xx range (200)" in {
        val resp = mockResponse(JsString(""), OK)
        val result = testEvaluate(resp)
        result mustBe Right(resp)
      }

      "the status code is in the 2xx range (204)" in {
        val resp = mockResponse(JsString(""), NO_CONTENT)
        val result = testEvaluate(resp)
        result mustBe Right(resp)
      }

      "the status code is in the 4xx range (404)" in {
        val resp = mockResponse(Json.parse("""{ "errorMessage" : "Error!" }"""), NOT_FOUND)
        val result = testEvaluate(resp)
        result mustBe Left(resp)
      }

      "the status code is in the 5xx range (500)" in {
        val resp = mockResponse(Json.parse("""{ "errorMessage" : "Error!" }"""), INTERNAL_SERVER_ERROR)
        val result = testEvaluate(resp)
        result mustBe Left(resp)
      }
    }
  }
}
