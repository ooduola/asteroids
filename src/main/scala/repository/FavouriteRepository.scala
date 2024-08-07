package repository

import cats.effect.Async
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.sqlstate.class23.UNIQUE_VIOLATION
import model.{DbError, FavouriteAlreadyExistsError, FavouriteDbError}
import model.api.AsteroidSummary
import org.typelevel.log4cats.Logger

import java.sql.SQLException

trait FavouriteRepository[F[_]] {
  def addFavourite(asteroid: AsteroidSummary): F[Either[DbError, Unit]]
  def getListFavourites: F[List[AsteroidSummary]]
}

class FavouriteRepositoryImpl[F[_]: Async](transactor: Transactor[F]) extends FavouriteRepository[F] {

  override def addFavourite(asteroid: AsteroidSummary): F[Either[DbError, Unit]] = {
    FavouriteRepository
      .addFavouriteQuery(asteroid)
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

  override def getListFavourites: F[List[AsteroidSummary]] = {
    FavouriteRepository
      .getListFavouritesQuery
      .to[List]
      .transact(transactor)
      .adaptError { case sqlException: SQLException =>
        FavouriteDbError(sqlException)
      }
  }
}

object FavouriteRepository {

  def addFavouriteQuery(asteroid: AsteroidSummary): Update0 =
    sql"""
    INSERT INTO favourites (id, name, links)
    VALUES (${asteroid.id}, ${asteroid.name}, ${asteroid.links})
  """.update

  def getListFavouritesQuery: Query0[AsteroidSummary] =
    sql"""
      SELECT id, name, links FROM favourites
    """.query[AsteroidSummary]

}
