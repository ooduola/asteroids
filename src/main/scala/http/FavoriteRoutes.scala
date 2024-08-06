package http

import cats.effect.Concurrent
import cats.implicits._
import model.api.AsteroidSummary
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}
import service.FavoriteService

class FavoriteRoutes[F[_]: Concurrent](favoriteService: FavoriteService[F]) extends Http4sDsl[F] {

  implicit def asteroidSummaryDecoder: EntityDecoder[F, AsteroidSummary] = jsonOf[F, AsteroidSummary]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "add" =>
      req.as[AsteroidSummary].flatMap { asteroid =>
        favoriteService.addFavorite(asteroid).attempt.flatMap {
          case Right(_) => Ok(s"Asteroid ${asteroid.id} added to favorites")
          case Left(error) => InternalServerError(s"Failed to add favorite: ${error.getMessage}")
        }
      }

    case GET -> Root / "list" =>
      favoriteService.fetchFavorites().attempt.flatMap {
        case Right(favorites) => Ok(favorites)
        case Left(error) => InternalServerError(s"Failed to fetch favorites: ${error.getMessage}")
      }
  }
}
