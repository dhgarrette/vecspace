import sbt._
import Keys._

object VecspaceBuild extends Build {

  lazy val main = Project("vecspace", file(".")) dependsOn(tacc_hadoop)

  lazy val tacc_hadoop = Project("tacc-hadoop", file("tacc-hadoop")) dependsOn(scoobi)

  lazy val scoobi = Project("scoobi", file("tacc-hadoop/scoobi"))

}
