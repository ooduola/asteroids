package model.nasa

import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredCodec
import io.circe.generic.semiauto._


trait AsteroidBase {
  def links: DetailLink
  def id: String
  def neoReferenceId: String
  def name: String
  def nasaJplUrl: String
  def absoluteMagnitudeH: Double
  def estimatedDiameter: EstimatedDiameter
  def isPotentiallyHazardousAsteroid: Boolean
  def closeApproachData: List[CloseApproachData]
  def isSentryObject: Boolean
}

case class Asteroid(
                     links: DetailLink,
                     id: String,
                     neoReferenceId: String,
                     name: String,
                     nasaJplUrl: String,
                     absoluteMagnitudeH: Double,
                     estimatedDiameter: EstimatedDiameter,
                     isPotentiallyHazardousAsteroid: Boolean,
                     closeApproachData: List[CloseApproachData],
                     isSentryObject: Boolean
                   ) extends AsteroidBase


object Asteroid {
  implicit val asteroidEncoder: Encoder[Asteroid] = deriveEncoder
  implicit private def config: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val asteroidCodec: Codec.AsObject[Asteroid] = deriveConfiguredCodec
}

case class AsteroidDetail(
                           links: DetailLink,
                           id: String,
                           neoReferenceId: String,
                           name: String,
                           designation: String,
                           nasaJplUrl: String,
                           absoluteMagnitudeH: Double,
                           estimatedDiameter: EstimatedDiameter,
                           isPotentiallyHazardousAsteroid: Boolean,
                           closeApproachData: List[CloseApproachData],
                           orbitalData: OrbitalData,
                           isSentryObject: Boolean
                         ) extends AsteroidBase

object AsteroidDetail {
  implicit private def config: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val asteroidDetailCodec: Codec.AsObject[AsteroidDetail] = deriveConfiguredCodec
}

