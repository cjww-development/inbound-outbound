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

package dev.cjww.http.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.slf4j.{Logger, LoggerFactory}

trait WireMockSetup {
  val wiremockPort = Ports.getRandomPort
  val testingHost  = "localhost"
  val wiremockUrl  = s"http://$testingHost:$wiremockPort"

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private val wireMockServer = new WireMockServer(
    wireMockConfig()
      .port(wiremockPort)
  )

  protected def startWm(): Unit = {
    logger.info(s"[startWm] - Starting wiremock server on port $wiremockPort")
    wireMockServer.start()
    WireMock.configureFor(testingHost, wiremockPort)
  }

  protected def stopWm(): Unit = {
    wireMockServer.stop()
  }

  protected def resetWm(): Unit = {
    wireMockServer.resetAll()
    WireMock.reset()
  }
}
