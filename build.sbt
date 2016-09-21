name := "addbrain"

version := "1.0"

scalaVersion := "2.10.6"
val PhantomVersion = "1.22.0"
val sparkVersion = "1.6.2"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.10"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4"
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "1.6.2"


resolvers += Resolver.mavenLocal
