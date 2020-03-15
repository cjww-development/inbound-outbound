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

import com.typesafe.config.ConfigFactory
import scoverage.ScoverageKeys

import scala.util.{Failure, Success, Try}

val libraryName = "inbound-outbound"

val btVersion: String = Try(ConfigFactory.load().getString("version")).getOrElse("0.1.0")

val dependencies: Seq[ModuleID] = Seq(
  "org.slf4j"              %  "slf4j-api"          % "1.7.30",
  "com.typesafe.play"      %% "play-ws"            % "2.8.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0"  % Test,
  "org.mockito"            %  "mockito-core"       % "3.2.4"  % Test,
  "com.github.tomakehurst" %  "wiremock-jre8"      % "2.25.1" % Test
)

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;/.data/..*;views.*;models.*;.*(AuthService|BuildInfo|Routes).*",
  ScoverageKeys.coverageMinimum          := 80,
  ScoverageKeys.coverageFailOnMinimum    := false,
  ScoverageKeys.coverageHighlighting     := true
)

lazy val library = Project(libraryName, file("."))
  .settings(scoverageSettings:_*)
  .settings(
    version                              :=  btVersion,
    scalaVersion                         :=  "2.13.1",
    organization                         :=  "com.cjww-dev.libs",
    resolvers                            ++= Seq("cjww-dev" at "https://dl.bintray.com/cjww-development/releases"),
    libraryDependencies                  ++= dependencies,
    bintrayOrganization                  :=  Some("cjww-development"),
    bintrayReleaseOnPublish in ThisBuild :=  true,
    bintrayRepository                    :=  "releases",
    bintrayOmitLicense                   :=  true,
    scalacOptions           in ThisBuild ++= Seq("-unchecked", "-deprecation"),
    testOptions             in Test      +=  Tests.Argument("-oF")
  )