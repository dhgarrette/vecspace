import AssemblyKeys._

import com.typesafe.sbt.SbtStartScript

name := "vecspace"

version := "0.0.1"

scalaVersion := "2.10.2"

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Cloudera Hadoop Releases" at "https://repository.cloudera.com/content/repositories/releases/",
  "Thrift location" at "http://people.apache.org/~rawson/repo/",
  "dhg releases repo" at "http://www.cs.utexas.edu/~dhg/maven-repository/releases",
  "dhg snapshot repo" at "http://www.cs.utexas.edu/~dhg/maven-repository/snapshots"
)

libraryDependencies ++= Seq(
  "dhg" % "scala-util_2.10" % "1.0.0-SNAPSHOT" changing(),
  "junit" % "junit" % "4.10" % "test",
  "com.novocode" % "junit-interface" % "0.8" % "test->default") //switch to ScalaTest at some point...

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

seq(SbtStartScript.startScriptForClassesSettings: _*)

SbtStartScript.stage in Compile := Unit

