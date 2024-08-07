package service

import cats.effect.Concurrent
import cats.implicits._
import com.github.benmanes.caffeine.cache.{Cache, Caffeine}
import config.{ApiConfig, AppConfig}
import model.SortBy.Name
import model._
import model.api._
import org.http4s.Uri
import service.Transformer.transformResponse

import java.time.LocalDate

trait AsteroidService[F[_]] {
  def fetchAsteroidsWithDates(startDate: LocalDate, endDate: LocalDate): F[Either[Error, List[AsteroidSummary]]]

  def fetchAsteroidDetail(id: String): F[Either[Error, AsteroidDetail]]

  def sortAsteroids(asteroids: List[AsteroidSummary], sortBy: SortBy): F[Either[InvalidSortCriteriaError, List[AsteroidSummary]]]
}


class AsteroidServiceImpl[F[_] : Concurrent](client: ApiClientImpl[F],
                                             config: ApiConfig,
                                             asteroidCache: Cache[String, AsteroidDetail],
                                             summaryCache: Cache[(Option[LocalDate], Option[LocalDate]), List[AsteroidSummary]]
                                            ) extends AsteroidService[F] {
  private val baseUrl = config.baseUrl


  override def fetchAsteroidsWithDates(startDate: LocalDate, endDate: LocalDate): F[Either[Error, List[AsteroidSummary]]] = {
    val key = (startDate.some, endDate.some)

    Option(summaryCache.getIfPresent(key)) match {
      case Some(listAsteroids) => Concurrent[F].pure(Right(listAsteroids))
      case None =>
        val url = constructAsteroidsUrl(startDate.some, endDate.some)
        client.getAsteroids(url).flatMap {
          case Right(nasaResponse) =>
            val asteroidSummaryList = transformResponse(nasaResponse)
            summaryCache.put(key, asteroidSummaryList)
            Concurrent[F].pure(Right(asteroidSummaryList))
          case Left(error) => Concurrent[F].pure(Left(error))
        }
    }
  }

  override def fetchAsteroidDetail(id: String): F[Either[Error, AsteroidDetail]] = {
      Option(asteroidCache.getIfPresent(id)) match {
        case Some(asteroid) => Concurrent[F].pure(Right(asteroid))
        case None =>
          val url = Uri.unsafeFromString(s"$baseUrl${config.detailPath}$id?api_key=${config.apiKey}")
          client.getAsteroidDetail(url).flatMap {
            case Right(detail) =>
              asteroidCache.put(id, detail)
              Concurrent[F].pure(Right(detail))
            case Left(error) => Concurrent[F].pure(Left(error))
          }
      }
  }


  override def sortAsteroids(asteroids: List[AsteroidSummary], sortBy: SortBy): F[Either[InvalidSortCriteriaError, List[AsteroidSummary]]] = {
    val sortedAsteroids = sortBy match {
      case Name => Right(asteroids.sortBy(_.name))
      case other => Left(InvalidSortCriteriaError(s"Sorting criteria $other not supported"))
    }
    sortedAsteroids.pure[F]
  }

  private def constructAsteroidsUrl(startDateOpt: Option[LocalDate], endDateOpt: Option[LocalDate]): Uri = {
    val queryParams = List(
      startDateOpt.map(sd => s"start_date=$sd"),
      endDateOpt.map(ed => s"end_date=$ed"),
      Some(s"api_key=${config.apiKey}")
    ).flatten.mkString("&")

    val urlStr = s"$baseUrl${config.listPath}?$queryParams"
    Uri.unsafeFromString(urlStr)
  }

}
