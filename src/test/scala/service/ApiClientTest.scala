package service

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import cats.implicits._
import config.ApiConfig
import model.api.NasaResponse
import model.{HttpError, ParsingError}
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.client.Client
import org.http4s.{Request, Response, Status, Uri}
import org.mockito.ArgumentMatchersSugar.any
import org.mockito.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import utils.TestData.{asteroidDetail, nasaResponse}


class ApiClientTest extends AnyFunSuite with Matchers with MockitoSugar with BeforeAndAfterEach {

  val mockConfig: ApiConfig = ApiConfig("MOCK_KEY", "https://api.mock.gov", "/list", "/detail/")

  private val baseUrl = mockConfig.baseUrl
  private val listPath = mockConfig.listPath
  val expectedUri: Uri =
    Uri.unsafeFromString(
      s"$baseUrl$listPath?start_date=2024-01-01&end_date=2024-01-31&api_key=${mockConfig.apiKey}"
    )

  private val client: Client[IO] = mock[Client[IO]]
  val apiClient = new ApiClientImpl[IO](client)

  test("getAsteroids returns NasaResponse on successful API call") {
    val mockResponse = Response[IO](status = Status.Ok).withEntity(nasaResponse)
    when(client.run(any[Request[IO]])).thenReturn(Resource.pure(mockResponse))
    val apiCall = apiClient.getAsteroids(expectedUri)

    apiCall.unsafeRunSync() shouldBe nasaResponse.asRight
  }

  test("getAsteroidDetail returns NasaResponse on successful API call") {
    val mockResponse = Response[IO](status = Status.Ok).withEntity(asteroidDetail)
    when(client.run(any[Request[IO]])).thenReturn(Resource.pure(mockResponse))
    val apiCall = apiClient.getAsteroidDetail(expectedUri)

    apiCall.unsafeRunSync() shouldBe asteroidDetail.asRight
  }

  test("api call correctly returns HttpError when there is server error response") {
    val mockResponse = Response[IO](status = Status.InternalServerError).withEntity("Internal Server Error")
    when(client.run(any[Request[IO]])).thenReturn(Resource.pure(mockResponse))

    val apiCall: IO[Either[model.Error, NasaResponse]] = apiClient.getAsteroids(expectedUri)

    apiCall.unsafeRunSync() should matchPattern {
      case Left(HttpError(message)) if message.contains("Received unsuccessful status: 500") =>
    }
  }

  test("api call correctly returns ParsingError when the response is not parsable") {
    val invalidJsonResponse = Response[IO](status = Status.Ok).withEntity("{invalid json}")
    when(client.run(any[Request[IO]])).thenReturn(Resource.pure(invalidJsonResponse))

    val apiCall = apiClient.getAsteroids(expectedUri)

    apiCall.unsafeRunSync() should matchPattern {
      case Left(ParsingError(_)) =>
    }
  }
}
