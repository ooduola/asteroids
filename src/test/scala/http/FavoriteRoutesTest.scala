package http

import cats.data.Kleisli
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import model.api._
import model.{FavouriteAlreadyExistsError, FavouriteDbError}
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.implicits._
import org.mockito.Mockito.{reset, verify, verifyNoInteractions, when}
import org.mockito.MockitoSugar.mock
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import service.FavoriteService
import utils.TestData._

import java.sql.SQLException

class FavoriteRoutesTest extends AnyFunSuite with Matchers with BeforeAndAfterEach {

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]
  val mockFavoriteService: FavoriteService[IO] = mock[FavoriteService[IO]]
  val routes: Kleisli[IO, Request[IO], Response[IO]] = new FavoriteRoutes[IO](mockFavoriteService).routes.orNotFound

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockFavoriteService)
  }

  test("POST /add returns status 200 when adding a favorite succeeds") {

    when(mockFavoriteService.addFavorite(asteroidSummary))
      .thenReturn(IO.pure(Right(())))

    val request = Request[IO](Method.POST, uri"/add").withEntity(asteroidSummary)
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.Ok
    response.as[String].unsafeRunSync() shouldBe s"Asteroid ${asteroidSummary.id} added to favorites"
    verify(mockFavoriteService).addFavorite(asteroidSummary)
  }

  test("POST /add returns status 409 when adding a favorite fails due to uniqueness constraint") {

    when(mockFavoriteService.addFavorite(asteroidSummary))
      .thenReturn(IO.pure(Left(FavouriteAlreadyExistsError)))

    val request = Request[IO](Method.POST, uri"/add").withEntity(asteroidSummary)
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.Conflict
    response.as[String].unsafeRunSync() shouldBe s"${FavouriteAlreadyExistsError.message}"
    verify(mockFavoriteService).addFavorite(asteroidSummary)
  }

  test("POST /add returns status 500 when adding a favorite fails due to a database error") {
    val dbError = FavouriteDbError(new SQLException("Database error"))

    when(mockFavoriteService.addFavorite(asteroidSummary))
      .thenReturn(IO.pure(Left(dbError)))

    val request = Request[IO](Method.POST, uri"/add").withEntity(asteroidSummary)
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.InternalServerError
    response.as[String].unsafeRunSync() shouldBe s"${dbError.message}: Database error"
    verify(mockFavoriteService).addFavorite(asteroidSummary)
  }

  test("GET /list returns status 200 and list of favorites") {
    when(mockFavoriteService.fetchFavorites())
      .thenReturn(IO.pure(Right(asteroidSummaryList)))

    val request = Request[IO](Method.GET, uri"/list")
    val response = routes.run(request).unsafeRunSync
    val responseBody = response.as[List[AsteroidSummary]].unsafeRunSync()

    response.status shouldBe Status.Ok
    responseBody shouldBe asteroidSummaryList
    verify(mockFavoriteService).fetchFavorites()
  }

  test("GET /list returns status 500 when fetching favorites fails due to a database error") {
    val dbError = FavouriteDbError(new SQLException("Database error"))

    when(mockFavoriteService.fetchFavorites())
      .thenReturn(IO.pure(Left(dbError)))

    val request = Request[IO](Method.GET, uri"/list")
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.InternalServerError
    response.as[String].unsafeRunSync() shouldBe s"Failed to fetch favorites: ${dbError.message}"
    verify(mockFavoriteService).fetchFavorites()
  }

  test("returns status 404 for invalid routes") {
    val request = Request[IO](Method.GET, uri"/bad-path")
    val response = routes.run(request).unsafeRunSync

    response.status shouldBe Status.NotFound
    verifyNoInteractions(mockFavoriteService)
  }
}
