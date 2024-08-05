package model.api

import io.circe._
import io.circe.generic.extras.auto._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class OrbitalData(
                       orbitId: String,
                       orbitDeterminationDate: String,
                       firstObservationDate: String,
                       lastObservationDate: String,
                       dataArcInDays: Int,
                       observationsUsed: Int,
                       orbitUncertainty: String,
                       minimumOrbitIntersection: String,
                       jupiterTisserandInvariant: String,
                       epochOsculation: String,
                       eccentricity: String,
                       semiMajorAxis: String,
                       inclination: String,
                       ascendingNodeLongitude: String,
                       orbitalPeriod: String,
                       perihelionDistance: String,
                       perihelionArgument: String,
                       aphelionDistance: String,
                       perihelionTime: String,
                       meanAnomaly: String,
                       meanMotion: String,
                       equinox: String,
                       orbitClass: OrbitClass
                     )

object OrbitalData {
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val orbitalDataCodec: Codec.AsObject[OrbitalData] = deriveConfiguredCodec
}