package service

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.github.benmanes.caffeine.cache.Cache
import config._
import model.SortBy.Name
import model._
import model.api.{AsteroidDetail, AsteroidSummary, NasaResponse}
import org.http4s.Uri
import org.mockito.ArgumentMatchersSugar.any
import org.mockito.{ArgumentMatchers, MockitoSugar}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import utils.TestData._

import java.time.LocalDate

class AsteroidServiceTest extends AnyFunSuite with Matchers with MockitoSugar with BeforeAndAfterEach {

  private val mockClient: ApiClientImpl[IO] = mock[ApiClientImpl[IO]]
  private val mockConfig: ApiConfig = ApiConfig("MOCK_KEY", "https://.mock.gov", "/list", "/detail/")
  private val mockAsteroidCache = mock[Cache[(Option[LocalDate], Option[LocalDate]), List[AsteroidSummary]]]
  private val mockSummaryCache = mock[Cache[String, AsteroidDetail]]
  private val startDate = LocalDate.parse("2023-01-01")
  private val endDate = LocalDate.parse("2023-01-02")

  val asteroidService = new AsteroidServiceImpl[IO](mockClient, mockConfig, mockSummaryCache, mockAsteroidCache)

  private val baseUrl = mockConfig.baseUrl
  private val listPath = mockConfig.listPath
  private val detailPath = mockConfig.detailPath


  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockClient, mockAsteroidCache, mockSummaryCache)
  }

  test("fetchAsteroidsWithDates returns expected result") {
    when(mockClient.getAsteroids(any[Uri])).thenReturn(IO.pure(Right(nasaResponse)))

    val result = asteroidService.fetchAsteroidsWithDates(startDate, endDate).unsafeRunSync()
    val expectedUri: Uri =
      Uri.unsafeFromString(
        s"$baseUrl$listPath?start_date=2023-01-01&end_date=2023-01-02&api_key=${mockConfig.apiKey}"
      )

    result shouldBe Right(asteroidSummaryList)
    verify(mockClient).getAsteroids(ArgumentMatchers.eq(expectedUri))
}

  test("fetchAsteroidDetail returns expected result") {
    when(mockClient.getAsteroidDetail(any[Uri])).thenReturn(IO.pure(Right(asteroidDetail)))

    val asteroidId = "123abc"
    val result = asteroidService.fetchAsteroidDetail(asteroidId).unsafeRunSync()
    val expectedUri: Uri = Uri.unsafeFromString(s"$baseUrl$detailPath$asteroidId?api_key=${mockConfig.apiKey}")

    result shouldBe Right(asteroidDetail)
    verify(mockClient).getAsteroidDetail(ArgumentMatchers.eq(expectedUri))
  }

  test("sortAsteroids sorts by name") {
    val result = asteroidService.sortAsteroids(asteroidSummaryList, Name).unsafeRunSync()

    result shouldBe Right(asteroidSummaryList.sortBy(_.name))
  }

  test("sortAsteroids returns InvalidSortCriteriaError error for invalid sort criteria") {
    val invalidSortCriteria = "id"

    val result = SortBy.fromString("id") match {
      case Some(sortBy) => asteroidService.sortAsteroids(asteroidSummaryList, sortBy).unsafeRunSync()
      case None => Left(InvalidSortCriteriaError(s"Sorting criteria $invalidSortCriteria not supported"))
    }

    result shouldBe Left(InvalidSortCriteriaError(s"Sorting criteria $invalidSortCriteria not supported"))
  }

  test("fetchAsteroidsWithDates handles error response") {
    when(mockClient.getAsteroids(any[Uri])).thenReturn(IO.pure(Left(HttpError("Error fetching data"))))

    val result = asteroidService.fetchAsteroidsWithDates(startDate, endDate).unsafeRunSync()

    result shouldBe Left(HttpError("Error fetching data"))
  }

  test("fetchAsteroidDetail handles error response") {
    when(mockClient.getAsteroidDetail(any[Uri])).thenReturn(IO.pure(Left(HttpError("Error fetching data"))))

    val asteroidId = "123abc"
    val result = asteroidService.fetchAsteroidDetail(asteroidId).unsafeRunSync()

    result shouldBe Left(HttpError("Error fetching data"))
  }

}


