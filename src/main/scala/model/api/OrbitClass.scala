package model.api

import io.circe._
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class OrbitClass(
                       orbitClassType: String,
                       orbitClassDescription: String,
                       orbitClassRange: String
                     )

object OrbitClass {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val orbitClassCodec: Codec.AsObject[OrbitClass] = deriveConfiguredCodec
}
