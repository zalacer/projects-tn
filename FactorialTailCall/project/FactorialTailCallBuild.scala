import sbt._
import sbt.Keys._

object FactorialTailCallBuild extends Build {

  lazy val FactorialTailCall = Project(
    id = "FactorialTailCall",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "FactorialTailCall",
      organization := "tn",
      version := "1.0",
      scalaVersion := "2.10.4",
      fork := true
    )
  )
}

