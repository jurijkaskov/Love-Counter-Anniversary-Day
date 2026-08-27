package com.example.data.models

import android.content.Context
import com.example.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

enum class EventCategory(
  val id: String,
  val titleResId: Int,
  val iconKey: String
) {
  RELATIONSHIP("relationship", com.example.R.string.cat_relationship_title, "favorite"),
  WEDDING("wedding", com.example.R.string.cat_wedding_title, "celebration"),
  FIRST_DATE("first_date", com.example.R.string.cat_first_date_title, "favorite_border"),
  ENGAGEMENT("engagement", com.example.R.string.cat_engagement_title, "ring"),
  BIRTHDAY("birthday", com.example.R.string.cat_birthday_title, "cake"),
  SPECIAL_DAY("special_day", com.example.R.string.cat_special_day_title, "star"),
  TRIP("trip", com.example.R.string.cat_trip_title, "flight"),
  CUSTOM("custom", com.example.R.string.cat_custom_title, "star");

  companion object {
    fun fromId(id: String): EventCategory {
      return entries.find { it.id == id } ?: RELATIONSHIP
    }
  }
}

data class StoryModel(
  val id: String = UUID.randomUUID().toString(),
  val category: EventCategory = EventCategory.RELATIONSHIP,
  val yourName: String = "",
  val partnerName: String = "",
  val title: String = "",
  val dateEpochDay: Long = LocalDate.now().toEpochDay(),
  val note: String = "",
  val iconKey: String = "favorite",
  val themeAccent: String = "rosewood",
  val isPrimary: Boolean = false,
  val isFavorite: Boolean = false,
  val reminderConfig: ReminderConfig = ReminderConfig.defaultForCategory(category),
  val createdAtEpochMillis: Long = System.currentTimeMillis()
) {
  val localDate: LocalDate
    get() = LocalDate.ofEpochDay(dateEpochDay)

  fun getDisplayTitle(context: Context): String = when {
    title.isNotBlank() -> title
    yourName.isNotBlank() && partnerName.isNotBlank() -> "$yourName & $partnerName"
    yourName.isNotBlank() -> yourName
    partnerName.isNotBlank() -> partnerName
    else -> context.getString(category.titleResId)
  }

  val displayTitle: String
    get() = title.ifBlank {
      if (yourName.isNotBlank() && partnerName.isNotBlank()) "$yourName & $partnerName"
      else yourName.ifBlank { partnerName.ifBlank { "" } }
    }

  fun getDisplayInitials(context: Context): String {
    val first = yourName.trim().firstOrNull()?.uppercaseChar()
    val second = partnerName.trim().firstOrNull()?.uppercaseChar()
    return when {
      first != null && second != null -> "$first & $second"
      first != null -> "$first"
      second != null -> "$second"
      else -> context.getString(R.string.model_initials_placeholder)
    }
  }

  @Deprecated("Use getDisplayInitials(context)", ReplaceWith("getDisplayInitials(context)"))
  val displayInitials: String
    get() {
      val first = yourName.trim().firstOrNull()?.uppercaseChar()
      val second = partnerName.trim().firstOrNull()?.uppercaseChar()
      return when {
        first != null && second != null -> "$first & $second"
        first != null -> "$first"
        second != null -> "$second"
        else -> "♥"
      }
    }

  val formattedDate: String
    get() {
      val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
      return localDate.format(formatter)
    }

  val isPastDate: Boolean
    get() = !localDate.isAfter(LocalDate.now())

  val totalDays: Long
    get() = if (isPastDate) {
      ChronoUnit.DAYS.between(localDate, LocalDate.now())
    } else {
      ChronoUnit.DAYS.between(LocalDate.now(), localDate)
    }

  val totalWeeks: Long
    get() = if (isPastDate) {
      ChronoUnit.WEEKS.between(localDate, LocalDate.now())
    } else {
      ChronoUnit.WEEKS.between(LocalDate.now(), localDate)
    }

  val totalMonths: Long
    get() = if (isPastDate) {
      ChronoUnit.MONTHS.between(localDate, LocalDate.now())
    } else {
      ChronoUnit.MONTHS.between(LocalDate.now(), localDate)
    }

  val totalHours: Long
    get() = totalDays * 24

  val daysDifference: Long
    get() = totalDays

  val countdownDays: Long
    get() = totalDays

  /**
   * Exact Gregorian calendar period breakdown (years, months, days)
   */
  val exactPeriod: java.time.Period
    get() {
      val today = LocalDate.now()
      return if (isPastDate) {
        java.time.Period.between(localDate, today)
      } else {
        java.time.Period.between(today, localDate)
      }
    }

  val exactYears: Int
    get() = Math.abs(exactPeriod.years)

  val exactMonths: Int
    get() = Math.abs(exactPeriod.months)

  val exactDays: Int
    get() = Math.abs(exactPeriod.days)

  fun getFormattedPeriodBreakdown(context: Context): String {
    val y = exactYears
    val m = exactMonths
    val d = exactDays

    val parts = mutableListOf<String>()
    if (y > 0) parts.add("$y ${context.getString(if (y == 1) R.string.unit_year else R.string.unit_years)}")
    if (m > 0) parts.add("$m ${context.getString(if (m == 1) R.string.unit_month else R.string.unit_months)}")
    if (d > 0 || parts.isEmpty()) parts.add("$d ${context.getString(if (d == 1) R.string.unit_day else R.string.unit_days)}")

    return parts.joinToString(" · ")
  }

  @Deprecated("Use getFormattedPeriodBreakdown(context)", ReplaceWith("getFormattedPeriodBreakdown(context)"))
  val formattedPeriodBreakdown: String
    get() {
      val y = exactYears
      val m = exactMonths
      val d = exactDays

      val parts = mutableListOf<String>()
      if (y > 0) parts.add("$y ${if (y == 1) "year" else "years"}")
      if (m > 0) parts.add("$m ${if (m == 1) "month" else "months"}")
      if (d > 0 || parts.isEmpty()) parts.add("$d ${if (d == 1) "day" else "days"}")

      return parts.joinToString(" · ")
    }

  /**
   * Calculates the next upcoming anniversary date and details.
   */
  val nextAnniversaryDate: LocalDate
    get() {
      val today = LocalDate.now()
      val start = localDate
      // If the event itself is in the future, the next moment is the event date
      if (!isPastDate) return start

      var targetYear = today.year
      var candidate = try {
        start.withYear(targetYear)
      } catch (e: Exception) {
        // Handle Feb 29 on non-leap years
        LocalDate.of(targetYear, 2, 28)
      }

      if (candidate.isBefore(today)) {
        targetYear += 1
        candidate = try {
          start.withYear(targetYear)
        } catch (e: Exception) {
          LocalDate.of(targetYear, 2, 28)
        }
      }
      return candidate
    }

  val nextAnniversaryYears: Int
    get() = nextAnniversaryDate.year - localDate.year

  val daysUntilNextAnniversary: Long
    get() = ChronoUnit.DAYS.between(LocalDate.now(), nextAnniversaryDate)

  fun getNextAnniversaryTitle(context: Context): String {
    val count = nextAnniversaryYears
    if (count <= 0) return context.getString(R.string.model_anniversary_special)

    return context.getString(R.string.calendar_event_anniversary_year, count)
  }

  @Deprecated("Use getNextAnniversaryTitle(context)", ReplaceWith("getNextAnniversaryTitle(context)"))
  val nextAnniversaryTitle: String
    get() {
      val count = nextAnniversaryYears
      if (count <= 0) return "Special Celebration"
      val suffix = when {
        count % 100 in 11..13 -> "th"
        count % 10 == 1 -> "st"
        count % 10 == 2 -> "nd"
        count % 10 == 3 -> "rd"
        else -> "th"
      }
      return "$count$suffix Anniversary"
    }

  val nextAnniversaryFormattedDate: String
    get() {
      val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
      return nextAnniversaryDate.format(formatter)
    }

  /**
   * Progress of the current year (from last anniversary to next anniversary, 0.0f to 1.0f)
   */
  val currentYearProgress: Float
    get() {
      val today = LocalDate.now()
      val next = nextAnniversaryDate
      val prev = try {
        next.minusYears(1)
      } catch (e: Exception) {
        today
      }
      val totalDaysInYear = ChronoUnit.DAYS.between(prev, next).coerceAtLeast(1)
      val daysPassed = ChronoUnit.DAYS.between(prev, today).coerceAtLeast(0)
      return (daysPassed.toFloat() / totalDaysInYear.toFloat()).coerceIn(0.0f, 1.0f)
    }

  /**
   * Next occurrence date (exact date for future events, or next yearly anniversary for past events)
   */
  val nextOccurrenceDate: LocalDate
    get() = if (!isPastDate) localDate else nextAnniversaryDate

  val daysUntilNextOccurrence: Long
    get() = ChronoUnit.DAYS.between(LocalDate.now(), nextOccurrenceDate)

  val formattedNextOccurrenceDate: String
    get() {
      val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
      return nextOccurrenceDate.format(formatter)
    }

  fun getCountdownBadgeText(context: Context): String {
    val days = daysUntilNextOccurrence
    return when {
      days == 0L -> context.getString(R.string.model_countdown_today)
      days == 1L -> context.getString(R.string.model_countdown_tomorrow)
      days < 0L -> context.getString(R.string.model_countdown_passed)
      else -> context.getString(R.string.model_countdown_in_days, days.toInt())
    }
  }

  @Deprecated("Use getCountdownBadgeText(context)", ReplaceWith("getCountdownBadgeText(context)"))
  val countdownBadgeText: String
    get() {
      val days = daysUntilNextOccurrence
      return when {
        days == 0L -> "Today!"
        days == 1L -> "Tomorrow"
        days < 0L -> "Passed"
        else -> "In $days days"
      }
    }

  fun getElapsedBadgeText(context: Context): String {
    val days = totalDays
    return when {
      days == 0L -> context.getString(R.string.model_elapsed_today)
      days == 1L -> context.getString(R.string.model_elapsed_yesterday)
      else -> context.getString(R.string.model_elapsed_days_ago, days.toInt())
    }
  }

  @Deprecated("Use getElapsedBadgeText(context)", ReplaceWith("getElapsedBadgeText(context)"))
  val elapsedBadgeText: String
    get() {
      val days = totalDays
      return when {
        days == 0L -> "Today"
        days == 1L -> "Yesterday"
        else -> "$days days ago"
      }
    }
}
