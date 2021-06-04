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

package dev.cjww.http.responses

import org.joda.time.LocalDateTime
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.RequestHeader

trait ApiResponse {
  private def requestProperties(statusCode: Int)(implicit req: RequestHeader): JsObject = Json.obj(
    "uri"       -> s"${req.uri}",
    "method"    -> s"${req.method.toUpperCase}",
    "requestId" -> s"${req.headers.get("requestId").getOrElse("-")}",
    "status"    -> statusCode
  )

  private def requestStats: JsObject = Json.obj(
    "stats" -> Json.obj(
      "requestCompletedAt" -> s"${LocalDateTime.now()}"
    )
  )

  private val bodyKey: Int => String = status => if((200 to 299).contains(status)) "body" else "errorMessage"

  def withJsonResponseBody[T](statusCode: Int, body: JsValue)(result: JsValue => T)(implicit req: RequestHeader): T = {
    result(requestProperties(statusCode) ++ Json.obj(bodyKey(statusCode) -> body) ++ requestStats)
  }

  def withJsonResponseBody[T](statusCode: Int, body: JsValue, errorMessage: String)(result: JsValue => T)(implicit req: RequestHeader): T = {
    result(requestProperties(statusCode) ++ Json.obj("errorMessage" -> errorMessage, "errorBody" -> body) ++ requestStats)
  }
}
