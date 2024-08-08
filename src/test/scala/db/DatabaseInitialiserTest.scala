package db

import cats.effect._
import cats.effect.unsafe.implicits.global
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.testcontainers.containers.PostgreSQLContainer
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class DatabaseInitialiserTest extends AnyFunSuite with BeforeAndAfterAll {

  class PostgreSQLContainerFixture extends PostgreSQLContainer("postgres:latest")

  implicit val logger: Logger[IO] = Slf4jLogger.getLogger[IO]
  private val container = new PostgreSQLContainerFixture()

  override def beforeAll(): Unit = {
    container.start()
  }

  override def afterAll(): Unit = {
    container.stop()
  }

  test("migrate should successfully apply migrations") {

    val dbUrl = container.getJdbcUrl
    val dbUser = container.getUsername
    val dbPassword = container.getPassword

    val result = DatabaseInitialiser.migrate[IO](dbUrl, dbUser, dbPassword).attempt.unsafeRunSync()

    assert(result.isRight, s"Migration failed: ${result.left.getOrElse("")}")
  }

  test("migrate should handle migration failures") {
    val invalidDbUrl = "jdbc:postgresql://invalid-host:5432/test"
    val dbUser = "user"
    val dbPassword = "password"

    val result = DatabaseInitialiser.migrate[IO](invalidDbUrl, dbUser, dbPassword).attempt.unsafeRunSync()

    assert(result.isLeft, "Migration should have failed but it didn't")
  }
}
