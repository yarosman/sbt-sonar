import java.time.Year

import sbtheader.License

enablePlugins(AutomateHeaderPlugin)
enablePlugins(SbtPlugin)

addSbtPlugin("com.github.sbt" % "sbt2-compat" % "0.1.0")

name := "sbt-sonar"
organization := "io.github.yarosman"
homepage := Some(url("https://github.com/yarosman/sbt-sonar"))

// Licence
organizationName := "All sbt-sonar contributors"
startYear := Some(2016)
licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
headerLicense := Some(
  License.ALv2(
    s"${startYear.value.get}-${Year.now}",
    organizationName.value
  )
)
headerResources / excludeFilter := "*.scala"
scmInfo := Some(
  ScmInfo(
    url("https://github.com/yarosman/sbt-sonar"),
    "scm:git:https://github.com/yarosman/sbt-sonar.git",
    Some("scm:git:git@github.com:yarosman/sbt-sonar.git")
  )
)
developers := List(
  Developer(
    "yarosman",
    "Yaroslav Derman",
    "@yarosman",
    url("https://github.com/yarosman")
  )
)

scalaVersion := "2.12.21"
crossScalaVersions := Seq("2.12.21", "3.8.4")
pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.12.13"
    case _      => "2.0.0"
  }
}
sbtPlugin := true
scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-deprecation"
)
libraryDependencies ++= {
  val collectionCompat =
    if (scalaBinaryVersion.value == "2.12") {
      Seq("org.scala-lang.modules" %% "scala-collection-compat" % "2.14.0")
    } else {
      Nil
    }

  List(
    "org.sonarsource.scanner.api" % "sonar-scanner-api"       % "2.16.3.1081" % Compile,
    "org.scalatest"              %% "scalatest"               % "3.2.10"      % Test,
    "org.mockito"                %% "mockito-scala-scalatest" % "2.2.1"       % Test
  ) ++ collectionCompat
}
ThisBuild / scalafmtOnCompile :=
  sys.env
    .get("CI")
    .forall(_.toLowerCase == "false")
Global / cancelable := true

// Scripted
scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++ Seq(
    "-Xmx1024M",
    "-Dplugin.version=" + version.value,
    "-Dsonar.host.url=http://localhost"
  )
}
scriptedBufferLog := false
