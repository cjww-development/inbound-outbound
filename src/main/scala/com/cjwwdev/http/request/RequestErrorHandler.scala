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

import org.slf4j.LoggerFactory
import play.api.http.{HttpErrorHandler, Writeable}
import play.api.http.Status.{FORBIDDEN, NOT_FOUND}
import play.api.mvc.Results.{InternalServerError, NotFound, Status, Forbidden, Redirect}
import play.api.mvc.{Call, RequestHeader, Result}

import scala.concurrent.Future

trait RequestErrorHandler[T] extends HttpErrorHandler {
  private val logger = LoggerFactory.getLogger(this.getClass)

  implicit val writer: Writeable[T]

  def standardError(rh: RequestHeader): T
  def notFoundError(rh: RequestHeader): T
  def serverError(rh: RequestHeader): T
  def forbiddenError(rh: RequestHeader): Either[T, Call]

  def forbiddenResult(error: Either[T, Call]): Result = {
    error.fold(staticMsg => Forbidden(staticMsg), call => Redirect(call))
  }

  override def onClientError(rh: RequestHeader, status: Int, msg: String): Future[Result] = {
    logger.warn(s"[onClientError] - Url: ${rh.uri}, status code: $status")
    status match {
      case NOT_FOUND => Future.successful(NotFound(notFoundError(rh)))
      case FORBIDDEN => Future.successful(forbiddenResult(forbiddenError(rh)))
      case _         => Future.successful(Status(status)(standardError(rh)))
    }
  }

  override def onServerError(rh: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"[onServerError] - Server Error!", exception)
    Future.successful(InternalServerError(serverError(rh)))
  }
}
