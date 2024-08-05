package model.nasa

import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class RelativeVelocity(
                             kilometersPerSecond: String,
                             kilometersPerHour: String,
                             milesPerHour: String
                           )

object RelativeVelocity {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val relativeVelocityCodec: Codec.AsObject[RelativeVelocity] = deriveConfiguredCodec
}
