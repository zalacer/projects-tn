import sbt._
import sbt.Keys._

object BuildConf extends Build {

  val a =  "FactorialTailCall"

  lazy val BuildConf = Project(
    id = a,
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := a,
      organization := "tn",
      version := "1.0",
      scalaVersion := "2.10.4",
      fork := true
    )
  )
}

