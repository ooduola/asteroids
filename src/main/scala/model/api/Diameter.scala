package model.api

import io.circe._
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class Diameter(
                     estimatedDiameterMin: Double,
                     estimatedDiameterMax: Double
                   )

object Diameter {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val diameterCodec: Codec.AsObject[Diameter] = deriveConfiguredCodec
}
