package http

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._
import org.mockito.ArgumentMatchersSugar.argThat
import org.mockito.Mockito
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.MockitoSugar.{mock, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import service.AsteroidService
import model.nasa._
import utils.TestData._
import utils._


class RoutesTest extends AnyFunSuite with Matchers with BeforeAndAfterEach {

  val mockAsteroidService: AsteroidService[IO] = mock[AsteroidService[IO]]
  val routes = new Routes[IO](mockAsteroidService).routes.orNotFound

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.clearInvocations(mockAsteroidService)
  }

  test("GET /asteroids returns status 200 and list of asteroids") {
    when(mockAsteroidService.fetchAsteroids())
      .thenReturn(IO.pure(Right(List(asteroid))))

    val request = Request[IO](Method.GET, uri"/asteroids")
    val response = routes.run(request).unsafeRunSync
    val responseBody = response.as[List[Asteroid]].unsafeRunSync()

    response.status shouldBe Status.Ok
    responseBody.length shouldBe 1
    responseBody.head.name shouldBe "Test Asteroid"
    verify(mockAsteroidService).fetchAsteroids()
  }

  test("GET /asteroids/startDate/endDate returns status 200 and list of asteroids with correct date format") {
    when(mockAsteroidService.fetchAsteroidsWithDates(
      argThat(new DateFormatMatcher("yyyy-MM-dd")),
      argThat(new DateFormatMatcher("yyyy-MM-dd"))
    )).thenReturn(IO.pure(Right(asteroidList)))

    val request = Request[IO](Method.GET, uri"/asteroids/2023-01-01/2023-01-02")
    val response = routes.run(request).unsafeRunSync
    val responseBody = response.as[List[Asteroid]].unsafeRunSync()

    response.status shouldBe Status.Ok
    responseBody.length shouldBe asteroidList.length
    responseBody.head.name shouldBe "Test Asteroid"
    verify(mockAsteroidService).fetchAsteroidsWithDates("2023-01-01", "2023-01-02")
  }

  test("returns status 200 and asteroid detail with correct asteroid reference id") {
    when(mockAsteroidService.fetchAsteroidDetail("12345"))
      .thenReturn(IO.pure(Right(
        asteroidDetail
      )))

    val request = Request[IO](Method.GET, uri"/asteroids/12345")
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.Ok

    val responseBody = response.as[AsteroidDetail].unsafeRunSync()
    responseBody.name shouldBe "Test Asteroid Detail"

    // Verify that the service method was called
    verify(mockAsteroidService).fetchAsteroidDetail("12345")
  }

  test("returns status 400 for incorrect date format") {
    val request = Request[IO](Method.GET, uri"/asteroids/01-01-2023/01-01-2023")
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.BadRequest
    verifyNoInteractions(mockAsteroidService)
  }

  test("returns status 404 for invalid routes") {
    val request = Request[IO](Method.GET, uri"/bad-endpoint")
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.NotFound
    verifyNoInteractions(mockAsteroidService)
  }
}
