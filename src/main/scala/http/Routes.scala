package http

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.syntax.all._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import model.Error
import model.nasa._
import service.AsteroidService
import utils.DateUtils._

class Routes[F[_] : Concurrent](asteroidService: AsteroidService[F]) extends Http4sDsl[F] {

  implicit def listAsteroidDecoder: EntityDecoder[F, List[Asteroid]] = jsonOf[F, List[Asteroid]]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "asteroids" =>
      handleResponse(asteroidService.fetchAsteroids())

    case GET -> Root / "asteroids" / startDate / endDate =>
      validateDates(startDate, endDate) match {
        case Valid((start, end)) =>
          handleResponse(asteroidService.fetchAsteroidsWithDates(start, end))
        case Invalid(errors) =>
          BadRequest(errors.toList.mkString(", "))
      }

    case GET -> Root / "asteroids" / id if id.nonEmpty =>
      handleResponse(asteroidService.fetchAsteroidDetail(id))

    case req @ POST -> Root / "sortAsteroids" =>
      for {
        asteroids <- req.as[List[Asteroid]]
        sortByParam = extractSortBy(req)
        response <- handleSorting(sortByParam, asteroids)
      } yield response

    case req =>
      NotFound(s"Resource not found for path: ${req.uri}")
  }

  private def handleResponse[A](result: F[Either[Error, A]])(implicit encoder: io.circe.Encoder[A]): F[Response[F]] =
    result.flatMap {
      case Right(data) => Ok(data.asJson)
      case Left(error) => InternalServerError(error.toString)
    }

  private def extractSortBy(req: Request[F]): Option[String] = {
    req.uri.query.params.get("sortBy")
  }

  private def handleSorting(sortBy: Option[String], asteroids: List[Asteroid]): F[Response[F]] = {
    sortBy match {
      case Some(sortByParam) =>
        asteroidService.sortAsteroids(asteroids, sortByParam).flatMap {
          case Right(sortedAsteroids) => Ok(sortedAsteroids.asJson)
          case Left(error) => InternalServerError(error.toString)
        }
      case None => BadRequest("The 'sortBy' parameter must be provided")
    }
  }
}
