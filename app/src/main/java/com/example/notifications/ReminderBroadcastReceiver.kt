package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.PreferencesManager
import com.example.data.StoryRepository
import com.example.data.models.ReminderOffset

class ReminderBroadcastReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent?) {
    if (intent == null) return
    val storyId = intent.getStringExtra(ReminderScheduler.EXTRA_STORY_ID) ?: return
    val daysBefore = intent.getIntExtra(ReminderScheduler.EXTRA_OFFSET_DAYS, 0)

    val storyRepository = StoryRepository(context)
    val preferencesManager = PreferencesManager(context)
    val userSettings = preferencesManager.settings.value

    if (!userSettings.remindersEnabled) return

    val story = storyRepository.getStoryById(storyId) ?: return
    if (!story.reminderConfig.isEnabled) return

    val offset = story.reminderConfig.offsets.find { it.daysBefore == daysBefore }
      ?: ReminderOffset(daysBefore)

    // Display warm, high-priority reminder notification
    ReminderNotificationHelper.showReminderNotification(context, story, offset)

    // Reschedule next cycle if recurring
    ReminderScheduler.scheduleRemindersForStory(
      context = context,
      story = story,
      remindersGloballyEnabled = userSettings.remindersEnabled
    )
  }
}
