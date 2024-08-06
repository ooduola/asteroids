package db

import cats.effect._
import com.zaxxer.hikari.HikariConfig
import config.DbConfig
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

object DatabaseConfig {

  def transactor[F[_]: Async](config: DbConfig)(implicit ec: ExecutionContext): Resource[F, HikariTransactor[F]] = {
    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl(config.url)
    hikariConfig.setUsername(config.user)
    hikariConfig.setPassword(config.password)
    hikariConfig.setDriverClassName(config.driver)

    HikariTransactor.fromHikariConfig[F](hikariConfig, ec)
  }
}
