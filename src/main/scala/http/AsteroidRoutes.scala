package http

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.syntax.all._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import model.{Error, SortBy}
import model.api._
import service.AsteroidService
import utils.DateUtils._

import java.time.LocalDate

class AsteroidRoutes[F[_]: Concurrent](asteroidService: AsteroidService[F]) extends Http4sDsl[F] {

  implicit def listAsteroidDecoder: EntityDecoder[F, List[Asteroid]] = jsonOf[F, List[Asteroid]]
  implicit def asteroidSummaryDecoder: EntityDecoder[F, List[AsteroidSummary]] = jsonOf[F, List[AsteroidSummary]]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "dates" =>
      val defaultStartDate = LocalDate.now()
      val defaultEndDate = LocalDate.now().plusDays(7)
      handleResponse(asteroidService.fetchAsteroidsWithDates(defaultStartDate, defaultEndDate))

    case GET -> Root / "dates" / startDate / endDate =>
      validateDates(startDate, endDate) match {
        case Valid((start, end)) =>
          handleResponse(asteroidService.fetchAsteroidsWithDates(start, end))
        case Invalid(errors) =>
          BadRequest(errors.toList.mkString(", "))
      }

    case GET -> Root / "details" / id if id.nonEmpty =>
      handleResponse(asteroidService.fetchAsteroidDetail(id))

    case req @ POST -> Root / "sort" =>
      for {
        asteroids <- req.as[List[AsteroidSummary]]
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

  private def extractSortBy(req: Request[F]): Option[SortBy] = {
    req.uri.query.params.get("sortBy").flatMap(SortBy.fromString)
  }

  private def handleSorting(sortBy: Option[SortBy], asteroids: List[AsteroidSummary]): F[Response[F]] = {
    sortBy match {
      case Some(sort) => asteroidService.sortAsteroids(asteroids, sort).flatMap {
        case Right(sortedAsteroids) => Ok(sortedAsteroids.asJson)
        case Left(error) => InternalServerError(error.toString)
      }
      case None => BadRequest(s"Invalid 'sortBy' parameter in URL path")
    }
  }
}
