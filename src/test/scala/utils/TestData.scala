package utils

import model.api._

import java.time.LocalDate

object TestData {

  val asteroid: Asteroid = Asteroid(
    DetailLink("http://example.com/neo/1"),
    "1",
    "1",
    "Test Asteroid",
    "http://example.com",
    10.0,
    EstimatedDiameter(
      Diameter(1.0, 2.0),
      Diameter(1000, 2000),
      Diameter(0.6, 1.2),
      Diameter(3.3, 6.6)
    ),
    false,
    List.empty,
    false
  )

  val asteroidList: List[Asteroid] = List(
    Asteroid(
      DetailLink("http://example.com/neo/1"),
      "1",
      "1",
      "Test Asteroid",
      "http://example.com",
      10.0,
      EstimatedDiameter(
        Diameter(1.0, 2.0),
        Diameter(1000, 2000),
        Diameter(0.6, 1.2),
        Diameter(3.3, 6.6)
      ),
      false,
      List.empty,
      false
    ),
    Asteroid(
      DetailLink("http://example.com/neo/1"),
      "1",
      "1",
      "Test Asteroid 2",
      "http://example.com",
      10.0,
      EstimatedDiameter(
        Diameter(1.0, 2.0),
        Diameter(1000, 2000),
        Diameter(0.6, 1.2),
        Diameter(3.3, 6.6)
      ),
      false,
      List.empty,
      false
    ),
    Asteroid(
      DetailLink("http://example.com/neo/1"),
      "1",
      "1",
      "Test Asteroid 3",
      "http://example.com",
      10.0,
      EstimatedDiameter(
        Diameter(1.0, 2.0),
        Diameter(1000, 2000),
        Diameter(0.6, 1.2),
        Diameter(3.3, 6.6)
      ),
      false,
      List.empty,
      false
    )
  )

  val asteroidSummaryList: List[AsteroidSummary] = asteroidList.map { asteroid =>
    AsteroidSummary(
      id = asteroid.id,
      name = asteroid.name,
      links = asteroid.links
    )
  }


  val asteroidSummary: AsteroidSummary =
    AsteroidSummary(
      id = asteroid.id,
      name = asteroid.name,
      links = asteroid.links
    )




  val asteroidDetail: AsteroidDetail = AsteroidDetail(
    links = DetailLink("http://example.com/neo/1/detail"),
    id = "1",
    neoReferenceId = "1",
    name = "Test Asteroid Detail",
    designation = "2021 AB",
    nasaJplUrl = "http://example.com/neo/1",
    absoluteMagnitudeH = 10.0,
    estimatedDiameter = EstimatedDiameter(
      kilometers = Diameter(1.0, 2.0),
      meters = Diameter(1000, 2000),
      miles = Diameter(0.6, 1.2),
      feet = Diameter(3.3, 6.6)
    ),
    isPotentiallyHazardousAsteroid = false,
    closeApproachData = List(
      CloseApproachData(
        closeApproachDate = "2024-08-04",
        closeApproachDateFull = "2024-Aug-04 14:00 UT",
        epochDateCloseApproach = 1710480000000L,
        relativeVelocity = RelativeVelocity(
          kilometersPerSecond = "12.34",
          kilometersPerHour = "44500",
          milesPerHour = "27600"
        ),
        missDistance = MissDistance(
          astronomical = "0.012",
          lunar = "4.6",
          kilometers = "12000",
          miles = "7500"
        ),
        orbitingBody = "Earth"
      )
    ),
    orbitalData = OrbitalData(
      orbitId = "1",
      orbitDeterminationDate = "2024-07-15T00:00:00Z",
      firstObservationDate = "2024-01-01",
      lastObservationDate = "2024-08-03",
      dataArcInDays = 200,
      observationsUsed = 50,
      orbitUncertainty = "0.1",
      minimumOrbitIntersection = "0.01",
      jupiterTisserandInvariant = "3.5",
      epochOsculation = "2024-08-01",
      eccentricity = "0.1",
      semiMajorAxis = "1.2",
      inclination = "10.0",
      ascendingNodeLongitude = "100.0",
      orbitalPeriod = "365.25",
      perihelionDistance = "0.9",
      perihelionArgument = "0.5",
      aphelionDistance = "1.5",
      perihelionTime = "2024-07-20",
      meanAnomaly = "0.7",
      meanMotion = "0.1",
      equinox = "J2000",
      orbitClass = OrbitClass(
        orbitClassType = "Near-Earth",
        orbitClassDescription = "Near-Earth orbit class",
        orbitClassRange = "0.1 - 1.0 AU"
      )
    ),
    isSentryObject = false
  )

  val date: LocalDate = LocalDate.parse("2024-08-04")

  val nasaResponse: NasaResponse = NasaResponse(
    elementCount = 1,
    nearEarthObjects = Map(date -> asteroidList)
  )

  val nasaResponseTwo: NasaResponse = NasaResponse(
    elementCount = 1,
    nearEarthObjects = Map(date -> List(asteroid))
  )

}
