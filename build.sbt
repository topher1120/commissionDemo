name := """play-java-preview"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.0"

libraryDependencies ++= Seq(
  javaWs,
  "org.webjars" % "webjars-play_2.11" % "2.3.0-RC1-1",
  "org.webjars" % "bootstrap" % "3.1.1-1",
  "org.webjars" % "angular-ui-bootstrap" % "0.11.0-2"
)
