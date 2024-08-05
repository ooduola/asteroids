package model

sealed trait SortBy

object SortBy {
  case object Name extends SortBy

  def fromString(value: String): Option[SortBy] = value.toLowerCase match {
    case "name" => Some(Name)
    case _ => None
  }
}
