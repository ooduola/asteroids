package scala

import cats.effect._
import com.comcast.ip4s._
import com.github.benmanes.caffeine.cache.{Cache, Caffeine}
import config.{AppConfig, ConfigLoader}
import http.Routes
import model.api._
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import service.{ApiClientImpl, AsteroidServiceImpl}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger


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
        val asteroidService = buildServices(config)
        startServer(asteroidService)
      }.recoverWith { case e =>
        logger.error(e)("Server failed") *> IO.pure(ExitCode.Error)
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

  private def buildServices(cfg: AppConfig)(implicit client: Client[IO],
                                            cache: Cache[(Option[String], Option[String]), NasaResponse]): AsteroidServiceImpl[IO] = {
    val nasaClient = new ApiClientImpl[IO](client)
    new AsteroidServiceImpl[IO](nasaClient, cfg.api, cache)
  }


  private def startServer(asteroidService: AsteroidServiceImpl[IO])(implicit logger: Logger[IO]): IO[ExitCode] = {
    val routes = new Routes[IO](asteroidService).routes

    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(routes.orNotFound)
      .build
      .use { server =>
        logger.info("Server started on http://0.0.0.0:8080") *>
          IO.never
      }
      .as(ExitCode.Success)
  }
}
