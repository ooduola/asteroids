package scala

import cats.effect._
import com.comcast.ip4s._
import com.github.benmanes.caffeine.cache.{Cache, Caffeine}
import config.{AppConfig, ConfigLoader, ServerConfig}
import db.{DatabaseConfig, DatabaseInitialiser}
import http._
import model.api._
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import service._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
import repository.{FavoriteRepository, FavoriteRepositoryImpl}

import scala.concurrent.ExecutionContext.Implicits.global


object Main extends IOApp {

  private object HttpClient {
    def clientResource[F[_]: Async]: Resource[F, Client[F]] =
      EmberClientBuilder.default[F].build
  }

  override def run(args: List[String]): IO[ExitCode] = {
    implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

    HttpClient.clientResource[IO].use { httpClient =>
      implicit val client: Client[IO] = httpClient

      implicit val cache: Cache[(Option[String], Option[String]), NasaResponse] =
        Caffeine.newBuilder()
          .maximumSize(100)
          .build[(Option[String], Option[String]), NasaResponse]()

      loadConfig.flatMap { config =>
        DatabaseConfig.transactor[IO](config.db).use { xa =>
          DatabaseInitialiser.setupSchema(xa).flatMap { _ =>
            val favoriteRepository: FavoriteRepository[IO] = new FavoriteRepositoryImpl[IO](xa)
            val favoriteService = new FavoriteServiceImpl[IO](favoriteRepository)
            val nasaClient = new ApiClientImpl[IO]
            val asteroidService = new AsteroidServiceImpl[IO](nasaClient, config.api)
            startServer(asteroidService, favoriteService, config.server)
          }
        }
      }.handleErrorWith { e =>
        logger.error(e)("Server failed ") *> IO.pure(ExitCode.Error)
      }
    }
  }

  private def loadConfig(implicit logger: Logger[IO]): IO[AppConfig] = {
    IO(ConfigLoader.loadConfig).attempt.flatMap {
      case Right(cfg) => IO.pure(cfg)
      case Left(e) =>
        logger.error(e)("Failed to load configuration") *> IO.raiseError(e)
    }
  }

  private def startServer(asteroidService: AsteroidServiceImpl[IO],
                          favoriteService: FavoriteServiceImpl[IO],
                          config: ServerConfig)(implicit logger: Logger[IO]): IO[ExitCode] = {
    val asteroidRoutes = new AsteroidRoutes[IO](asteroidService).routes
    val favoriteRoutes = new FavoriteRoutes[IO](favoriteService).routes

    val routes = Router(
      "/asteroids" -> asteroidRoutes,
      "/favorites" -> favoriteRoutes
    ).orNotFound

    EmberServerBuilder
      .default[IO]
      .withHost(config.host)
      .withPort(config.port)
      .withHttpApp(routes)
      .build
      .use { server =>
        logger.info("Server started on http://0.0.0.0:8080") *>
          IO.never
      }
      .as(ExitCode.Success)
  }
}
