ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "asteroids_challenge",
    libraryDependencies ++= Seq(
      // Typelevel Libraries
      "org.typelevel" %% "cats-core" % "2.12.0",
      "org.typelevel" %% "cats-effect" % "3.5.0",

      // HTTP and JSON Libraries
      "org.http4s" %% "http4s-ember-server" % "0.23.27",
      "org.http4s" %% "http4s-ember-client" % "0.23.27",
      "org.http4s" %% "http4s-core" % "0.23.27",
      "org.http4s" %% "http4s-circe" % "0.23.27",
      "org.http4s" %% "http4s-dsl" % "0.23.27",
      "io.circe" %% "circe-core" % "0.14.9",
      "io.circe" %% "circe-generic" % "0.14.9",
      "io.circe" %% "circe-generic-extras" % "0.14.4",
      "io.circe" %% "circe-parser" % "0.14.9",

      // Database Libraries
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC2",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC2",

      // Logging Libraries
      "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
      "ch.qos.logback" % "logback-classic" % "1.5.6",

      // Configuration Libraries
      "com.github.pureconfig" %% "pureconfig" % "0.17.7",

      // Caching Libraries
      "com.github.ben-manes.caffeine" % "caffeine" % "3.1.8",

      // Miscellaneous Libraries
      "eu.timepit" %% "refined" % "0.11.1",
      "eu.timepit" %% "refined-cats" % "0.11.2",
      "com.beachape" %% "enumeratum" % "1.7.4",

      // Testing Libraries
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.mockito" %% "mockito-scala" % "1.17.37" % Test,
      "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test
    )
  )
