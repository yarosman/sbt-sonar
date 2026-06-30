import sbtsonar.SonarPlugin.autoImport.sonarProperties
import sbtsonar.SonarPlugin.autoImport.sonarScan

lazy val baseSettings = Seq(
  version := "0.1",
  scalaVersion := "3.8.4",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
)
lazy val sonarSettings = Seq(
  sonarProperties ++= Map(
    "sonar.projectName" -> "Multi Module",
    "sonar.modules" -> "module1,module2",
    "module1.sonar.projectName" -> "Module 1",
    "module2.sonar.projectName" -> "Module 2"
  )
)

lazy val module1 = (project in file("module1"))
  .settings(baseSettings)
  .settings(name := "module2")

lazy val module2 = (project in file("module2"))
  .settings(baseSettings)
  .settings(name := "module1")

lazy val multiModule = (project in file("."))
  .aggregate(module1, module2)
  .settings(name := "multi-module-sbt-2.0")
  .settings(baseSettings)
  .settings(sonarSettings)
  .settings(sonarScan / aggregate := false)
