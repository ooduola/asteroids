package utils

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
import cats.implicits._

object DateUtils {

  private type ValidationResult[T] = ValidatedNel[String, T]
  private val DatePattern = """\d{4}-\d{2}-\d{2}""".r

  def validateDates(startDate: String, endDate: String): ValidationResult[(String, String)] = {
    (validateDate(startDate), validateDate(endDate)).mapN((_, _))
  }

  private def validateDate(date: String): ValidationResult[String] = {
    if (DatePattern.matches(date)) {
      Valid(date)
    } else {
      Validated.invalidNel(s"Date '$date' in url path does not match format YYYY-MM-DD")
    }
  }
}
