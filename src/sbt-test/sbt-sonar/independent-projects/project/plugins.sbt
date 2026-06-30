val pluginVersion = sys.props.getOrElse(
  "plugin.version",
  throw new RuntimeException(
    """|The system property 'plugin.version' is not defined.
       |Specify this property using the scriptedLaunchOpts -D.""".stripMargin))

addSbtPlugin("org.johnnei.scapegoat" %% "sbt-scapegoat" % "1.3.12")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.4.4")
addSbtPlugin("io.github.yarosman" % "sbt-sonar" % pluginVersion)
