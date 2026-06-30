<h1 align="left">sbt-sonar</h1>

[![release-badge]][release] [![maven-badge]][maven]

[release]: https://github.com/yarosman/sbt-sonar/actions/workflows/release.yml
[release-badge]:
  https://img.shields.io/github/actions/workflow/status/yarosman/sbt-sonar/release.yml?branch=master
[maven]: https://search.maven.org/artifact/io.github.yarosman/sbt-sonar
[maven-badge]: https://img.shields.io/maven-central/v/io.github.yarosman/sbt-sonar.svg

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

## License

The project is licensed under the Apache License v2. See the [LICENSE](LICENSE)
file for more details.
