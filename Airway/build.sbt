
name := "Airway"

version := "0.1"

scalaVersion := "2.11.6"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++=
"com.typesafe.akka" %% "akka-actor" % "2.3.10" ::
"com.typesafe.akka" %% "akka-testkit" % "2.3.10" % "test" ::
"com.typesafe.akka" %% "akka-remote" % "2.3.10" ::
"com.typesafe.akka" %% "akka-agent" % "2.3.10" ::
"org.scalatest" %% "scalatest" % "3.0.0-SNAP4" % "test" ::
Nil

