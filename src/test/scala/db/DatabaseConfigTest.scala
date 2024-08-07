package db

import cats.effect._
import cats.effect.unsafe.implicits.global
import com.zaxxer.hikari.HikariConfig
import config.DbConfig
import db.DatabaseConfig
import doobie.hikari.HikariTransactor
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.ExecutionContext

class DatabaseConfigTest extends AnyFunSuite with MockitoSugar {

  implicit val ec: ExecutionContext = ExecutionContext.global

  test("transactor should be correctly configured") {
    val dbConfig = DbConfig("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "user", "password", "org.h2.Driver")
    val transactorResource = DatabaseConfig.transactor[IO](dbConfig)

    val transactor = transactorResource.use { t =>
      IO {
        assert(t.isInstanceOf[HikariTransactor[IO]], "Transactor is not an instance of HikariTransactor[IO]")

        // Access the HikariConfig to verify settings
        val hikariConfig: HikariConfig = t.kernel

        assert(hikariConfig.getJdbcUrl == dbConfig.url, s"JDBC URL is not correctly configured: ${hikariConfig.getJdbcUrl}")
        assert(hikariConfig.getUsername == dbConfig.user, s"Username is not correctly configured: ${hikariConfig.getUsername}")
        assert(hikariConfig.getPassword == dbConfig.password, s"Password is not correctly configured")
        assert(hikariConfig.getDriverClassName == dbConfig.driver, s"Driver class name is not correctly configured: ${hikariConfig.getDriverClassName}")

      }
    }.unsafeRunSync()
  }

}
