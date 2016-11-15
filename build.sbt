name := "bigdata"

version := "0.1"

scalaVersion := "2.11.8"



resolvers += Resolver.mavenLocal

lazy val temperaturesDao = project.in(file("temperatures-spark")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.sparkDependencies)

lazy val temperaturesController = project.in(file("temperatures-akka")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.akkaDependencies) dependsOn(temperaturesDao)

lazy val algorithms = project.in( file("algorithms")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.commonDependencies)

lazy val root = (project in file(".")).
    aggregate(algorithms , temperaturesDao, temperaturesController)
