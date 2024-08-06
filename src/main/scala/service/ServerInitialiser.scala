package service

import cats.effect.{ExitCode, IO}
import config.ServerConfig
import http.{AsteroidRoutes, FavoriteRoutes}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

object ServerInitialiser {

  def startServer(asteroidService: AsteroidService[IO],
                          favoriteService: FavoriteService[IO],
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
      .use { _ =>
        logger.info(s"Server has successfully started") *>
          IO.never
      }
      .as(ExitCode.Success)
  }

}
