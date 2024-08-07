package service

import cats.effect.Async
import cats.implicits._
import model.{FavouriteAlreadyExistsError, FavouriteDbError, DbError}
import model.api._
import repository.FavoriteRepository

trait FavoriteService[F[_]] {
  def addFavorite(asteroid: AsteroidSummary): F[Either[DbError, Unit]]
  def fetchFavorites(): F[Either[DbError, List[AsteroidSummary]]]
}

class FavoriteServiceImpl[F[_]: Async](favoriteRepository: FavoriteRepository[F]) extends FavoriteService[F] {

  override def addFavorite(asteroid: AsteroidSummary): F[Either[DbError, Unit]] =
    favoriteRepository.addFavorite(asteroid).attempt.map {
      case Right(_) => Right(())
      case Left(error: DbError) => Left(error)
      case Left(unknownError) => Left(FavouriteDbError(new java.sql.SQLException(unknownError)))
    }

  override def fetchFavorites(): F[Either[DbError, List[AsteroidSummary]]] =
    favoriteRepository.getListFavorites.attempt.map {
      case Right(favorites) => Right(favorites)
      case Left(error: FavouriteDbError) => Left(error)
      case Left(other) => Left(FavouriteDbError(new java.sql.SQLException(other)))
    }
}
