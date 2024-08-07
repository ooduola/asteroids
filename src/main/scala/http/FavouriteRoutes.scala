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
import service.FavouriteService

class FavouriteRoutes[F[_]: Concurrent](favouriteService: FavouriteService[F])(implicit logger: Logger[F]) extends Http4sDsl[F] {

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
        favouriteService.addFavourite(asteroid).flatMap {
          case Right(_) =>
            logger.info(s"Asteroid ${asteroid.id} added to favourites") *>
              Ok(s"Asteroid ${asteroid.id} added to favourites")
          case Left(error) => errorResponse(error)
        }
      }

    case GET -> Root / "list" =>
      favouriteService.fetchFavourites().flatMap {
        case Right(favourites) =>
          logger.info("Fetched favourites successfully") *>
            Ok(favourites)
        case Left(error: FavouriteDbError) =>
          logger.error(error)("Failed to fetch favourites") *>
            InternalServerError(s"Failed to fetch favourites: ${error.message}")
        case Left(other) =>
          logger.error(s"Failed to fetch favourites: ${other.message}") *>
            InternalServerError("An unexpected error occurred")
      }

    case req =>
      NotFound(s"Resource not found for path: ${req.uri}")
  }
}
