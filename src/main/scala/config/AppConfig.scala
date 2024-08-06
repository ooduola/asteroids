package config

import cats.effect._
import cats.syntax.all._
import com.comcast.ip4s.{Host, Port}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.ip4s._
import org.typelevel.log4cats.Logger

case class ApiConfig(apiKey: String, baseUrl: String, listPath: String, detailPath: String)
case class DbConfig(url: String, password: String, user: String, driver: String)
case class ServerConfig(host: Host, port: Port)
case class AppConfig(api: ApiConfig, db: DbConfig, server: ServerConfig)

object ConfigLoader {
  def loadConfig[F[_]: Sync](implicit logger: Logger[F]): F[AppConfig] = {
    Sync[F].delay(ConfigSource.default.load[AppConfig]).flatMap {
      case Right(cfg) => Sync[F].pure(cfg)
      case Left(e) =>
        logger.error(s"Failed to load configuration: ${e.toList.mkString(", ")}") *>
          Sync[F].raiseError[AppConfig](new Exception(e.toList.mkString(", ")))
    }
  }
}
