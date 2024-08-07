package repository

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import doobie._
import doobie.implicits._
import model.api.{AsteroidSummary, DetailLink}
import model.{FavouriteAlreadyExistsError, FavouriteDbError}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import utils.TestData.asteroidSummary
import utils.TransactorUtil.createH2Transactor

import java.sql.SQLException
import scala.io.Source

class FavoriteRepositoryTest extends AnyFunSuite with Matchers {

  def loadSqlScript(name: String): String = {
    val source = Source.fromResource(name)
    try source.mkString finally source.close()
  }

  test("addFavorite should return Right(()) when the asteroid is added successfully") {
    createH2Transactor.use { transactor =>
      val repository = new FavoriteRepositoryImpl[IO](transactor)

      Fragment.const(loadSqlScript("sql/create_favorites_table.sql"))
        .update
        .run
        .transact(transactor)
        .flatMap { _ =>
          repository.addFavorite(asteroidSummary)
        }.map { result =>
          result shouldBe Right(())
        }
    }.unsafeRunSync()
  }

  test("addFavorite should return Left(FavouriteAlreadyExistsError) when a UNIQUE_VIOLATION error occurs") {
    createH2Transactor.use { transactor =>
      val repository = new FavoriteRepositoryImpl[IO](transactor)

      val setup = for {
        _ <-
          sql"""
          CREATE TABLE favorites (
            id VARCHAR PRIMARY KEY,
            name VARCHAR NOT NULL,
            links VARCHAR NOT NULL
          )
        """.update.run.transact(transactor)

        _ <- repository.addFavorite(asteroidSummary)
        result <- repository.addFavorite(asteroidSummary)
      } yield result

      setup.map { result =>
        result shouldBe Left(FavouriteAlreadyExistsError)
      }
    }.unsafeRunSync()
  }

  test("getListFavorites should return a list of favorites when fetching is successful") {
    createH2Transactor.use { transactor =>
      val repository = new FavoriteRepositoryImpl[IO](transactor)

      val setup = for {
        _ <-
          sql"""
          CREATE TABLE favorites (
            id VARCHAR PRIMARY KEY,
            name VARCHAR NOT NULL,
            links VARCHAR NOT NULL
          )
        """.update.run.transact(transactor)

        _ <- repository.addFavorite(asteroidSummary)
        result <- repository.getListFavorites
      } yield result

      setup.map { result =>
        result shouldBe List(asteroidSummary)
      }
    }.unsafeRunSync()
  }

  test("getListFavorites should return Left(FavouriteDbError) when an error occurs while fetching") {
    createH2Transactor.use { transactor =>
      val repository = new FavoriteRepositoryImpl[IO](transactor)

      // Modify the query to induce an error, such as a table not existing
      val faultySetup = for {
        _ <-
          sql"""
          CREATE TABLE wrong_table (
            id VARCHAR PRIMARY KEY,
            name VARCHAR NOT NULL,
            links VARCHAR NOT NULL
          )
        """.update.run.transact(transactor)
        result <- repository.getListFavorites
      } yield result

      faultySetup.map { result =>
        result shouldBe Left(FavouriteDbError(new java.sql.SQLException("Table does not exist")))
      }
    }.unsafeRunSync()
  }
}


