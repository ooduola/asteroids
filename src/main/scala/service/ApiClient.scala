package service

import cats.effect.{Concurrent, IO}
import cats.implicits._
import org.http4s.{Response, Status, Uri}
import org.http4s.circe.CirceEntityCodec._
import io.circe.{Decoder, DecodingFailure}
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import model._
import model.api._
import org.http4s.Method.GET

trait ApiClient[F[_]] {
  def getAsteroids(url: Uri): F[Either[Error, NasaResponse]]
  def getAsteroidDetail(url: Uri): F[Either[Error, AsteroidDetail]]
}

class ApiClientImpl[F[_]: Concurrent](client: Client[F]) extends ApiClient[F] with Http4sClientDsl[F] {

  override def getAsteroids(url: Uri): F[Either[Error, NasaResponse]] =
    client.run(GET(url)).use(handleResponse[NasaResponse])

  override def getAsteroidDetail(url: Uri): F[Either[Error, AsteroidDetail]] =
    client.run(GET(url)).use(handleResponse[AsteroidDetail])

  private def handleResponse[A](response: Response[F])(implicit decoder: Decoder[A]): F[Either[Error, A]] =
    response.status match {
      case status if status.isSuccess => handleSuccess(response)
      case status => handleFailure(response, status)
    }

  private def handleSuccess[A](response: Response[F])(implicit decoder: Decoder[A]): F[Either[Error, A]] =
    response.as[A].attempt.map {
      case Right(data) => Right(data)
      case Left(error) => Left(ParsingError(error.getMessage))
    }

  private def handleFailure[B](response: Response[F], status: Status): F[Either[Error, B]] =
    response.bodyText.compile.string.map { body =>
      val errorMessage = if (body.isEmpty) {
        s"Received unsuccessful status: ${status.code}"
      } else {
        s"Received unsuccessful status: ${status.code}. Response body: $body"
      }
      Left(HttpError(errorMessage))
    }
}
