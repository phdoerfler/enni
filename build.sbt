name := "Enni"

ThisBuild / organization := "io.doerfler"
ThisBuild / version := "1-SNAPSHOT"

enablePlugins(PackPlugin)
packMain := Map("enni" -> "io.doerfler.enni.EnniApp")

libraryDependencies += "org.parboiled" %% "parboiled" % "2.1.5"
libraryDependencies += "joda-time" % "joda-time" % "2.10.1"


libraryDependencies ++= specs2("4.3.4", Seq("core", "html", "scalacheck"))

def specs2(version: String, features: Seq[String]) =
  features.map(fe => "org.specs2" %% f"specs2-$fe%s" % version % "test")

scalacOptions in Test += "-Yrangepos" // specs2

scalacOptions += "-Ypartial-unification" // cats
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"



initialCommands in console := """import io.doerfler._"""
initialCommands in (Test, console) := (initialCommands in console).value

initialCommands in consoleQuick := """
import org.scalacheck._
import Gen._
import Arbitrary.arbitrary
"""