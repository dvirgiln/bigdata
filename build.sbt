name := "bigdata"

version := "0.1"

scalaVersion := "2.10.6"



resolvers += Resolver.mavenLocal

lazy val temperatures = project.in(file("temperatures-spark")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.sparkDependencies)

lazy val algorithms = project.in( file("algorithms")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.commonDependencies)

lazy val root = (project in file(".")).
    aggregate(algorithms , temperatures)
