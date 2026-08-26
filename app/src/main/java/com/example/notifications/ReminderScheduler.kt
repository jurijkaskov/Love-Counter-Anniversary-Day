package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.PreferencesManager
import com.example.data.StoryRepository
import com.example.data.models.ReminderConfig
import com.example.data.models.ReminderOffset
import com.example.data.models.StoryModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object ReminderScheduler {

  const val ACTION_REMINDER_ALARM = "com.example.cherish.ACTION_REMINDER_ALARM"
  const val EXTRA_STORY_ID = "com.example.cherish.EXTRA_STORY_ID"
  const val EXTRA_OFFSET_DAYS = "com.example.cherish.EXTRA_OFFSET_DAYS"

  /**
   * Calculates the exact trigger epoch millis for a story's reminder offset.
   * If the calculated time for the upcoming occurrence has already passed,
   * returns null (or the next annual cycle if applicable).
   */
  fun calculateNextTriggerMillis(
    story: StoryModel,
    offset: ReminderOffset,
    currentTimeMillis: Long = System.currentTimeMillis()
  ): Long? {
    val now = LocalDateTime.now(ZoneId.systemDefault())
    val today = now.toLocalDate()
    val timeHour = story.reminderConfig.timeHour.coerceIn(0, 23)
    val timeMinute = story.reminderConfig.timeMinute.coerceIn(0, 59)

    // Determine target event dates to check (this upcoming occurrence, or next year's)
    val candidateTargetDates = mutableListOf<LocalDate>()

    val eventLocalDate = story.localDate
    if (story.isPastDate) {
      // Annual recurring event
      val thisYearOccurrence = runCatching {
        eventLocalDate.withYear(today.year)
      }.getOrElse {
        // Leap year case: Feb 29 on non-leap year -> Feb 28
        LocalDate.of(today.year, 2, 28)
      }
      candidateTargetDates.add(thisYearOccurrence)

      val nextYearOccurrence = runCatching {
        eventLocalDate.withYear(today.year + 1)
      }.getOrElse {
        LocalDate.of(today.year + 1, 2, 28)
      }
      candidateTargetDates.add(nextYearOccurrence)
    } else {
      // Future event
      candidateTargetDates.add(eventLocalDate)
      // If it is also an annual celebration, add next year
      val nextYearOccurrence = runCatching {
        eventLocalDate.withYear(eventLocalDate.year + 1)
      }.getOrElse {
        LocalDate.of(eventLocalDate.year + 1, 2, 28)
      }
      candidateTargetDates.add(nextYearOccurrence)
    }

    for (targetDate in candidateTargetDates) {
      val triggerDate = targetDate.minusDays(offset.daysBefore.toLong())
      val triggerDateTime = triggerDate.atTime(timeHour, timeMinute)
      val triggerMillis = triggerDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

      if (triggerMillis > currentTimeMillis) {
        return triggerMillis
      }
    }

    return null
  }

  fun scheduleRemindersForStory(
    context: Context,
    story: StoryModel,
    remindersGloballyEnabled: Boolean = true
  ) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

    // Always cancel potential previous alarms for all possible offsets first
    cancelAllRemindersForStory(context, story.id)

    if (!remindersGloballyEnabled || !story.reminderConfig.isEnabled) {
      return
    }

    val now = System.currentTimeMillis()
    for (offset in story.reminderConfig.offsets) {
      val triggerMillis = calculateNextTriggerMillis(story, offset, now) ?: continue
      val pendingIntent = createAlarmPendingIntent(context, story.id, offset.daysBefore) ?: continue

      try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
              alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
              )
            } else {
              alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
              )
            }
          } else {
            alarmManager.setExactAndAllowWhileIdle(
              AlarmManager.RTC_WAKEUP,
              triggerMillis,
              pendingIntent
            )
          }
        } else {
          alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            pendingIntent
          )
        }
      } catch (e: SecurityException) {
        // Fallback for restricted background alarm policies
        alarmManager.set(
          AlarmManager.RTC_WAKEUP,
          triggerMillis,
          pendingIntent
        )
      }
    }
  }

  fun cancelAllRemindersForStory(context: Context, storyId: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

    // Check standard offsets + potential custom offsets range (0 to 365 days)
    val offsetsToCheck = listOf(0, 1, 2, 3, 5, 7, 10, 14, 21, 30, 60, 90)
    for (days in offsetsToCheck) {
      val pendingIntent = createAlarmPendingIntent(context, storyId, days, flag = PendingIntent.FLAG_NO_CREATE)
      if (pendingIntent != null) {
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
      }
    }
  }

  fun rescheduleAll(context: Context) {
    val storyRepository = StoryRepository(context)
    val preferencesManager = PreferencesManager(context)
    val globalRemindersEnabled = preferencesManager.settings.value.remindersEnabled
    val allStories = storyRepository.stories.value

    for (story in allStories) {
      scheduleRemindersForStory(
        context = context,
        story = story,
        remindersGloballyEnabled = globalRemindersEnabled
      )
    }
  }

  private fun createAlarmPendingIntent(
    context: Context,
    storyId: String,
    daysBefore: Int,
    flag: Int = 0
  ): PendingIntent? {
    val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
      action = ACTION_REMINDER_ALARM
      putExtra(EXTRA_STORY_ID, storyId)
      putExtra(EXTRA_OFFSET_DAYS, daysBefore)
    }

    val requestCode = (storyId.hashCode() * 31 + daysBefore)
    val defaultFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
      PendingIntent.FLAG_UPDATE_CURRENT
    }

    val combinedFlags = if (flag != 0) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flag or PendingIntent.FLAG_IMMUTABLE else flag
    } else {
      defaultFlags
    }

    return if (flag == PendingIntent.FLAG_NO_CREATE) {
      PendingIntent.getBroadcast(context, requestCode, intent, combinedFlags)
    } else {
      PendingIntent.getBroadcast(context, requestCode, intent, combinedFlags)
    }
  }
}
