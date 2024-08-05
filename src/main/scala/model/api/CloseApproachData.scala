package model.api

import io.circe._
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class CloseApproachData(
                              closeApproachDate: String,
                              closeApproachDateFull: String,
                              epochDateCloseApproach: Long,
                              relativeVelocity: RelativeVelocity,
                              missDistance: MissDistance,
                              orbitingBody: String
                            )

object CloseApproachData {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val closeApproachDataCodec: Codec.AsObject[CloseApproachData] = deriveConfiguredCodec
}