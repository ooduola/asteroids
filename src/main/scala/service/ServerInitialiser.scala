package service

import cats.effect.{ExitCode, IO}
import config.ServerConfig
import http.{AsteroidRoutes, FavouriteRoutes}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

object ServerInitialiser {

  def startServer(asteroidService: AsteroidService[IO],
                  favouriteService: FavouriteService[IO],
                  config: ServerConfig)(implicit logger: Logger[IO]): IO[ExitCode] = {

    val asteroidRoutes = new AsteroidRoutes[IO](asteroidService).routes
    val favouriteRoutes = new FavouriteRoutes[IO](favouriteService).routes

    val routes = Router(
      "/asteroids" -> asteroidRoutes,
      "/favourites" -> favouriteRoutes
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
