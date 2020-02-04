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

package com.cjwwdev.http.caller

import com.cjwwdev.http.wiremock.{StubbedBasicHttpCalls, WireMockSetup}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

import scala.concurrent.ExecutionContext.Implicits.global

class HttpSpec extends PlaySpec
  with FutureAwaits
  with DefaultAwaitTimeout
  with StubbedBasicHttpCalls
  with WireMockSetup
  with BeforeAndAfterEach
  with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    startWm()
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    resetWm()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    stopWm()
  }

  val app: Application = new GuiceApplicationBuilder()
    .build()

  val testHttp: DefaultHttp = app.injector.instanceOf[DefaultHttp]

  "head" should {
    "return a 200" in {
      stubbedHead("/test/head", 200)
      val res = await(testHttp.head(s"http://localhost:${wiremockPort}/test/head", Seq()))
      assert(res.isRight)
      assert(res.exists(_.status == 200))
    }

    "return a 404" in {
      stubbedHead("/test/head", 404)
      val res = await(testHttp.head(s"http://localhost:${wiremockPort}/test/head", Seq()))
      assert(res.isLeft)
      assert(res.left.exists(_.status == 404))
    }
  }

  "get" should {
    "return a 200" in {
      stubbedGet("/test/get", 200, "testing testing")
      val res = await(testHttp.get(s"http://localhost:${wiremockPort}/test/get", Seq()))
      assert(res.isRight)
      assert(res.exists(_.status == 200))
    }

    "return a 404" in {
      stubbedGet("/test/get", 404, """{ "errorMessage" : "test123" }""")
      val res = await(testHttp.get(s"http://localhost:${wiremockPort}/test/get", Seq()))
      assert(res.isLeft)
      assert(res.left.exists(_.status == 404))
    }
  }

  "post" should {
    "return a 200" in {
      stubbedPost("/test/post", 200, """{ "test" : "test123" }""")
      val res = await(testHttp.post(s"http://localhost:${wiremockPort}/test/post", "testing", Seq()))
      assert(res.isRight)
      assert(res.exists(_.status == 200))
    }

    "return a 404" in {
      stubbedPost("/test/post", 404, """{ "errorMessage" : "test123" }""")
      val res = await(testHttp.post(s"http://localhost:${wiremockPort}/test/post", "testing", Seq()))
      assert(res.isLeft)
      assert(res.left.exists(_.status == 404))
    }
  }

  "patch" should {
    "return a 200" in {
      stubbedPatch("/test/patch", 200, """{ "test" : "test123" }""")
      val res = await(testHttp.patch(s"http://localhost:${wiremockPort}/test/patch", "testing", Seq()))
      assert(res.isRight)
      assert(res.exists(_.status == 200))
    }

    "return a 404" in {
      stubbedPatch("/test/patch", 404, """{ "errorMessage" : "test123" }""")
      val res = await(testHttp.patch(s"http://localhost:${wiremockPort}/test/patch", "testing", Seq()))
      assert(res.isLeft)
      assert(res.left.exists(_.status == 404))
    }
  }

  "put" should {
    "return a 200" in {
      stubbedPut("/test/put", 200, """{ "test" : "test123" }""")
      val res = await(testHttp.put(s"http://localhost:${wiremockPort}/test/put", "testing", Seq()))
      assert(res.isRight)
      assert(res.exists(_.status == 200))
    }

    "return a 404" in {
      stubbedPut("/test/put", 404, """{ "errorMessage" : "test123" }""")
      val res = await(testHttp.put(s"http://localhost:${wiremockPort}/test/put", "testing", Seq()))
      assert(res.isLeft)
      assert(res.left.exists(_.status == 404))
    }
  }

  "delete" should {
    "return a 200" in {
      stubbedDelete("/test/delete", 200, """{ "test" : "test123" }""")
      val res = await(testHttp.delete(s"http://localhost:${wiremockPort}/test/delete", Seq()))
      assert(res.isRight)
      assert(res.exists(_.status == 200))
    }

    "return a 404" in {
      stubbedDelete("/test/delete", 404, """{ "errorMessage" : "test123" }""")
      val res = await(testHttp.delete(s"http://localhost:${wiremockPort}/test/delete", Seq()))
      assert(res.isLeft)
      assert(res.left.exists(_.status == 404))
    }
  }
}
