import AssemblyKeys._

import com.typesafe.startscript.StartScriptPlugin

name := "vecspace"

version := "0.0.1"

scalaVersion := "2.9.2"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Cloudera Hadoop Releases" at "https://repository.cloudera.com/content/repositories/releases/",
  "Thrift location" at "http://people.apache.org/~rawson/repo/"
)

libraryDependencies ++= Seq(
  "commons-logging" % "commons-logging" % "1.1.1",
  "log4j" % "log4j" % "1.2.16",
  "org.scalatest" %% "scalatest" % "1.6.1" % "test",
  "junit" % "junit" % "4.10" % "test",
  "com.novocode" % "junit-interface" % "0.6" % "test->default") //switch to ScalaTest at some point...

// Scrunch Dependencies (in addition to Scrunch and Crunch jars in lib/)
libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-core" % "0.20.2-cdh3u1" excludeAll(
    ExclusionRule(organization = "com.sun.jdmk"),
    ExclusionRule(organization = "com.sun.jmx"),
    ExclusionRule(organization = "javax.jms")
  ),
  "com.google.guava" % "guava" % "r09",
  "org.apache.avro" % "avro-mapred" % "1.6.0",
  "org.codehaus.jackson" % "jackson-core-asl" % "1.8.3",
  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.3",
  "org.codehaus.jackson" % "jackson-smile" % "1.8.6",
  "org.slf4j" % "slf4j-log4j12" % "1.6.1",
  "org.apache.hbase" % "hbase" % "0.90.3-cdh3u1" excludeAll(
    ExclusionRule(organization = "org.apache.hadoop"),
    ExclusionRule(organization = "commons-logging"),
    ExclusionRule(organization = "com.google.guava"),
    ExclusionRule(organization = "log4j"),
    ExclusionRule(organization = "org.slf4j")
  )
)

seq(assemblySettings: _*)

jarName in assembly := "vecpsace-assembly.jar"

test in assembly := {}

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

