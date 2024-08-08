package utils

import cats.effect.{IO, Resource}
import doobie.Transactor

object TransactorUtil {

  def createH2Transactor: Resource[IO, Transactor[IO]] =
    Resource.eval {
      IO.delay {
        Transactor.fromDriverManager[IO](
          "org.h2.Driver",
          "jdbc:h2:mem:test-2;DB_CLOSE_DELAY=-1",
          "sa",
          ""
        )
      }
    }
}