<h1 align="left">sbt-sonar</h1>

[![release-badge]][release] [![maven-sbt1-badge]][maven-sbt1] [![maven-sbt2-badge]][maven-sbt2]

[release]: https://github.com/yarosman/sbt-sonar/actions/workflows/release.yml
[release-badge]:
  https://img.shields.io/github/actions/workflow/status/yarosman/sbt-sonar/release.yml?branch=master
[maven-sbt1]: https://central.sonatype.com/artifact/io.github.yarosman/sbt-sonar_2.12_1.0/3.0.0
[maven-sbt1-badge]:
  https://img.shields.io/maven-central/v/io.github.yarosman/sbt-sonar_2.12_1.0?label=sbt%201
[maven-sbt2]: https://central.sonatype.com/artifact/io.github.yarosman/sbt-sonar_sbt2_3/3.0.0
[maven-sbt2-badge]:
  https://img.shields.io/maven-central/v/io.github.yarosman/sbt-sonar_sbt2_3?label=sbt%202

sbt-sonar is an sbt plugin, which provides an easy way to integrate Scala
projects with [SonarQube](https://www.sonarqube.org) - a tool for continuous
code inspection and quality management :white_check_mark:.

Under the hood, it uses the embedded
[sonar-scanner API](https://github.com/SonarSource/sonar-scanner-api) library,
which allows you to run SonarQube scan without the need to have the
sonar-scanner executable installed in your environment.

This plugin is particularly useful for CI when used together with e.g.
[sbt-release](https://www.github.com/sbt/sbt-release) plugin for an automated
release process in your project, but it can be also used on its own.

## Requirements

- sbt 1.x or sbt 2.x. Version `3.0.0` is published for sbt 1.x
  (`sbt-sonar_2.12_1.0`) and sbt 2.x (`sbt-sonar_sbt2_3`).
- A reachable [SonarQube](https://www.sonarsource.com/products/sonarqube/)
  server.
- For Scala coverage and static-analysis reports, a SonarQube Scala plugin that
  understands the report properties you configure.

## Installation

Add the plugin to `project/plugins.sbt`:

```scala
addSbtPlugin("io.github.yarosman" % "sbt-sonar" % "3.0.0")
```

Use the same `addSbtPlugin` line for sbt 1 and sbt 2 projects. sbt resolves the
correct published artifact for the running sbt version.

The plugin is an auto plugin with `allRequirements`, so it is enabled
automatically. You only need imports when you refer to its settings or task in
`build.sbt`.

## Quick start

Define at least the SonarQube server URL in `build.sbt`:

```scala
import sbtsonar.SonarPlugin.autoImport.sonarProperties

sonarProperties ++= Map(
  "sonar.host.url" -> "https://sonarqube.example.com"
)
```

Run analysis from sbt:

```bash
sbt sonarScan
```

You can also pass Sonar properties as JVM system properties. These values
override values from `sonarProperties`:

```bash
sbt -Dsonar.host.url=https://sonarqube.example.com -Dsonar.token="$SONAR_TOKEN" sonarScan
```

## Configuration in sbt

By default, `sonarScan` uses the `sonarProperties` setting. sbt-sonar adds these
properties automatically:

- `sonar.projectName` from `name`
- `sonar.projectKey` from `normalizedName`
- `sonar.projectVersion` from `version` when `sonarScan` runs
- `sonar.projectBaseDir` from `baseDirectory`
- `sonar.sourceEncoding` as `UTF-8`
- `sonar.scala.version` from `scalaVersion`
- `sonar.sources` from `Compile / scalaSource`, relative to `baseDirectory`
- `sonar.tests` from `Test / scalaSource`, relative to `baseDirectory`
- Scala coverage and Scapegoat report paths under `Compile / crossTarget`

Append your project-specific values with `++=`:

```scala
import sbtsonar.SonarPlugin.autoImport.sonarProperties

sonarProperties ++= Map(
  "sonar.host.url" -> "https://sonarqube.example.com",
  "sonar.projectName" -> "My Scala Service",
  "sonar.projectKey" -> "my-scala-service",
  "sonar.sources" -> "src/main/scala",
  "sonar.tests" -> "src/test/scala",
  "sonar.junit.reportPaths" -> "target/test-reports"
)
```

Replace the generated properties completely with `:=`:

```scala
import sbtsonar.SonarPlugin.autoImport.sonarProperties

sonarProperties := Map(
  "sonar.host.url" -> "https://sonarqube.example.com",
  "sonar.projectName" -> "My Scala Service",
  "sonar.projectKey" -> "my-scala-service",
  "sonar.projectVersion" -> version.value,
  "sonar.projectBaseDir" -> baseDirectory.value.getAbsolutePath,
  "sonar.sources" -> "src/main/scala",
  "sonar.tests" -> "src/test/scala",
  "sonar.sourceEncoding" -> "UTF-8",
  "sonar.scala.version" -> scalaVersion.value
)
```

### Coverage and Scapegoat report properties

By default, `sonarExpectSonarQubeCommunityPlugin := true`, which keeps the
historic community Sonar Scala property names:

```scala
sonar.scala.scoverage.reportPath
sonar.scala.scapegoat.reportPath
```

If your SonarQube server expects the vendor SonarScala property names, set:

```scala
import sbtsonar.SonarPlugin.autoImport.sonarExpectSonarQubeCommunityPlugin

sonarExpectSonarQubeCommunityPlugin := false
```

Then sbt-sonar generates:

```scala
sonar.scala.coverage.reportPaths
sonar.scala.scapegoat.reportPaths
```

## External `sonar-project.properties`

If you prefer a Sonar Scanner properties file, create
`sonar-project.properties` in the project root:

```properties
sonar.host.url=https://sonarqube.example.com
sonar.projectKey=my-scala-service
sonar.projectName=My Scala Service
sonar.projectVersion=0.1.0
sonar.sources=src/main/scala
sonar.tests=src/test/scala
sonar.sourceEncoding=UTF-8
```

Enable external config in `build.sbt`:

```scala
import sbtsonar.SonarPlugin.autoImport.sonarUseExternalConfig

sonarUseExternalConfig := true
```

When `sonarScan` runs with the embedded scanner, sbt-sonar reads the file and
passes the current sbt `version` as `sonar.projectVersion` to the scanner. In
standalone scanner mode, sbt-sonar updates `sonar.projectVersion` in the file
before invoking `sonar-scanner`.

## Multi-module builds

For an aggregated build, define module properties on the root project and run
`sonarScan` only once from that root project:

```scala
import sbtsonar.SonarPlugin.autoImport.sonarProperties
import sbtsonar.SonarPlugin.autoImport.sonarScan

lazy val commonSettings = Seq(
  scalaVersion := "2.12.20",
  version := "0.1.0"
)

lazy val module1 = (project in file("module1"))
  .settings(commonSettings)
  .settings(name := "module1")

lazy val module2 = (project in file("module2"))
  .settings(commonSettings)
  .settings(name := "module2")

lazy val root = (project in file("."))
  .aggregate(module1, module2)
  .settings(commonSettings)
  .settings(
    name := "multi-module",
    sonarScan / aggregate := false,
    sonarProperties ++= Map(
      "sonar.host.url" -> "https://sonarqube.example.com",
      "sonar.projectName" -> "Multi Module",
      "sonar.projectKey" -> "multi-module",
      "sonar.modules" -> "module1,module2",
      "module1.sonar.projectName" -> "Module 1",
      "module1.sonar.projectBaseDir" -> "module1",
      "module2.sonar.projectName" -> "Module 2",
      "module2.sonar.projectBaseDir" -> "module2"
    )
  )
```

`sonarScan / aggregate := false` prevents sbt from running `sonarScan` again in
each aggregated subproject.

## Standalone scanner mode

The default scanner mode is embedded:

```scala
sonarUseSonarScannerCli := false
```

Use standalone mode only when you intentionally want sbt-sonar to call an
installed `sonar-scanner` executable:

```scala
import sbtsonar.SonarPlugin.autoImport.sonarUseSonarScannerCli

sonarUseSonarScannerCli := true
```

Standalone mode requires either:

- `SONAR_SCANNER_HOME` in the environment, or
- `-DsonarScanner.home=/path/to/sonar-scanner`

The executable must exist at `bin/sonar-scanner` on Unix-like systems or
`bin/sonar-scanner.bat` on Windows under that directory.

Example:

```bash
SONAR_SCANNER_HOME=/opt/sonar-scanner \
  sbt -Dsonar.host.url=https://sonarqube.example.com sonarScan
```

## sbt-release integration

When using [sbt-release](https://github.com/sbt/sbt-release), run `sonarScan`
after tests, coverage, and other report-generating tasks:

```scala
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.ReleasePlugin.autoImport.releaseProcess
import sbtrelease.ReleasePlugin.autoImport.releaseStepCommand
import sbtrelease.ReleasePlugin.autoImport.releaseStepTask
import sbtsonar.SonarPlugin.autoImport.sonarScan

releaseProcess := Seq[ReleaseStep](
  releaseStepCommand("coverageOn"),
  releaseStepTask(Test / test),
  releaseStepCommand("coverageOff"),
  releaseStepTask(coverageReport),
  releaseStepTask(scapegoat),
  releaseStepTask(sonarScan)
)
```

Adapt the release steps to the coverage and static-analysis plugins used by
your build.

## Examples

The repository contains scripted example builds under
[`src/sbt-test/sbt-sonar`](src/sbt-test/sbt-sonar):

- [`sbt-1.0`](src/sbt-test/sbt-sonar/sbt-1.0) - minimal sbt 1.x project.
- [`sbt-2.0`](src/sbt-test/sbt-sonar/sbt-2.0) - minimal sbt 2.x project.
- [`external-config`](src/sbt-test/sbt-sonar/external-config) - external
  `sonar-project.properties` with sbt 1.x.
- [`external-config-sbt-2.0`](src/sbt-test/sbt-sonar/external-config-sbt-2.0) -
  external `sonar-project.properties` with sbt 2.x.
- [`multi-module`](src/sbt-test/sbt-sonar/multi-module) and
  [`multi-module-sbt-2.0`](src/sbt-test/sbt-sonar/multi-module-sbt-2.0) -
  aggregated multi-module builds.
- [`multi-module-not-on-root-with-correct-config`](src/sbt-test/sbt-sonar/multi-module-not-on-root-with-correct-config)
  and
  [`multi-module-not-on-root-with-correct-config-sbt-2.0`](src/sbt-test/sbt-sonar/multi-module-not-on-root-with-correct-config-sbt-2.0)
  - module directories that need explicit `sonar.projectBaseDir` values.

## Notable changes

- `3.0.0` removes sbt 0.13 and Scala 2.11 support.
- `3.0.0` adds sbt 2.x support and publishes `sbt-sonar_sbt2_3`.
- The current published artifacts are
  `io.github.yarosman:sbt-sonar_2.12_1.0:3.0.0` for sbt 1.x and
  `io.github.yarosman:sbt-sonar_sbt2_3:3.0.0` for sbt 2.x. In sbt builds, use
  `addSbtPlugin("io.github.yarosman" % "sbt-sonar" % "3.0.0")`.
- The embedded scanner remains the default. Set `sonarUseSonarScannerCli :=
  true` only for standalone scanner mode.

## License

The project is licensed under the Apache License v2. See the [LICENSE](LICENSE)
file for more details.
