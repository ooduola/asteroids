package service

import model.nasa.{Asteroid, NasaResponse}

object Transformer {

  def transformResponse(resp: NasaResponse): List[Asteroid] = {
    resp.nearEarthObjects.values.flatten.toList
  }
}
