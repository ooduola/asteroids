package service

import cats.effect.Async
import cats.implicits._
import model.api._
import model.{DbError, FavouriteDbError, NoFavouritesExistsError}
import repository.FavouriteRepository

trait FavouriteService[F[_]] {
  def addFavourite(asteroid: AsteroidSummary): F[Either[DbError, Unit]]
  def fetchFavourites(): F[Either[DbError, List[AsteroidSummary]]]
}

class FavouriteServiceImpl[F[_]: Async](favouriteRepository: FavouriteRepository[F]) extends FavouriteService[F] {

  override def addFavourite(asteroid: AsteroidSummary): F[Either[DbError, Unit]] =
    favouriteRepository.addFavourite(asteroid).attempt.map {
      case Right(_) => Right(())
      case Left(error: DbError) => Left(error)
      case Left(unknownError) => Left(FavouriteDbError(new java.sql.SQLException(unknownError)))
    }

  override def fetchFavourites(): F[Either[DbError, List[AsteroidSummary]]] =
    favouriteRepository.getListFavourites.attempt.map {
      case Right(favourites) if favourites.isEmpty => Left(NoFavouritesExistsError)
      case Right(favourites) => Right(favourites)
      case Left(error: FavouriteDbError) =>
        Left(error)
      case Left(other) =>
        Left(FavouriteDbError(new java.sql.SQLException(other)))
    }
}
