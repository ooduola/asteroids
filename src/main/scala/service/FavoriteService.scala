package service

import cats.effect.Async
import model.api._
import repository.FavoriteRepository

trait FavoriteService[F[_]] {
  def addFavorite(asteroid: AsteroidSummary): F[Unit]
  def fetchFavorites(): F[List[AsteroidSummary]]
}

class FavoriteServiceImpl[F[_]: Async](favoriteRepository: FavoriteRepository[F]) extends FavoriteService[F] {

  override def addFavorite(asteroid: AsteroidSummary): F[Unit] =
    favoriteRepository.addFavorite(asteroid)

  override def fetchFavorites():  F[List[AsteroidSummary]] =
    favoriteRepository.getListFavorites
}
