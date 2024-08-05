package model.nasa

import io.circe._
import io.circe.generic.semiauto._

case class MissDistance(
                         astronomical: String,
                         lunar: String,
                         kilometers: String,
                         miles: String
                       )

object MissDistance {
  implicit val missDistanceCodec: Codec.AsObject[MissDistance] = deriveCodec
}
