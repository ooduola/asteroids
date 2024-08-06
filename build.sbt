ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.14"

val catsCoreVersion = "2.12.0"
val catsEffectVersion = "3.5.0"
val http4sVersion = "0.23.27"
val circeVersion = "0.14.9"
val doobieVersion = "1.0.0-RC2"
val postgresVersion = "42.7.3"
val log4catsVersion = "2.7.0"
val logbackVersion = "1.5.6"
val pureconfigVersion = "0.17.7"
val typesafeConfigVersion = "1.4.3"
val caffeineVersion = "3.1.8"
val refinedVersion = "0.11.1"
val refinedCatsVersion = "0.11.2"
val enumeratumVersion = "1.7.4"
val scalatestVersion = "3.2.19"
val mockitoScalaVersion = "1.17.37"
val scalatestPlusMockitoVersion = "3.2.19.0"

lazy val root = (project in file("."))
  .settings(
    name := "asteroids_challenge",
    libraryDependencies ++= Seq(
      // Typelevel Libraries
      "org.typelevel" %% "cats-core" % catsCoreVersion,
      "org.typelevel" %% "cats-effect" % catsEffectVersion,

      // HTTP and JSON Libraries
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-core" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-generic-extras" % "0.14.4",
      "io.circe" %% "circe-parser" % circeVersion,

      // Database Libraries
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-specs2" % doobieVersion,
      "org.postgresql" % "postgresql" % postgresVersion,

      // Logging Libraries
      "org.typelevel" %% "log4cats-slf4j" % log4catsVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion,

      // Configuration Libraries
      "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,
      "com.github.pureconfig" %% "pureconfig-ip4s" % pureconfigVersion,
      "com.typesafe" % "config" % typesafeConfigVersion,

      // Caching Libraries
      "com.github.ben-manes.caffeine" % "caffeine" % caffeineVersion,

      // Miscellaneous Libraries
      "eu.timepit" %% "refined" % refinedVersion,
      "eu.timepit" %% "refined-cats" % refinedCatsVersion,
      "com.beachape" %% "enumeratum" % enumeratumVersion,

      // Testing Libraries
      "org.scalatest" %% "scalatest" % scalatestVersion % Test,
      "org.mockito" %% "mockito-scala" % mockitoScalaVersion % Test,
      "org.scalatestplus" %% "mockito-5-12" % scalatestPlusMockitoVersion % Test
    ),
    unmanagedResourceDirectories in Test += baseDirectory.value / "src" / "test" / "resources"
  )
