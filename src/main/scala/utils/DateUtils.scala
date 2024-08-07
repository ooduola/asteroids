package utils

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.implicits._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

object DateUtils {

  private type ValidationResult[T] = ValidatedNel[String, T]

  private val DateFormatterPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def validateDates(startDate: String, endDate: String): ValidationResult[(LocalDate, LocalDate)] = {
    (validateDate(startDate), validateDate(endDate)).mapN((_, _))
  }

  private def validateDate(date: String): ValidationResult[LocalDate] = {
    parseDate(date) match {
      case Some(parsedDate) => Valid(parsedDate)
      case None => Validated.invalidNel(s"Date '$date' does not match format 'YYYY-MM-DD'")
    }
  }

  private def parseDate(dateStr: String): Option[LocalDate] = {
    Try(LocalDate.parse(dateStr, DateFormatterPattern)).toOption
  }
}
