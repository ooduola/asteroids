ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.14"


val catsCore = "2.12.0"
val catsEffect = "3.5.0"
val http4s = "0.23.27"
val circe = "0.14.9"
val doobie = "1.0.0-RC2"
val postgres = "42.7.3"
val flyway = "7.15.0"
val log4cats = "2.7.0"
val logback = "1.5.6"
val pureconfig = "0.17.7"
val typesafeConfig = "1.4.3"
val caffeine = "3.1.8"
val refined = "0.11.1"
val refinedCats = "0.11.2"
val enumeratum = "1.7.4"
val scalatest = "3.2.19"
val scalaMock = "6.0.0"
val mockitoScala = "1.17.37"
val scalatestPlusMockito = "3.2.19.0"


// Project settings
lazy val root = (project in file("."))
  .settings(
    name := "asteroids_challenge",

    // Library dependencies
    libraryDependencies ++= Seq(
      // Typelevel Libraries
      "org.typelevel" %% "cats-core" % catsCore,
      "org.typelevel" %% "cats-effect" % catsEffect,

      // HTTP and JSON Libraries
      "org.http4s" %% "http4s-ember-server" % http4s,
      "org.http4s" %% "http4s-ember-client" % http4s,
      "org.http4s" %% "http4s-core" % http4s,
      "org.http4s" %% "http4s-circe" % http4s,
      "org.http4s" %% "http4s-dsl" % http4s,
      "io.circe" %% "circe-core" % circe,
      "io.circe" %% "circe-generic" % circe,
      "io.circe" %% "circe-generic-extras" % "0.14.4",
      "io.circe" %% "circe-parser" % circe,

      // Database Libraries
      "org.tpolecat" %% "doobie-core" % doobie,
      "org.tpolecat" %% "doobie-hikari" % doobie,
      "org.tpolecat" %% "doobie-postgres" % doobie,
      "org.tpolecat" %% "doobie-specs2" % doobie,
      "org.postgresql" % "postgresql" % postgres,
      "org.flywaydb" % "flyway-core" % flyway,

      // Logging Libraries
      "org.typelevel" %% "log4cats-slf4j" % log4cats,
      "ch.qos.logback" % "logback-classic" % logback,

      // Configuration Libraries
      "com.github.pureconfig" %% "pureconfig" % pureconfig,
      "com.github.pureconfig" %% "pureconfig-ip4s" % pureconfig,
      "com.typesafe" % "config" % typesafeConfig,

      // Caching Libraries
      "com.github.ben-manes.caffeine" % "caffeine" % caffeine,

      // Miscellaneous Libraries
      "eu.timepit" %% "refined" % refined,
      "eu.timepit" %% "refined-cats" % refinedCats,
      "com.beachape" %% "enumeratum" % enumeratum,

      // Testing Libraries
      "org.scalatest" %% "scalatest" % scalatest % Test,
      "org.mockito" %% "mockito-scala" % mockitoScala % Test,
      "org.scalatestplus" %% "mockito-5-12" % scalatestPlusMockito % Test,
      "org.scalamock" %% "scalamock" % scalaMock % Test,
      "com.h2database" % "h2" % "2.3.230" % Test
    )
  )
