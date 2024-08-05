package utils

import model.nasa._

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

  val asteroidDetail: AsteroidDetail = AsteroidDetail(
    links = DetailLink("http://example.com/neo/1/detail"),
    id = "1",
    neoReferenceId = "1",  // Updated field name
    name = "Test Asteroid Detail",
    designation = "2021 AB",
    nasaJplUrl = "http://example.com/neo/1",  // Updated field name
    absoluteMagnitudeH = 10.0,  // Updated field name
    estimatedDiameter = EstimatedDiameter(
      kilometers = Diameter(1.0, 2.0),
      meters = Diameter(1000, 2000),
      miles = Diameter(0.6, 1.2),
      feet = Diameter(3.3, 6.6)
    ),
    isPotentiallyHazardousAsteroid = false,  // Updated field name
    closeApproachData = List(
      CloseApproachData(
        closeApproachDate = "2024-08-04",  // Updated field name
        closeApproachDateFull = "2024-Aug-04 14:00 UT",  // Updated field name
        epochDateCloseApproach = 1710480000000L,  // Updated field name
        relativeVelocity = RelativeVelocity(
          kilometersPerSecond = "12.34",  // Updated field name
          kilometersPerHour = "44500",  // Updated field name
          milesPerHour = "27600"  // Updated field name
        ),
        missDistance = MissDistance(
          astronomical = "0.012",
          lunar = "4.6",
          kilometers = "12000",
          miles = "7500"
        ),
        orbitingBody = "Earth"  // Updated field name
      )
    ),
    orbitalData = OrbitalData(
      orbitId = "1",  // Updated field name
      orbitDeterminationDate = "2024-07-15T00:00:00Z",  // Updated field name
      firstObservationDate = "2024-01-01",  // Updated field name
      lastObservationDate = "2024-08-03",  // Updated field name
      dataArcInDays = 200,
      observationsUsed = 50,  // Updated field name
      orbitUncertainty = "0.1",  // Updated field name
      minimumOrbitIntersection = "0.01",  // Updated field name
      jupiterTisserandInvariant = "3.5",  // Updated field name
      epochOsculation = "2024-08-01",  // Updated field name
      eccentricity = "0.1",
      semiMajorAxis = "1.2",  // Updated field name
      inclination = "10.0",
      ascendingNodeLongitude = "100.0",  // Updated field name
      orbitalPeriod = "365.25",  // Updated field name
      perihelionDistance = "0.9",  // Updated field name
      perihelionArgument = "0.5",  // Updated field name
      aphelionDistance = "1.5",  // Updated field name
      perihelionTime = "2024-07-20",  // Updated field name
      meanAnomaly = "0.7",  // Updated field name
      meanMotion = "0.1",  // Updated field name
      equinox = "J2000",
      orbitClass = OrbitClass(
        orbitClassType = "Near-Earth",  // Updated field name
        orbitClassDescription = "Near-Earth orbit class",  // Updated field name
        orbitClassRange = "0.1 - 1.0 AU"  // Updated field name
      )
    ),
    isSentryObject = false  // Updated field name
  )

  val nasaResponse: NasaResponse = NasaResponse(
    elementCount = 1,
    nearEarthObjects = Map("2024-08-04" -> asteroidList)
  )

  val nasaResponseTwo: NasaResponse = NasaResponse(
    elementCount = 1,
    nearEarthObjects = Map("2024-08-04" -> List(asteroid))
  )

}
