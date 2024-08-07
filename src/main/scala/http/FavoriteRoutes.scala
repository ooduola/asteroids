package http

import cats.effect.Concurrent
import cats.implicits._
import model.{DbError, Error, FavouriteAlreadyExistsError, FavouriteDbError}
import model.api.AsteroidSummary
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes, Response}
import org.typelevel.log4cats.Logger
import service.FavoriteService

class FavoriteRoutes[F[_]: Concurrent](favoriteService: FavoriteService[F])(implicit logger: Logger[F]) extends Http4sDsl[F] {

  implicit def asteroidSummaryDecoder: EntityDecoder[F, AsteroidSummary] = jsonOf[F, AsteroidSummary]

  private def errorResponse(error: DbError): F[Response[F]] = error match {
    case FavouriteAlreadyExistsError =>
      logger.warn(s"Failed to add favorite: ${error.message}") *>
        Conflict(error.message)
    case FavouriteDbError(cause) =>
      logger.error(cause)(s"Failed to add favorite: ${error.message}") *>
        InternalServerError(s"${error.message}: ${cause.getMessage}")
    case _ =>
      logger.error(s"Unexpected error: ${error.message}") *>
        InternalServerError("An unexpected error occurred")
  }

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "add" =>
      req.as[AsteroidSummary].flatMap { asteroid =>
        favoriteService.addFavorite(asteroid).flatMap {
          case Right(_) =>
            logger.info(s"Asteroid ${asteroid.id} added to favorites") *>
              Ok(s"Asteroid ${asteroid.id} added to favorites")
          case Left(error) => errorResponse(error)
        }
      }

    case GET -> Root / "list" =>
      favoriteService.fetchFavorites().flatMap {
        case Right(favorites) =>
          logger.info("Fetched favorites successfully") *>
            Ok(favorites)
        case Left(error: FavouriteDbError) =>
          logger.error(error)("Failed to fetch favorites") *>
            InternalServerError(s"Failed to fetch favorites: ${error.message}")
        case Left(other) =>
          logger.error(s"Failed to fetch favorites: ${other.message}") *>
            InternalServerError("An unexpected error occurred")
      }
  }
}
