name := "bigdata"

version := "0.1"

scalaVersion := "2.11.8"



resolvers += Resolver.mavenLocal

lazy val temperaturesDao = project.in(file("temperatures-spark")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.sparkDependenciesCassandra)

lazy val enronSpark = project.in(file("enron-spark")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.sparkDependenciesXML ++ Dependencies.config)

lazy val temperaturesController = project.in(file("temperatures-akka")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.akkaDependencies) dependsOn(temperaturesDao)

lazy val algorithms = project.in( file("algorithms")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.commonDependencies).
  settings(libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-RC1")

lazy val akkaExamples = project.in(file("akka-examples")).
    settings(Common.settings: _*).
    settings(libraryDependencies ++= Dependencies.akkaDependencies)

lazy val root = (project in file(".")).
    aggregate(algorithms , temperaturesDao, temperaturesController, akkaExamples, enronSpark)
