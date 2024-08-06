package http

import cats.data.Kleisli
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import model.HttpError
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._
import org.mockito.ArgumentMatchersSugar.argThat
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.MockitoSugar.{mock, reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import service.AsteroidService
import model.api._
import utils.TestData._
import utils._

class AsteroidRoutesTest extends AnyFunSuite with Matchers with BeforeAndAfterEach {

  val mockAsteroidService: AsteroidService[IO] = mock[AsteroidService[IO]]
  val routes: Kleisli[IO, Request[IO], Response[IO]] = new AsteroidRoutes[IO](mockAsteroidService).routes.orNotFound

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAsteroidService)
  }

  test("GET /asteroids returns status 200 and list of asteroids") {
    when(mockAsteroidService.fetchAsteroids())
      .thenReturn(IO.pure(Right(List(asteroidSummary))))

    val request = Request[IO](Method.GET, uri"/")
    val response = routes.run(request).unsafeRunSync
    val responseBody = response.as[List[AsteroidSummary]].unsafeRunSync()

    response.status shouldBe Status.Ok
    responseBody.length shouldBe 1
    responseBody.head.name shouldBe "Test Asteroid"
    verify(mockAsteroidService).fetchAsteroids()
  }

  test("GET with valid dates should status 200 and list of asteroids") {
    when(mockAsteroidService.fetchAsteroidsWithDates(
      argThat(new DateFormatMatcher("yyyy-MM-dd")),
      argThat(new DateFormatMatcher("yyyy-MM-dd"))
    )).thenReturn(IO.pure(Right(asteroidSummaryList)))

    val request = Request[IO](Method.GET, uri"/dates/2023-01-01/2023-01-02")
    val response = routes.run(request).unsafeRunSync
    val responseBody = response.as[List[AsteroidSummary]].unsafeRunSync()

    response.status shouldBe Status.Ok
    responseBody.length shouldBe asteroidList.length
    responseBody.head.name shouldBe "Test Asteroid"
    verify(mockAsteroidService).fetchAsteroidsWithDates("2023-01-01", "2023-01-02")
  }

  test("GET /:id with valid ID should return asteroid details") {
    when(mockAsteroidService.fetchAsteroidDetail("12345"))
      .thenReturn(IO.pure(Right(asteroidDetail)))

    val request = Request[IO](Method.GET, uri"/details/12345")
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.Ok
    println(response.body)

    val responseBody = response.as[AsteroidDetail].unsafeRunSync()
    responseBody.name shouldBe "Test Asteroid Detail"

    verify(mockAsteroidService).fetchAsteroidDetail("12345")
  }

  test("returns status 400 for incorrect date format") {
    val request = Request[IO](Method.GET, uri"/dates/01-01-2023/01-01-2023")
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.BadRequest
    verifyNoInteractions(mockAsteroidService)
  }

  test("returns status 404 for invalid routes") {
    val request = Request[IO](Method.GET, uri"/bad-path")
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.NotFound
    verifyNoInteractions(mockAsteroidService)
  }
}
