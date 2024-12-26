ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val zioVersion = "2.1.11"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "zio-dynamodb-3-example",
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-dynamodb" % "1.0.0-RC9",
      "dev.zio" %% "zio-dynamodb-json" % "1.0.0-RC9",
      "io.github.kitlangton" %% "neotype" % "0.3.8",
      "io.github.kitlangton" %% "neotype-zio-schema" % "0.3.8",
      "dev.zio" %% "zio-test" % zioVersion % "it,test",
      "dev.zio" %% "zio-test-sbt" % zioVersion % "it,test"
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )
