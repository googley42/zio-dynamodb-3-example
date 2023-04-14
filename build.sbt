ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val zioVersion = "2.0.12"

lazy val root = (project in file("."))
  .settings(
    name := "zio-dynamodb-3-example",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % zioVersion,
      "dev.zio" %% "zio-dynamodb" % "0.2.8+5-aa0fb167-SNAPSHOT"
    )
  )
