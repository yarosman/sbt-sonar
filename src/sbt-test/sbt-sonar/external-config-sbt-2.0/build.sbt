import sbtsonar.SonarPlugin.autoImport.sonarUseExternalConfig

name := "external-config-sbt-2.0"

version := "0.1"

scalaVersion := "3.8.4"

sonarUseExternalConfig := true

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test
