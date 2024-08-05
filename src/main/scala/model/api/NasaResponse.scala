package model.api

import io.circe._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import io.circe.generic.extras._
import cats.syntax.either._
import io.circe.syntax.EncoderOps

case class NasaResponse(
                         elementCount: Int,
                         nearEarthObjects: Map[LocalDate, List[Asteroid]]
                       )

object NasaResponse {
  import model.api.Asteroid._

  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  implicit val dateEncoder: Encoder[LocalDate] = Encoder.encodeString.contramap(_.format(dateFormatter))

  implicit val dateDecoder: Decoder[LocalDate] =
    Decoder.decodeString.emap { str =>
      Either.catchNonFatal(LocalDate.parse(str, dateFormatter)).leftMap(_.getMessage)
    }

  implicit val mapDecoder: Decoder[Map[LocalDate, List[Asteroid]]] = (c: HCursor) => c.keys match {
    case Some(keys) =>
      val result = keys.toList.foldLeft[Decoder.Result[Map[LocalDate, List[Asteroid]]]](Right(Map.empty)) {
        case (Right(acc), key) =>
          c.downField(key).as[List[Asteroid]].map { value =>
            acc + (LocalDate.parse(key, dateFormatter) -> value)
          }
        case (left@Left(_), _) => left
      }
      result
    case None => Right(Map.empty)
  }

  implicit val customConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val nasaResponseCodec: Codec.AsObject[NasaResponse] = new Codec.AsObject[NasaResponse] {
    def encodeObject(a: NasaResponse): JsonObject = JsonObject.fromMap(Map(
      "element_count" -> Json.fromInt(a.elementCount),
      "near_earth_objects" -> Json.fromFields(
        a.nearEarthObjects.map {
          case (date, asteroids) =>
            date.format(dateFormatter) -> Json.fromValues(asteroids.map(_.asJson))
        }
      )
    ))

    override def apply(c: HCursor): Decoder.Result[NasaResponse] = for {
      elementCount <- c.downField("element_count").as[Int]
      nearEarthObjects <- c.downField("near_earth_objects").as[Map[LocalDate, List[Asteroid]]]
    } yield NasaResponse(elementCount, nearEarthObjects)

  }
}