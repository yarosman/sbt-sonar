import sbtsonar.SonarPlugin.autoImport.sonarUseExternalConfig

name := "external-config"

version := "0.1"

scalaVersion := "2.12.20"

ThisBuild / scapegoatVersion := "3.2.0"

Scapegoat / scalacOptions := (Scapegoat / scalacOptions).value
  .filterNot(_.startsWith("-P:scapegoat:minimalLevel:"))

sonarUseExternalConfig := true

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
