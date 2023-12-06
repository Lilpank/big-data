ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "zookeper-animal-client"
  )
libraryDependencies += "org.apache.zookeeper" % "zookeeper" % "3.8.3"
libraryDependencies += "org.apache.curator" % "curator-framework" % "4.2.0"
