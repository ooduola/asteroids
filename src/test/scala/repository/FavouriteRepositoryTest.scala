package repository

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Resource}
import doobie._
import doobie.implicits._
import model.{FavouriteAlreadyExistsError, FavouriteDbError}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import repository.FavouriteRepositoryImpl
import utils.TestData.asteroidSummary
import utils.TransactorUtil.createH2Transactor

import scala.io.Source

class FavouriteRepositoryTest extends AnyFunSuite with Matchers {

  test("addFavourite should return Right(()) when the asteroid is added successfully") {
    createH2Transactor.use { transactor =>
      for {
        _ <- setupSchema(transactor)
        result <- new FavouriteRepositoryImpl[IO](transactor).addFavourite(asteroidSummary)
        _ <- cleanupSchema(transactor)
      } yield {
        result shouldBe Right(())
      }
    }.unsafeRunSync()
  }

  test("addFavourite should return Left(FavouriteAlreadyExistsError) when a UNIQUE_VIOLATION error occurs") {
    createH2Transactor.use { transactor =>
      for {
        _ <- setupSchema(transactor)
        _ <- new FavouriteRepositoryImpl[IO](transactor).addFavourite(asteroidSummary)
        result <- new FavouriteRepositoryImpl[IO](transactor).addFavourite(asteroidSummary)
        _ <- cleanupSchema(transactor)
      } yield {
        result shouldBe Left(FavouriteAlreadyExistsError)
      }
    }.unsafeRunSync()
  }

  test("getListFavourites should return a list of favorites when fetching is successful") {
    createH2Transactor.use { transactor =>
      for {
        _ <- setupSchema(transactor)
        _ <- new FavouriteRepositoryImpl[IO](transactor).addFavourite(asteroidSummary)
        result <- new FavouriteRepositoryImpl[IO](transactor).getListFavourites
        _ <- cleanupSchema(transactor) // Cleanup after the test
      } yield {
        result shouldBe List(asteroidSummary)
      }
    }.unsafeRunSync()
  }

  test("getListFavourites should return Left(FavouriteDbError) when no table is present adding to favorites") {
    createH2Transactor.use { transactor =>
      for {
        _ <- runSqlScript(transactor, "sql/create_wrong_table.sql")
        result <- new FavouriteRepositoryImpl[IO](transactor).addFavourite(asteroidSummary)
        _ <- cleanupSchema(transactor)
      } yield {
        result should matchPattern { case Left(_: FavouriteDbError) => }
      }
    }.unsafeRunSync()
  }

  test("getListFavourites should return Left(FavouriteDbError) when an error occurs while fetching") {
    createH2Transactor.use { transactor =>
      for {
        _ <- runSqlScript(transactor, "sql/create_wrong_table.sql")
        result <- new FavouriteRepositoryImpl[IO](transactor).getListFavourites.attempt
        _ <- cleanupSchema(transactor)
      } yield {
        result should matchPattern { case Left(_: FavouriteDbError) => }
      }
    }.unsafeRunSync()
  }

  private def loadSqlScript(name: String): String = {
    val source = Source.fromResource(name)
    try source.mkString finally source.close()
  }

  private def runSqlScript(transactor: Transactor[IO], scriptName: String): IO[Unit] =
    Fragment.const(loadSqlScript(scriptName)).update.run.transact(transactor).void

  private def setupSchema(transactor: Transactor[IO]): IO[Unit] =
    runSqlScript(transactor, "sql/create_favourites_table.sql")

  private def cleanupSchema(transactor: Transactor[IO]): IO[Unit] =
    runSqlScript(transactor, "sql/drop_all_table.sql")
}
