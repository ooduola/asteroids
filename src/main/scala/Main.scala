package scala

import cats.effect._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
import service.ServerInitialiser.startServer

object Main extends IOApp {

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] = {
    AppResources.build[IO].use {
      case (asteroidService, favouriteService, serverConfig) =>
        startServer(asteroidService, favouriteService, serverConfig).handleErrorWith { e =>
          logger.error(e)("Server failed") *> IO.pure(ExitCode.Error)
        }
    }.handleErrorWith { e =>
      logger.error(e)("Failed to initialise app resources") *> IO.pure(ExitCode.Error)
    }
  }
}
