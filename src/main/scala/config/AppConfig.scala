package config

import com.comcast.ip4s.{Host, Port}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.ip4s._

case class ApiConfig(apiKey: String, baseUrl: String, listPath: String, detailPath: String)
case class DbConfig(url: String, password: String, user: String)
case class ServerConfig(host: Host, port: Port)
case class AppConfig(api: ApiConfig, db: DbConfig, server: ServerConfig)

object ConfigLoader {
  def loadConfig: AppConfig = {
    ConfigSource.default.loadOrThrow[AppConfig]
  }
}
