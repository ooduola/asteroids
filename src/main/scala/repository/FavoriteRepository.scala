package repository

import cats.effect.Async
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.sqlstate.class23.UNIQUE_VIOLATION
import model.{DbError, FavouriteAlreadyExistsError, FavouriteDbError}
import model.api.AsteroidSummary

import java.sql.SQLException

trait FavoriteRepository[F[_]] {
  def addFavorite(asteroid: AsteroidSummary): F[Either[DbError, Unit]]
  def getListFavorites: F[List[AsteroidSummary]]
}

class FavoriteRepositoryImpl[F[_]: Async](transactor: Transactor[F]) extends FavoriteRepository[F] {

  override def addFavorite(asteroid: AsteroidSummary): F[Either[DbError, Unit]] = {
    FavoriteRepository
      .addFavoriteQuery(asteroid)
      .run
      .transact(transactor)
      .attemptSql
      .map {
        case Right(_) => Right(())
        case Left(sqlException) if sqlException.getSQLState == UNIQUE_VIOLATION.value =>
          Left(FavouriteAlreadyExistsError)
        case Left(sqlException) =>
          Left(FavouriteDbError(sqlException))
      }
  }

  override def getListFavorites: F[List[AsteroidSummary]] = {
    FavoriteRepository
      .getListFavoritesQuery
      .to[List]
      .transact(transactor)
      .adaptError { case sqlException: SQLException =>
        FavouriteDbError(sqlException)
      }
  }
}

object FavoriteRepository {

  def addFavoriteQuery(asteroid: AsteroidSummary): Update0 =
    sql"""
      INSERT INTO favorites (id, name, links) VALUES (${asteroid.id}, ${asteroid.name}, ${asteroid.links})
    """.update

  def getListFavoritesQuery: Query0[AsteroidSummary] =
    sql"""
      SELECT id, name, links FROM favorites
    """.query[AsteroidSummary]

}
