package com.example.data.models

import android.content.Context
import com.example.R
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
    @Deprecated("Use getDisplayLabel(context)")
    get() = "Legacy label"

  fun getDisplayLabel(context: Context): String {
    if (customLabel != null) return customLabel
    return when (daysBefore) {
      0 -> context.getString(R.string.reminder_offset_today)
      1 -> context.getString(R.string.reminder_offset_1d)
      3 -> context.getString(R.string.reminder_offset_3d)
      7 -> context.getString(R.string.reminder_offset_1w)
      14 -> context.getString(R.string.reminder_offset_2w)
      30 -> context.getString(R.string.reminder_offset_1m)
      else -> {
        if (daysBefore % 7 == 0) {
          context.getString(R.string.reminder_offset_weeks_before, daysBefore / 7)
        } else {
          context.getString(R.string.reminder_offset_days_before, daysBefore)
        }
      }
    }
  }

  val shortLabel: String
    @Deprecated("Use getShortLabel(context)")
    get() = "Legacy short"

  fun getShortLabel(context: Context): String {
    return when (daysBefore) {
      0 -> context.getString(R.string.reminder_short_today)
      1 -> context.getString(R.string.reminder_short_1d)
      3 -> context.getString(R.string.reminder_short_3d)
      7 -> context.getString(R.string.reminder_short_1w)
      14 -> context.getString(R.string.reminder_short_2w)
      30 -> context.getString(R.string.reminder_short_1m)
      else -> context.getString(R.string.reminder_short_days_before, daysBefore)
    }
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

  fun getSummaryText(context: Context): String {
    if (!isEnabled || offsets.isEmpty()) {
      return context.getString(R.string.reminder_no_reminders)
    }
    val sortedOffsets = offsets.sortedByDescending { it.daysBefore }
    return sortedOffsets.joinToString(" · ") { it.getDisplayLabel(context) }
  }

  @Deprecated("Use getSummaryText(context)")
  val summaryText: String
    get() = "Legacy summary"

  /**
   * Generates warm, contextual notification copy for this event and specific offset.
   */
  fun getNotificationContent(context: Context, story: StoryModel, offset: ReminderOffset): Pair<String, String> {
    val title = story.getDisplayTitle(context)
    val days = offset.daysBefore

    val (headline, body) = when {
      days == 0 -> {
        when (story.category) {
          EventCategory.WEDDING -> Pair(
            context.getString(R.string.notif_headline_anniversary),
            context.getString(R.string.notif_body_anniversary, story.partnerName.ifBlank { context.getString(R.string.notif_body_partner_love) })
          )
          EventCategory.BIRTHDAY -> Pair(
            context.getString(R.string.notif_headline_birthday),
            context.getString(R.string.notif_body_birthday, title)
          )
          EventCategory.FIRST_DATE -> Pair(
            context.getString(R.string.notif_headline_first_date),
            context.getString(R.string.notif_body_first_date)
          )
          EventCategory.ENGAGEMENT -> Pair(
            context.getString(R.string.notif_headline_engagement),
            context.getString(R.string.notif_body_engagement)
          )
          else -> Pair(
            context.getString(R.string.notif_headline_generic, title),
            context.getString(R.string.notif_body_generic)
          )
        }
      }
      days == 1 -> {
        when (story.category) {
          EventCategory.WEDDING -> Pair(
            context.getString(R.string.notif_headline_anniversary_1d),
            context.getString(R.string.notif_body_anniversary_1d)
          )
          EventCategory.BIRTHDAY -> Pair(
            context.getString(R.string.notif_headline_birthday_1d),
            context.getString(R.string.notif_body_birthday_1d, title)
          )
          else -> Pair(
            context.getString(R.string.notif_headline_generic_1d),
            context.getString(R.string.notif_body_generic_1d, title)
          )
        }
      }
      days == 3 -> {
        Pair(
          context.getString(R.string.notif_headline_in_days, title, 3),
          context.getString(R.string.notif_body_in_3d)
        )
      }
      days == 7 -> {
        Pair(
          context.getString(R.string.notif_headline_1w, title),
          context.getString(R.string.notif_body_1w)
        )
      }
      days == 14 -> {
        Pair(
          context.getString(R.string.notif_headline_2w, title),
          context.getString(R.string.notif_body_2w)
        )
      }
      days == 30 -> {
        Pair(
          context.getString(R.string.notif_headline_1m, title),
          context.getString(R.string.notif_body_1m)
        )
      }
      else -> {
        Pair(
          context.getString(R.string.notif_headline_in_days, title, days),
          context.getString(R.string.notif_body_generic_days, days)
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
