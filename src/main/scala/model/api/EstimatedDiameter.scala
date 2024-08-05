package model.api

import io.circe._
import io.circe.generic.semiauto._

case class EstimatedDiameter(
                              kilometers: Diameter,
                              meters: Diameter,
                              miles: Diameter,
                              feet: Diameter
                            )

object EstimatedDiameter {
  implicit val estimatedDiameterCodec: Codec.AsObject[EstimatedDiameter] = deriveCodec
}
