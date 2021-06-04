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

package dev.cjww.http.caller

import dev.cjww.http.responses.ResponseEvaluator
import dev.cjww.http.responses.ResponseEvaluator.ConnectorResponse
import play.api.http.HttpVerbs
import play.api.libs.ws.{BodyWritable, WSClient}

import scala.concurrent.{Future, ExecutionContext => ExC}

trait HttpPost {
  val wsClient: WSClient

  def post[T: BodyWritable](url: String, data: T, headers: Seq[(String, String)])(implicit ec: ExC): Future[ConnectorResponse] = {
    wsClient
      .url(url)
      .withHttpHeaders(headers:_*)
      .post(data)
      .map(ResponseEvaluator(url, HttpVerbs.POST, _))
  }
}
