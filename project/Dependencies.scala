import sbt._
import Keys._

object Dependencies {

  val commonDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "2.2.4",
    "org.slf4j" % "slf4j-log4j12" % "1.7.10"
  )

  val sparkVersion = "2.0.2"


  val sparkDependencies  : Seq[ModuleID] = commonDependencies ++ Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion ,
    "org.apache.spark" %% "spark-sql" % sparkVersion ,
    "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0-M3"
  )

  val akkaDependencies  : Seq[ModuleID] = commonDependencies ++ Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.12",
    "com.typesafe.akka" %% "akka-http-core" % "2.4.11",
    "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
    "com.typesafe.akka" %% "akka-http-jackson-experimental" % "2.4.11",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.11",
    "com.typesafe.akka" %% "akka-http-testkit" % "2.4.11",
    "com.typesafe.akka" %% "akka-http-xml-experimental" % "2.4.11"
  )



}
