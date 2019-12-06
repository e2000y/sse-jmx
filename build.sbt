organization := "com.tzavellas"
description := "JMX Library for Scala"
name := "sse-jmx"
licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("http://www.github.com/sptz45/" + name.value))
scmInfo := Some(
      ScmInfo(
        browseUrl = url("http://github.com/sptz45/" + name.value),
        connection = "https://github.com/sptz45/" + name.value +".git",
        devConnection = Some("https://github.com/sptz45/" + name.value +".git")
      )
    )
developers := List(
      Developer(
        id = "sptz45",
        name = "Spiros Tzavellas",
        email = "spiros at tzavellas dot com",
        url = url("http://www.tzavellas.com")
      )
    )

crossScalaVersions := Seq("2.11.8", "2.12.6", "2.13.1")

scalaVersion := "2.13.1"

scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:implicitConversions",
    "-unchecked",
    "-Wdead-code",
    "-Xlint:adapted-args",
    "-Xlint:infer-any"
  )

startYear := Some(2009)

libraryDependencies ++= Seq(
  "junit"             % "junit"           % "4.12" % "test",
  "com.novocode"      % "junit-interface" % "0.11" % "test"
)

compileOrder := CompileOrder.JavaThenScala
testOptions += Tests.Argument(TestFrameworks.JUnit, "-q")
parallelExecution in Test := false
