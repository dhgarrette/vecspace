import AssemblyKeys._

import com.typesafe.sbt.SbtStartScript

name := "vecspace"

version := "0.0.1"

scalaVersion := "2.10.1"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Cloudera Hadoop Releases" at "https://repository.cloudera.com/content/repositories/releases",
  "dhg releases repo" at "http://www.cs.utexas.edu/~dhg/maven-repository/releases",
  "dhg snapshot repo" at "http://www.cs.utexas.edu/~dhg/maven-repository/snapshots"
)

libraryDependencies ++= Seq(
  "dhg" % "scala-util_2.10" % "1.0.0-SNAPSHOT" changing(),
  "dhg" % "nlp_2.10" % "1.0.0-SNAPSHOT" changing(),
  "com.nicta" %% "scoobi" % "0.7.2",
  "junit" % "junit" % "4.10" % "test",
  "com.novocode" % "junit-interface" % "0.8" % "test->default") //switch to ScalaTest at some point...

seq(assemblySettings: _*)

jarName in assembly := "vecpsace-assembly.jar"

test in assembly := {}

seq(SbtStartScript.startScriptForClassesSettings: _*)

SbtStartScript.stage in Compile := Unit

