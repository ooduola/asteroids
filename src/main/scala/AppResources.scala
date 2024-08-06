package scala

import cats.effect._
import com.github.benmanes.caffeine.cache.{Cache, Caffeine}
import config.{ConfigLoader, ServerConfig}
import db.{DatabaseConfig, DatabaseInitialiser}
import model.api._
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.Logger
import repository.FavoriteRepositoryImpl
import service._

import scala.concurrent.ExecutionContext.Implicits.global

object AppResources {

  def build[F[_]: Async](implicit logger: Logger[F]): Resource[F, (AsteroidService[F], FavoriteService[F], ServerConfig)] = {
    for {
      _ <- Resource.eval(logger.info("Starting application resource initialization"))
      client <- httpClientResource[F]
      cache <- cacheResource[F]
      config <- Resource.eval(ConfigLoader.loadConfig[F])
      _ <- Resource.eval(DatabaseInitialiser.migrate[F](config.db.url, config.db.user, config.db.password))
      transactor <- DatabaseConfig.transactor[F](config.db)
      favoriteRepository = new FavoriteRepositoryImpl[F](transactor)
      favoriteService = new FavoriteServiceImpl[F](favoriteRepository)
      nasaClient = new ApiClientImpl[F](client)
      asteroidService = new AsteroidServiceImpl[F](nasaClient, config.api, cache)

      _ <- Resource.eval(logger.info("Application resources initialized successfully"))
    } yield (asteroidService, favoriteService, config.server)
  }

  private def cacheResource[F[_]: Async]: Resource[F, Cache[(Option[String], Option[String]), NasaResponse]] = {
    Resource.pure {
      Caffeine.newBuilder()
        .maximumSize(100)
        .build[(Option[String], Option[String]), NasaResponse]()
    }
  }

  private def httpClientResource[F[_]: Async]: Resource[F, Client[F]] =
    EmberClientBuilder.default[F].build
}
