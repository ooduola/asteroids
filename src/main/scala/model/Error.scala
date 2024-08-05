package model

sealed trait Error extends Product with Serializable
case class HttpError(message: String) extends Error
case class ParsingError(message: String) extends Error
case class InvalidSortCriteriaError(message: String) extends Error
case class InvalidDetailIdError(message: String) extends Error