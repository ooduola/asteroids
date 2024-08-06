//package repository
//
//import cats.effect._
//import com.zaxxer.hikari.HikariConfig
//import doobie.hikari.HikariTransactor
//import org.scalatest.matchers.should.Matchers
//import model.api._
//import org.h2.jdbcx.JdbcDataSource
//import org.scalatest.funsuite.AnyFunSuite
//
//import scala.concurrent.ExecutionContext
//
//
//class FavoriteRepositoryTest extends AnyFunSuite with Matchers {
//
//  object TestTransactor {
//
//    def transactor[F[_]: Async]: Resource[F, HikariTransactor[F]] = {
//      val dataSource = new JdbcDataSource()
//      dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
//      dataSource.setUser("")
//      dataSource.setPassword("")
//
//      val hikariConfig = new HikariConfig()
//      hikariConfig.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
//      hikariConfig.setUsername("")
//      hikariConfig.setPassword("")
//      hikariConfig.setDriverClassName("org.h2.Driver")
//
//      HikariTransactor.fromHikariConfig[F](hikariConfig, ExecutionContext.global)
//    }
//  }
//
//  test("new test") {
//    val transactorResource = TestTransactor.transactor[IO]
//
//    transactorResource.use { xa =>
//      val repo = new FavoriteRepositoryImpl[IO](xa)
//
//      for {
//        _ <- repo.addFavorite(AsteroidSummary("1", "Asteroid 1", DetailLink("d")))
//        favorites <- repo.getListFavorites
//      } yield {
//        favorites should contain(AsteroidSummary("1", "Asteroid 1", DetailLink("d")))
//      }
//    }
//  }
//
//  test("new test 2") {
//    val transactorResource = TestTransactor.transactor[IO]
//
//    transactorResource.use { xa =>
//      val repo = new FavoriteRepositoryImpl[IO](xa)
//
//      for {
//        _ <- repo.getListFavorites
//        favorites <- repo.getListFavorites
//      } yield {
//        favorites should contain(AsteroidSummary("1", "Asteroid 1", DetailLink("d")))
//      }
//    }
//  }
//}
//
//
////class FavoriteRepositoryTest extends AnyFunSuite with Matchers with BeforeAndAfterAll {
////
////  object TestTransactor {
////    def transactor[F[_]: Async]: Resource[F, HikariTransactor[F]] = {
////      val dataSource = new JdbcDataSource()
////      dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
////      dataSource.setUser("")
////      dataSource.setPassword("")
////
////      val hikariConfig = new HikariConfig()
////      hikariConfig.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
////      hikariConfig.setUsername("")
////      hikariConfig.setPassword("")
////      hikariConfig.setDriverClassName("org.h2.Driver")
////
////      HikariTransactor.fromHikariConfig[F](hikariConfig, ExecutionContext.global)
////    }
////  }
////
////  private def setupSchema(xa: HikariTransactor[IO]): IO[Unit] = {
////    val createSchema = sql"""
////      CREATE TABLE favorites (
////        id VARCHAR(255) PRIMARY KEY,
////        name VARCHAR(255),
////        link VARCHAR(255)
////      )
////    """.update.run
////    createSchema.transact(xa).void
////  }
////
////  private def clearSchema(xa: HikariTransactor[IO]): IO[Unit] = {
////    val dropSchema = sql"DROP TABLE IF EXISTS favorites".update.run
////    dropSchema.transact(xa).void
////  }
////
////  override def beforeAll(): Unit = {
////    super.beforeAll()
////    TestTransactor.transactor[IO].use { xa =>
////      setupSchema(xa)
////    }.unsafeRunSync()
////  }
////
////  override def afterAll(): Unit = {
////    super.afterAll()
////    TestTransactor.transactor[IO].use { xa =>
////      clearSchema(xa)
////    }.unsafeRunSync()
////  }
////
////  test("new test") {
////    TestTransactor.transactor[IO].use { xa =>
////      val repo = new FavoriteRepositoryImpl[IO](xa)
////
////      for {
////        _ <- repo.addFavorite(AsteroidSummary("1", "Asteroid 1", DetailLink("d")))
////        favorites <- repo.getListFavorites
////      } yield {
////        favorites should contain(AsteroidSummary("1", "Asteroid 1", DetailLink("d")))
////      }
////    }.unsafeRunSync()
////  }
////
////  test("new test 2") {
////    TestTransactor.transactor[IO].use { xa =>
////      val repo = new FavoriteRepositoryImpl[IO](xa)
////
////      for {
////        _ <- repo.addFavorite(AsteroidSummary("1", "Asteroid 1", DetailLink("d")))
////        favorites <- repo.getListFavorites
////      } yield {
////        favorites should contain(AsteroidSummary("1", "Asteroid 1", DetailLink("d")))
////      }
////    }.unsafeRunSync()
////  }
////}
