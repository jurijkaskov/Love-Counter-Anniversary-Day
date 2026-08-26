package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.models.ReminderOffset
import com.example.data.models.StoryModel

object ReminderNotificationHelper {

  const val CHANNEL_ID = "cherish_smart_reminders_channel"
  const val CHANNEL_NAME = "Cherished Moments & Anniversaries"
  const val CHANNEL_DESCRIPTION = "Gentle reminders for upcoming anniversaries, birthdays, and special milestones."

  const val EXTRA_STORY_ID = "com.example.cherish.EXTRA_STORY_ID"
  const val EXTRA_OFFSET_DAYS = "com.example.cherish.EXTRA_OFFSET_DAYS"

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

      val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
      if (existingChannel == null) {
        val channel = NotificationChannel(
          CHANNEL_ID,
          CHANNEL_NAME,
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          description = CHANNEL_DESCRIPTION
          enableLights(true)
          lightColor = Color.parseColor("#C69255") // Champagne gold
          enableVibration(true)
          setShowBadge(true)
        }
        notificationManager.createNotificationChannel(channel)
      }
    }
  }

  fun showReminderNotification(
    context: Context,
    story: StoryModel,
    offset: ReminderOffset
  ) {
    createNotificationChannel(context)

    // Check permission on Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(
          context,
          android.Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }

    val (title, body) = story.reminderConfig.getNotificationContent(story, offset)

    // Tap intent opening MainActivity with specific story
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_STORY_ID, story.id)
      putExtra(EXTRA_OFFSET_DAYS, offset.daysBefore)
    }

    val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    } else {
      PendingIntent.FLAG_UPDATE_CURRENT
    }

    val requestCode = (story.id.hashCode() * 31 + offset.daysBefore)
    val pendingIntent = PendingIntent.getActivity(
      context,
      requestCode,
      intent,
      pendingIntentFlags
    )

    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(body)
      .setStyle(NotificationCompat.BigTextStyle().bigText(body))
      .setColor(0xFF7D1B2D.toInt()) // Rosewood
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_REMINDER)
      .setContentIntent(pendingIntent)

    val notificationId = (story.id.hashCode() * 31 + offset.daysBefore)

    try {
      NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
    } catch (e: SecurityException) {
      // Permission denied at runtime
    }
  }
}
