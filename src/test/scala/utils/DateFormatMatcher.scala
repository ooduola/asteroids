package utils

import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateFormatMatcher(expectedFormat: String) extends ArgumentMatcher[String] {
  private val formatter = DateTimeFormatter.ofPattern(expectedFormat)

  override def matches(argument: String): Boolean = {
    try {
      LocalDate.parse(argument, formatter)
      true
    } catch {
      case _: Exception => false
    }
  }
}

object DateMatchers {
  def isValidDateFormat(expectedFormat: String): String = argThat(new DateFormatMatcher(expectedFormat))
}
