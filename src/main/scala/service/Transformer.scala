package service

import model.api.{AsteroidSummary, NasaResponse}

object Transformer {

  def transformResponse(resp: NasaResponse): List[AsteroidSummary] = {
    for {
      asteroids <- resp.nearEarthObjects.values.toList
      asteroid <- asteroids
    } yield AsteroidSummary(
      id = asteroid.id,
      name = asteroid.name,
      links = asteroid.links
    )
  }
}
