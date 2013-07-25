import AssemblyKeys._

assemblySettings

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

name := "vecspace"

version := "0.0.2"

scalaVersion := "2.9.2"

resolvers ++= Seq(
  "Sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "cloudera" at "https://repository.cloudera.com/content/repositories/releases",
  "conjars" at "http://conjars.org/repo"
)

scalacOptions ++= Seq("-deprecation", "-Ydependent-method-types", "-optimize", "-unchecked")

jarName in assembly := "tacc-hadoop-assembly.jar"

mainClass in assembly := None

test in assembly := {}

mainClass in oneJar := None
