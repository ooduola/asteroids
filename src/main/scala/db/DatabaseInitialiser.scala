package db

import cats.effect._
import cats.implicits._
import org.typelevel.log4cats.Logger
import org.flywaydb.core.Flyway

object DatabaseInitialiser {

  def migrate[F[_]: Sync: Logger](dbUrl: String, dbUser: String, dbPassword: String): F[Unit] = {
    Sync[F].delay {
      Flyway
        .configure()
        .dataSource(dbUrl, dbUser, dbPassword)
        .baselineOnMigrate(true)
        .load()
    }.flatMap { flyway =>
      Sync[F].delay(flyway.migrate()).void
    }.flatMap { _ =>
      Logger[F].info("Database migration completed successfully.")
    }.handleErrorWith { e =>
      Logger[F].error(e)("Database migration failed.") *> Sync[F].raiseError(e)
    }
  }
}

