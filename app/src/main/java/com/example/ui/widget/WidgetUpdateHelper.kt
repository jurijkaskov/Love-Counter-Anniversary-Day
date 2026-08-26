package com.example.ui.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

object WidgetUpdateHelper {

  const val ACTION_DAILY_WIDGET_UPDATE = "com.example.ui.widget.ACTION_DAILY_WIDGET_UPDATE"
  const val EXTRA_SCREEN_ROUTE = "extra_screen_route"
  const val EXTRA_STORY_ID = "extra_story_id"
  const val EXTRA_OPEN_CREATE_FLOW = "extra_open_create_flow"

  fun updateAllWidgets(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context) ?: return

    // 1. Update Main Countdown Widgets
    val mainComponent = ComponentName(context, MainCountdownWidgetProvider::class.java)
    val mainIds = appWidgetManager.getAppWidgetIds(mainComponent)
    if (mainIds.isNotEmpty()) {
      updateMainCountdownWidgets(context, appWidgetManager, mainIds)
    }

    // 2. Update Next Event Widgets
    val nextComponent = ComponentName(context, NextEventWidgetProvider::class.java)
    val nextIds = appWidgetManager.getAppWidgetIds(nextComponent)
    if (nextIds.isNotEmpty()) {
      updateNextEventWidgets(context, appWidgetManager, nextIds)
    }

    // 3. Update Minimal Days Widgets
    val minComponent = ComponentName(context, MinimalDaysWidgetProvider::class.java)
    val minIds = appWidgetManager.getAppWidgetIds(minComponent)
    if (minIds.isNotEmpty()) {
      updateMinimalDaysWidgets(context, appWidgetManager, minIds)
    }

