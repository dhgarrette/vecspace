import sbt._
import Keys._

object VecspaceBuild extends Build {

  lazy val main = Project(id = "vecspace", base = file(".")) dependsOn(dependent)

  lazy val dependent = Project(id = "Scalabha", base = file("scalabha"))

}

