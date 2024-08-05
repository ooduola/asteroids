package config

import pureconfig._
import pureconfig.generic.auto._

case class ApiConfig(apiKey: String, baseUrl: String, listPath: String, detailPath: String)
case class AppConfig(api: ApiConfig)

object ConfigLoader {
  def loadConfig: AppConfig = {
    ConfigSource.default.loadOrThrow[AppConfig]
  }
}