val catsVersion = "1.1.0"
val catsAll = "org.typelevel" %% "cats-core" % catsVersion


lazy val root = (project in file(".")).
  settings(
    organization := "com.example",
    name := "cats-examples",
    scalaVersion := "2.12.3",
    libraryDependencies ++= Seq(
      catsAll,
      "org.typelevel" %% "cats-free" % catsVersion
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature", "-Ypartial-unification",
      "-language:_"
    )
  )

