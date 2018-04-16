lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.11"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.david",
      scalaVersion    := "2.12.4"
    )),
    name := "bank-api",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
      "org.typelevel"     %% "cats-core"            % "1.1.0",
      "org.mongodb.scala" %% "mongo-scala-driver"   % "2.2.1",
      "com.gu"            %% "scanamo"              % "1.0.0-M6",
      //"net.debasishg"     %% "redisclient"          % "3.5",
      //"org.mongodb.scala" %% "mongo-scala-driver"   % "2.2.1",
      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test
    )
  )
enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

packageName in Docker := name.value
version in Docker := version.value

dockerBaseImage := "openjdk:jre-alpine"
mainClass in Compile := Some("com.david.bank.QuickstartServer")

parallelExecution in Test := false