    scheduleDailyUpdate(context)
  }

  fun updateMainCountdownWidgets(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    val preferences = WidgetPreferences(context)
    val dataManager = WidgetDataManager(context)

    for (appWidgetId in appWidgetIds) {
      val config = preferences.getConfig(appWidgetId, WidgetType.MAIN_COUNTDOWN)
      val data = dataManager.getMainCountdownData(config)
      val isDark = resolveIsDark(context, config.themePreference)

      val views: RemoteViews = if (data.isEmpty) {
        val layoutId = if (isDark) R.layout.widget_empty_dark else R.layout.widget_empty_light
        RemoteViews(context.packageName, layoutId).apply {
          setTextViewText(R.id.widget_empty_title, context.getString(R.string.widget_empty_prompt))
          val pendingIntent = createLaunchPendingIntent(context, appWidgetId, route = "countdown", openCreate = true)
          setOnClickPendingIntent(R.id.widget_root, pendingIntent)
        }
      } else {
        val layoutId = if (isDark) R.layout.widget_main_countdown_dark else R.layout.widget_main_countdown_light
        RemoteViews(context.packageName, layoutId).apply {
          setTextViewText(R.id.widget_title, data.title)
          setTextViewText(R.id.widget_date_subtitle, data.dateSubtitle)
          setTextViewText(R.id.widget_primary_count, data.count)
          setTextViewText(R.id.widget_count_label, data.countLabel)
          setTextViewText(R.id.widget_time_breakdown, data.breakdownOrNote)
          setTextViewText(R.id.widget_footer_text, data.badgeText)
          setImageViewResource(R.id.widget_icon, data.iconResId)

          val pendingIntent = createLaunchPendingIntent(context, appWidgetId, route = "countdown", storyId = data.storyId)
          setOnClickPendingIntent(R.id.widget_root, pendingIntent)
        }
      }

      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }

  fun updateNextEventWidgets(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    val preferences = WidgetPreferences(context)
    val dataManager = WidgetDataManager(context)

    for (appWidgetId in appWidgetIds) {
      val config = preferences.getConfig(appWidgetId, WidgetType.NEXT_EVENT)
      val data = dataManager.getNextEventData(config)
      val isDark = resolveIsDark(context, config.themePreference)

      val views: RemoteViews = if (data.isEmpty) {
        val layoutId = if (isDark) R.layout.widget_empty_dark else R.layout.widget_empty_light
        RemoteViews(context.packageName, layoutId).apply {
          setTextViewText(R.id.widget_empty_title, context.getString(R.string.widget_empty_prompt))
          val pendingIntent = createLaunchPendingIntent(context, appWidgetId, route = "moments", openCreate = true)
          setOnClickPendingIntent(R.id.widget_root, pendingIntent)
        }
      } else {
        val layoutId = if (isDark) R.layout.widget_next_event_dark else R.layout.widget_next_event_light
        RemoteViews(context.packageName, layoutId).apply {
          setTextViewText(R.id.widget_event_title, data.title)
          setTextViewText(R.id.widget_event_date, data.dateSubtitle)
          setTextViewText(R.id.widget_countdown_pill, data.badgeText)
          setTextViewText(R.id.widget_event_note, data.breakdownOrNote)
          setImageViewResource(R.id.widget_event_icon, data.iconResId)

          val pendingIntent = createLaunchPendingIntent(context, appWidgetId, route = "moments", storyId = data.storyId)
          setOnClickPendingIntent(R.id.widget_root, pendingIntent)
        }
      }

      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }

  fun updateMinimalDaysWidgets(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    val preferences = WidgetPreferences(context)
    val dataManager = WidgetDataManager(context)

    for (appWidgetId in appWidgetIds) {
      val config = preferences.getConfig(appWidgetId, WidgetType.MINIMAL_DAYS)
      val data = dataManager.getMinimalDaysData(config)
      val isDark = resolveIsDark(context, config.themePreference)

      val views: RemoteViews = if (data.isEmpty) {
        val layoutId = if (isDark) R.layout.widget_empty_dark else R.layout.widget_empty_light
        RemoteViews(context.packageName, layoutId).apply {
          setTextViewText(R.id.widget_empty_title, context.getString(R.string.widget_empty_prompt))
          val pendingIntent = createLaunchPendingIntent(context, appWidgetId, route = "countdown", openCreate = true)
          setOnClickPendingIntent(R.id.widget_root, pendingIntent)
        }
      } else {
        val layoutId = if (isDark) R.layout.widget_minimal_days_dark else R.layout.widget_minimal_days_light
        RemoteViews(context.packageName, layoutId).apply {
          setTextViewText(R.id.widget_minimal_count, data.count)
          setTextViewText(R.id.widget_minimal_label, data.countLabel)
          setImageViewResource(R.id.widget_minimal_icon, data.iconResId)

          val pendingIntent = createLaunchPendingIntent(context, appWidgetId, route = "countdown", storyId = data.storyId)
          setOnClickPendingIntent(R.id.widget_root, pendingIntent)
        }
      }

      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }

  fun scheduleDailyUpdate(context: Context) {
    try {
      val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
      val intent = Intent(context, WidgetUpdateReceiver::class.java).apply {
        action = ACTION_DAILY_WIDGET_UPDATE
      }
      val pendingIntent = PendingIntent.getBroadcast(
        context,
        9001,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      // Calculate next midnight + 2 seconds
      val now = LocalDateTime.now()
      val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay().plusSeconds(2)
      val triggerMillis = nextMidnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

      alarmManager.setAndAllowWhileIdle(
        AlarmManager.RTC,
        triggerMillis,
        pendingIntent
      )
    } catch (e: Exception) {
      // Ignored for safety on restricted devices
    }
  }

  private fun resolveIsDark(context: Context, themePreference: WidgetThemePreference): Boolean {
    return when (themePreference) {
      WidgetThemePreference.LIGHT -> false
      WidgetThemePreference.DARK -> true
      WidgetThemePreference.SYSTEM -> {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        nightModeFlags == Configuration.UI_MODE_NIGHT_YES
      }
    }
  }

  private fun createLaunchPendingIntent(
    context: Context,
    appWidgetId: Int,
    route: String,
    storyId: String? = null,
    openCreate: Boolean = false
  ): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra(EXTRA_SCREEN_ROUTE, route)
      if (storyId != null) {
        putExtra(EXTRA_STORY_ID, storyId)
      }
      if (openCreate) {
        putExtra(EXTRA_OPEN_CREATE_FLOW, true)
      }
    }
    return PendingIntent.getActivity(
      context,
      appWidgetId,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
  }
}
