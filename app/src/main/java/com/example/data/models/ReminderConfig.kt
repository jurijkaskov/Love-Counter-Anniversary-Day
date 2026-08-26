package com.example.data.models

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

/**
 * Represents a reminder offset before an event or celebration.
 */
data class ReminderOffset(
  val daysBefore: Int,
  val customLabel: String? = null
) {
  val displayLabel: String
    get() = when {
      customLabel != null -> customLabel
      daysBefore == 0 -> "On the day"
      daysBefore == 1 -> "1 day before"
      daysBefore == 3 -> "3 days before"
      daysBefore == 7 -> "1 week before"
      daysBefore == 14 -> "2 weeks before"
      daysBefore == 30 -> "1 month before"
      daysBefore % 7 == 0 -> "${daysBefore / 7} weeks before"
      else -> "$daysBefore days before"
    }

  val shortLabel: String
    get() = when {
      daysBefore == 0 -> "Day of"
      daysBefore == 1 -> "1d before"
      daysBefore == 3 -> "3d before"
      daysBefore == 7 -> "1w before"
      daysBefore == 14 -> "2w before"
      daysBefore == 30 -> "1m before"
      else -> "${daysBefore}d before"
    }

  companion object {
    val ON_THE_DAY = ReminderOffset(0)
    val ONE_DAY_BEFORE = ReminderOffset(1)
    val THREE_DAYS_BEFORE = ReminderOffset(3)
    val ONE_WEEK_BEFORE = ReminderOffset(7)
    val TWO_WEEKS_BEFORE = ReminderOffset(14)
    val ONE_MONTH_BEFORE = ReminderOffset(30)

    val DEFAULT_QUICK_OPTIONS = listOf(
      ON_THE_DAY,
      ONE_DAY_BEFORE,
      THREE_DAYS_BEFORE,
      ONE_WEEK_BEFORE,
      TWO_WEEKS_BEFORE,
      ONE_MONTH_BEFORE
    )

    fun custom(days: Int): ReminderOffset = ReminderOffset(days.coerceAtLeast(0))
  }
}

/**
 * Reminder configuration for a specific event or milestone.
 */
