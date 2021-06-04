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

import play.api.inject.ApplicationLifecycle
import play.api.libs.ws.WSClient

import javax.inject.Inject
import scala.concurrent.Future

class DefaultHttp @Inject()(val applicationLifecycle: ApplicationLifecycle,
                            val wsClient: WSClient) extends Http {
  applicationLifecycle.addStopHook { () =>
    Future.successful(wsClient.close())
  }
}

trait Http extends
  HttpHead with
  HttpGet with
  HttpPost with
  HttpPatch with
  HttpPut with
  HttpDelete
