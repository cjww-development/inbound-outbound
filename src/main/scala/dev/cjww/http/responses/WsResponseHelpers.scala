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

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import play.api.http.HttpVerbs
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import play.utils.Colors

import scala.util.Try

trait WsResponseHelpers {
  implicit class WsResponseOps(response: WSResponse) {
    private val colouredOutput: Boolean = Try(ConfigFactory.load().getBoolean("logging.colouredOutput")).getOrElse(false)

    private val logger = LoggerFactory.getLogger(this.getClass)

    def logResponse(url: String, method: String, inError: Boolean): WSResponse = {
      response.logOutboundCall(url, method, inError)
      if(inError && (method != HttpVerbs.HEAD)) response.logError() else response
    }

    protected def logOutboundCall(url: String, method: String, inError: Boolean): WSResponse = {
      val methodColour: String = logInColour(method, inError)(Colors.yellow)
      val urlColour: String    = logInColour(url, inError)(Colors.green)
      val statusColour: String = logInColour(response.status.toString, inError)(Colors.cyan)
      val logString = s"Outbound $methodColour call to $urlColour returned a $statusColour"
      if(inError) logger.error(logString) else logger.info(logString)
      response
    }

    protected def logError(): WSResponse = {
      val jsBody       = response.json
      val errorMessage = jsBody.\("errorMessage").as[String]
      val errorBody    = jsBody.\("errorBody").asOpt[JsValue]
      val logMsg       = errorBody.fold[String](s"Error message: $errorMessage")(js => s"Error message: $errorMessage -> Error body ${Json.prettyPrint(js)}")
      logger.error(logMsg)
      response
    }

    protected def logInColour(log: String, inError: Boolean)(logString: String => String): String = {
      (inError, colouredOutput) match {
        case (true, true)  => Colors.red(log)
        case (false, true) => logString(log)
        case (_, _)        => log
      }
    }
  }
}