data class ReminderConfig(
  val isEnabled: Boolean = true,
  val offsets: List<ReminderOffset> = listOf(
    ReminderOffset.ONE_WEEK_BEFORE,
    ReminderOffset.ONE_DAY_BEFORE,
    ReminderOffset.ON_THE_DAY
  ),
  val timeHour: Int = 9,
  val timeMinute: Int = 0
) {
  val localTime: LocalTime
    get() = LocalTime.of(timeHour.coerceIn(0, 23), timeMinute.coerceIn(0, 59))

  val formattedTime: String
    get() {
      val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
      return localTime.format(formatter)
    }

  val summaryText: String
    get() {
      if (!isEnabled || offsets.isEmpty()) {
        return "No reminders set"
      }
      val sortedOffsets = offsets.sortedByDescending { it.daysBefore }
      return sortedOffsets.joinToString(" · ") { it.displayLabel }
    }

  /**
   * Generates warm, contextual notification copy for this event and specific offset.
   */
  fun getNotificationContent(story: StoryModel, offset: ReminderOffset): Pair<String, String> {
    val title = story.displayTitle
    val days = offset.daysBefore

    val (headline, body) = when {
      days == 0 -> {
        when (story.category) {
          EventCategory.WEDDING -> Pair(
            "Happy Wedding Anniversary! ✨",
            "Today is your anniversary with ${story.partnerName.ifBlank { "your love" }}. Celebrate your story together."
          )
          EventCategory.BIRTHDAY -> Pair(
            "Happy Birthday! 🎂",
            "Today is ${title}. Make it an unforgettable celebration."
          )
          EventCategory.FIRST_DATE -> Pair(
            "Happy First Date Anniversary! ☕",
            "Celebrating the day your journey began. Cherish every memory."
          )
          EventCategory.ENGAGEMENT -> Pair(
            "Happy Engagement Anniversary! 💍",
            "Celebrating the magical moment you said yes."
          )
          else -> Pair(
            "Today is $title ✨",
            "A special day has arrived. Celebrate your love story today."
          )
        }
      }
      days == 1 -> {
        when (story.category) {
          EventCategory.WEDDING -> Pair(
            "Anniversary Tomorrow 💕",
            "Tomorrow is your wedding anniversary. A special day is almost here."
          )
          EventCategory.BIRTHDAY -> Pair(
            "Birthday Tomorrow 🎈",
            "Tomorrow is ${title}. Get ready to celebrate!"
          )
          else -> Pair(
            "Special Day Tomorrow ✨",
            "Tomorrow is $title. A meaningful moment is right around the corner."
          )
        }
      }
      days == 3 -> {
        Pair(
          "$title is in 3 days",
          "Your celebration is approaching in 3 days. Time to finalize any special plans."
        )
      }
      days == 7 -> {
        Pair(
          "1 week until $title 🗓️",
          "Your special milestone is coming up in 7 days. Time to prepare something special."
        )
      }
      days == 14 -> {
        Pair(
          "2 weeks until $title ✨",
          "Your anniversary is in 14 days. Plenty of time to plan a wonderful surprise."
        )
      }
      days == 30 -> {
        Pair(
          "1 month until $title 🌟",
          "A major celebration is in 30 days. Perfect time to plan ahead together."
        )
      }
      else -> {
        Pair(
          "$title is in $days days",
          "A special moment is coming up in $days days."
        )
      }
    }

    return Pair(headline, body)
  }

  fun toJson(): JSONObject {
    val obj = JSONObject()
    obj.put("isEnabled", isEnabled)
    obj.put("timeHour", timeHour)
    obj.put("timeMinute", timeMinute)
    val offsetsArray = JSONArray()
    offsets.forEach { offset ->
      val oObj = JSONObject()
      oObj.put("daysBefore", offset.daysBefore)
      if (offset.customLabel != null) {
        oObj.put("customLabel", offset.customLabel)
      }
      offsetsArray.put(oObj)
    }
    obj.put("offsets", offsetsArray)
    return obj
  }

  companion object {
    fun fromJson(obj: JSONObject?): ReminderConfig {
      if (obj == null) return defaultForCategory(EventCategory.RELATIONSHIP)
      val isEnabled = obj.optBoolean("isEnabled", true)
      val timeHour = obj.optInt("timeHour", 9)
      val timeMinute = obj.optInt("timeMinute", 0)
      val offsetsList = mutableListOf<ReminderOffset>()

      val array = obj.optJSONArray("offsets")
      if (array != null) {
        for (i in 0 until array.length()) {
          val oObj = array.optJSONObject(i)
          if (oObj != null) {
            val daysBefore = oObj.optInt("daysBefore", 0)
            val customLabel = if (oObj.has("customLabel")) oObj.optString("customLabel") else null
            offsetsList.add(ReminderOffset(daysBefore, customLabel))
          }
        }
      }

      val finalOffsets = if (offsetsList.isEmpty() && isEnabled) {
        listOf(ReminderOffset.ONE_WEEK_BEFORE, ReminderOffset.ONE_DAY_BEFORE, ReminderOffset.ON_THE_DAY)
      } else {
        offsetsList
      }

      return ReminderConfig(
        isEnabled = isEnabled,
        offsets = finalOffsets,
        timeHour = timeHour,
        timeMinute = timeMinute
      )
    }

    /**
     * Smart suggestions tailored to specific event types.
     */
    fun defaultForCategory(category: EventCategory): ReminderConfig {
      return when (category) {
        EventCategory.WEDDING -> ReminderConfig(
          isEnabled = true,
          offsets = listOf(
            ReminderOffset.ONE_MONTH_BEFORE,
            ReminderOffset.ONE_WEEK_BEFORE,
            ReminderOffset.ONE_DAY_BEFORE,
            ReminderOffset.ON_THE_DAY
          ),
          timeHour = 9,
          timeMinute = 0
        )
        EventCategory.BIRTHDAY -> ReminderConfig(
          isEnabled = true,
          offsets = listOf(
            ReminderOffset.ONE_WEEK_BEFORE,
            ReminderOffset.ONE_DAY_BEFORE,
            ReminderOffset.ON_THE_DAY
          ),
          timeHour = 9,
          timeMinute = 0
        )
        EventCategory.ENGAGEMENT, EventCategory.FIRST_DATE, EventCategory.RELATIONSHIP -> ReminderConfig(
          isEnabled = true,
          offsets = listOf(
            ReminderOffset.ONE_WEEK_BEFORE,
            ReminderOffset.ONE_DAY_BEFORE,
            ReminderOffset.ON_THE_DAY
          ),
          timeHour = 9,
          timeMinute = 0
        )
        EventCategory.TRIP, EventCategory.SPECIAL_DAY, EventCategory.CUSTOM -> ReminderConfig(
          isEnabled = true,
          offsets = listOf(
            ReminderOffset.ONE_WEEK_BEFORE,
            ReminderOffset.ONE_DAY_BEFORE,
            ReminderOffset.ON_THE_DAY
          ),
          timeHour = 9,
          timeMinute = 0
        )
      }
    }

    fun suggestionsForCategory(category: EventCategory): List<ReminderOffset> {
      return when (category) {
        EventCategory.WEDDING -> listOf(
          ReminderOffset.ONE_MONTH_BEFORE,
          ReminderOffset.ONE_WEEK_BEFORE,
          ReminderOffset.ONE_DAY_BEFORE,
          ReminderOffset.ON_THE_DAY
        )
        EventCategory.BIRTHDAY -> listOf(
          ReminderOffset.ONE_WEEK_BEFORE,
          ReminderOffset.ONE_DAY_BEFORE,
          ReminderOffset.ON_THE_DAY
        )
        else -> listOf(
          ReminderOffset.ONE_WEEK_BEFORE,
          ReminderOffset.ONE_DAY_BEFORE,
          ReminderOffset.ON_THE_DAY
        )
      }
    }
  }
}
