package model

import java.sql.SQLException

sealed abstract class DbError(val message: String, cause: Option[Throwable] = None)
  extends Exception(message, cause.orNull)

case object FavouriteAlreadyExistsError extends DbError("Asteroid with same id already exists")

case class FavouriteDbError(sqlException: SQLException)
  extends DbError(s"Error occurred when accessing database: ${sqlException.getMessage}", Some(sqlException))
