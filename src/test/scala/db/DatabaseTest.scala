//package db
//
//import cats.effect.{IO, IOApp}
//import doobie.hikari.HikariTransactor
//import doobie.implicits._
//
//object DatabaseTest extends IOApp.Simple {
//  val url = "jdbc:postgresql://localhost:5432/mydb"
//  val user = "myuser"
//  val password = "mypassword"
//
//  def transactor: HikariTransactor[IO] =
//    HikariTransactor.newHikariTransactor[IO](
//      "org.postgresql.Driver",
//      url,
//      user,
//      password
//    ).unsafeRunSync()
//
//  def run: IO[Unit] = {
//    transactor.use { xa =>
//      sql"SELECT 1".query[Int].unique.transact(xa).flatMap { result =>
//        IO(println(s"Connection test result: $result"))
//      }
//    }
//  }
//}
