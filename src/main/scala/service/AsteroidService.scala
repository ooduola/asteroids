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

trait AsteroidService[F[_]] {
  def fetchAsteroidsWithDates(startDate: String, endDate: String): F[Either[Error, List[AsteroidSummary]]]

  def fetchAsteroids(): F[Either[Error, List[AsteroidSummary]]]

  def fetchAsteroidDetail(id: String): F[Either[Error, AsteroidDetail]]

  def sortAsteroids(asteroids: List[AsteroidSummary], sortBy: SortBy): F[Either[InvalidSortCriteriaError, List[AsteroidSummary]]]
}

class AsteroidServiceImpl[F[_] : Concurrent](client: ApiClientImpl[F],
                                             config: ApiConfig,
                                             cache: Cache[(Option[String], Option[String]), NasaResponse]) extends AsteroidService[F] {
  private val baseUrl = config.baseUrl

  override def fetchAsteroidsWithDates(startDate: String, endDate: String): F[Either[Error, List[AsteroidSummary]]] =
    fetchFromCacheOrApi(startDate.some, endDate.some)

  override def fetchAsteroids(): F[Either[Error, List[AsteroidSummary]]] =
    fetchFromCacheOrApi(None, None)

  override def fetchAsteroidDetail(id: String): F[Either[Error, AsteroidDetail]] = {
    val url = Uri.unsafeFromString(s"$baseUrl${config.detailPath}$id?api_key=${config.apiKey}")
    client.getAsteroidDetail(url) // handle error
  }

  override def sortAsteroids(asteroids: List[AsteroidSummary], sortBy: SortBy): F[Either[InvalidSortCriteriaError, List[AsteroidSummary]]] = {
    val sortedAsteroids = sortBy match {
      case Name => Right(asteroids.sortBy(_.name))
      case other => Left(InvalidSortCriteriaError(s"Sorting criteria $other not supported"))
    }
    sortedAsteroids.pure[F]
  }

  private def fetchFromCacheOrApi(startDateOpt: Option[String], endDateOpt: Option[String]): F[Either[Error, List[AsteroidSummary]]] = {
    val key = (startDateOpt, endDateOpt)

    Option(cache.getIfPresent(key)) match {
      case Some(nasaResponse) =>
        val asteroidSummaryList = transformResponse(nasaResponse)
        Concurrent[F].pure(Right(asteroidSummaryList))
      case _ =>
        fetchAsteroidsFromApi(startDateOpt, endDateOpt).flatMap {
          case Right(nasaResponse) =>
            cache.put(key, nasaResponse)
            val asteroidSummaryList = transformResponse(nasaResponse)
            Concurrent[F].pure(Right(asteroidSummaryList))
          case Left(error) => Concurrent[F].pure(Left(error))
        }
    }
  }

  private def constructAsteroidsUrl(startDateOpt: Option[String], endDateOpt: Option[String]): Uri = {
    val urlStr = startDateOpt.zip(endDateOpt) match {
      case Some((startDate, endDate)) =>
        s"$baseUrl${config.listPath}?start_date=$startDate&end_date=$endDate&api_key=${config.apiKey}"
      case None =>
        s"$baseUrl${config.listPath}?api_key=${config.apiKey}"
    }
    Uri.unsafeFromString(s"$urlStr")
  }

  private def fetchAsteroidsFromApi(startDateOpt: Option[String], endDateOpt: Option[String]): F[Either[Error, NasaResponse]] = {
    val url = constructAsteroidsUrl(startDateOpt, endDateOpt)
    client.getAsteroids(url).map {
      case Right(resp) => Right(resp)
      case Left(error) => Left(error)
    }
  }
}
