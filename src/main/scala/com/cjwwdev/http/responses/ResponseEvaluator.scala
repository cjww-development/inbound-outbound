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

import play.api.libs.ws.WSResponse

object ResponseEvaluator extends WsResponseHelpers {
  private case class Contains(range: Range) {
    def unapply(int: Int): Boolean = range contains int
  }

  private val success     = Contains(200 to 299)
  private val clientError = Contains(400 to 499)
  private val serverError = Contains(500 to 599)

  type ConnectorResponse          = Either[WSResponse, WSResponse]
  val SuccessResponse: Right.type = scala.util.Right
  val ErrorResponse: Left.type    = scala.util.Left

  def apply(url: String, method: String, response: WSResponse): ConnectorResponse = response.status match {
    case success()                     => SuccessResponse(response.logResponse(url, method, inError = false))
    case clientError() | serverError() => ErrorResponse(response.logResponse(url, method, inError = true))
  }
}
