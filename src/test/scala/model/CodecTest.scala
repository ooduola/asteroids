package model

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import utils.TestData.{asteroidDetail, nasaResponse}
import io.circe.parser._
import model.nasa.NasaResponse
import model.nasa.AsteroidDetail
import model.nasa.NasaResponse._


import scala.io.Source

class CodecTest extends AnyFunSuite with Matchers {

  test("Decode: Nasa response") {
    val response = readResourceAsString("/mocks/nasa-response.json")
    val result = decode[NasaResponse](response)

    result shouldBe Right(nasaResponse)
  }

  test("Decode: Asteroid detail response") {
    val response = readResourceAsString("/mocks/asteroid-detail-response.json")
    val result = decode[AsteroidDetail](response)

    result shouldBe Right(asteroidDetail)
  }

  private def readResourceAsString(path: String): String =
    Source.fromInputStream(getClass.getResourceAsStream(path)).mkString
}
