package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.AccentColorStyle
import com.example.data.models.DateFormatOption
import com.example.data.models.FirstDayOfWeekOption
import com.example.data.models.ThemeMode
import com.example.data.models.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

class PreferencesManager(private val context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("cherish_user_preferences", Context.MODE_PRIVATE)

  private val _settings = MutableStateFlow(loadSettings())
  val settings: StateFlow<UserSettings> = _settings.asStateFlow()

  private fun loadSettings(): UserSettings {
    val themeName = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
    val accentName = prefs.getString(KEY_ACCENT_STYLE, AccentColorStyle.CHAMPAGNE_GOLD.name) ?: AccentColorStyle.CHAMPAGNE_GOLD.name
    val dateFormatName = prefs.getString(KEY_DATE_FORMAT, DateFormatOption.MONTH_DAY_YEAR.name) ?: DateFormatOption.MONTH_DAY_YEAR.name
    val firstDayName = prefs.getString(KEY_FIRST_DAY_OF_WEEK, FirstDayOfWeekOption.SYSTEM.name) ?: FirstDayOfWeekOption.SYSTEM.name
    val haptics = prefs.getBoolean(KEY_HAPTICS, true)
    val reducedMotion = prefs.getBoolean(KEY_REDUCED_MOTION, false)
    val reminders = prefs.getBoolean(KEY_REMINDERS, true)
    val reminderHour = prefs.getInt(KEY_REMINDER_HOUR, 9)
    val reminderMinute = prefs.getInt(KEY_REMINDER_MINUTE, 0)
    val suggestions = prefs.getBoolean(KEY_REMINDER_SUGGESTIONS, true)

    val themeMode = try { ThemeMode.valueOf(themeName) } catch (_: Exception) { ThemeMode.SYSTEM }
    val accentStyle = try { AccentColorStyle.valueOf(accentName) } catch (_: Exception) { AccentColorStyle.CHAMPAGNE_GOLD }
    val dateFormat = try { DateFormatOption.valueOf(dateFormatName) } catch (_: Exception) { DateFormatOption.MONTH_DAY_YEAR }
    val firstDayOfWeek = try { FirstDayOfWeekOption.valueOf(firstDayName) } catch (_: Exception) { FirstDayOfWeekOption.SYSTEM }

    return UserSettings(
      themeMode = themeMode,
      accentColorStyle = accentStyle,
      dateFormat = dateFormat,
      firstDayOfWeek = firstDayOfWeek,
      hapticFeedbackEnabled = haptics,
      reducedAnimations = reducedMotion,
      remindersEnabled = reminders,
      defaultReminderHour = reminderHour,
      defaultReminderMinute = reminderMinute,
      defaultSuggestionsEnabled = suggestions
    )
  }

  fun setThemeMode(mode: ThemeMode) {
    prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    _settings.value = _settings.value.copy(themeMode = mode)
  }

  fun setAccentColorStyle(style: AccentColorStyle) {
    prefs.edit().putString(KEY_ACCENT_STYLE, style.name).apply()
    _settings.value = _settings.value.copy(accentColorStyle = style)
  }

  fun setDateFormat(option: DateFormatOption) {
    prefs.edit().putString(KEY_DATE_FORMAT, option.name).apply()
    _settings.value = _settings.value.copy(dateFormat = option)
  }

  fun setFirstDayOfWeek(option: FirstDayOfWeekOption) {
    prefs.edit().putString(KEY_FIRST_DAY_OF_WEEK, option.name).apply()
    _settings.value = _settings.value.copy(firstDayOfWeek = option)
  }

  fun setHapticFeedbackEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
    _settings.value = _settings.value.copy(hapticFeedbackEnabled = enabled)
  }

  fun setReducedAnimations(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_REDUCED_MOTION, enabled).apply()
    _settings.value = _settings.value.copy(reducedAnimations = enabled)
  }

  fun setRemindersEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_REMINDERS, enabled).apply()
    _settings.value = _settings.value.copy(remindersEnabled = enabled)
  }

  fun setDefaultReminderTime(hour: Int, minute: Int) {
    prefs.edit()
      .putInt(KEY_REMINDER_HOUR, hour.coerceIn(0, 23))
      .putInt(KEY_REMINDER_MINUTE, minute.coerceIn(0, 59))
      .apply()
    _settings.value = _settings.value.copy(defaultReminderHour = hour, defaultReminderMinute = minute)
  }

  fun setDefaultSuggestionsEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_REMINDER_SUGGESTIONS, enabled).apply()
    _settings.value = _settings.value.copy(defaultSuggestionsEnabled = enabled)
  }

  fun setSmartSuggestionsEnabled(enabled: Boolean) {
    setDefaultSuggestionsEnabled(enabled)
  }

  /**
   * Generates a complete JSON backup and export of stories, milestones, tasks, and settings.
   */
  fun exportAllDataJson(
    storyRepository: StoryRepository,
    milestoneRepository: MilestoneRepository
  ): String {
    val root = JSONObject()
    root.put("appName", "Love Counter: Anniversary Day")
    root.put("version", "1.0.0")
    root.put("exportedAt", Instant.now().toString())

    // Settings
    val currentSettings = _settings.value
    val settingsObj = JSONObject().apply {
      put("themeMode", currentSettings.themeMode.name)
      put("accentColorStyle", currentSettings.accentColorStyle.name)
      put("dateFormat", currentSettings.dateFormat.name)
      put("firstDayOfWeek", currentSettings.firstDayOfWeek.name)
      put("hapticFeedbackEnabled", currentSettings.hapticFeedbackEnabled)
      put("reducedAnimations", currentSettings.reducedAnimations)
      put("remindersEnabled", currentSettings.remindersEnabled)
      put("defaultReminderHour", currentSettings.defaultReminderHour)
      put("defaultReminderMinute", currentSettings.defaultReminderMinute)
      put("defaultSuggestionsEnabled", currentSettings.defaultSuggestionsEnabled)
    }
    root.put("settings", settingsObj)

    // Stories & Moments
    val storiesArray = JSONArray()
    storyRepository.stories.value.forEach { story ->
      val sObj = JSONObject().apply {
        put("id", story.id)
        put("title", story.title)
        put("yourName", story.yourName)
        put("partnerName", story.partnerName)
        put("dateEpochDay", story.dateEpochDay)
        put("note", story.note)
        put("category", story.category.name)
        put("iconKey", story.iconKey)
        put("themeAccent", story.themeAccent)
        put("isPrimary", story.isPrimary)
        put("isFavorite", story.isFavorite)
        put("reminderConfig", story.reminderConfig.toJson())
        put("createdAtEpochMillis", story.createdAtEpochMillis)
      }
      storiesArray.put(sObj)
    }
    root.put("stories", storiesArray)

    // Milestones
    val milestonesArray = JSONArray()
    milestoneRepository.milestones.value.forEach { milestone ->
      val mObj = JSONObject().apply {
        put("id", milestone.id)
        put("title", milestone.title)
        put("description", milestone.description)
        put("category", milestone.category.name)
        put("targetDateEpochDay", milestone.targetDateEpochDay)
        put("associatedStoryId", milestone.associatedStoryId)
        put("timeframeLabel", milestone.timeframeLabel)
      }
      milestonesArray.put(mObj)
    }
    root.put("milestones", milestonesArray)

    // Milestone Tasks
    val tasksArray = JSONArray()
    milestoneRepository.tasks.value.forEach { task ->
      val tObj = JSONObject().apply {
        put("id", task.id)
        put("milestoneId", task.milestoneId)
        put("title", task.title)
        put("note", task.note)
        put("dueDateEpochDay", task.dueDateEpochDay)
        put("isCompleted", task.isCompleted)
        put("orderIndex", task.orderIndex)
      }
      tasksArray.put(tObj)
    }
    root.put("tasks", tasksArray)

    return root.toString(2)
  }

  /**
   * Resets all stored application data including stories, milestones, and preferences.
   */
  fun resetAllApplicationData(
    storyRepository: StoryRepository,
    milestoneRepository: MilestoneRepository
  ) {
    // Clear stories
    val stories = storyRepository.stories.value
    stories.forEach { storyRepository.deleteStory(it.id) }

    // Clear milestones & tasks
    val milestones = milestoneRepository.milestones.value
    milestones.forEach { milestoneRepository.deleteMilestone(it.id) }

    // Reset settings to defaults
    prefs.edit().clear().apply()
    _settings.value = UserSettings()
  }

  companion object {
    private const val KEY_THEME_MODE = "pref_theme_mode"
    private const val KEY_ACCENT_STYLE = "pref_accent_style"
    private const val KEY_DATE_FORMAT = "pref_date_format"
    private const val KEY_FIRST_DAY_OF_WEEK = "pref_first_day_of_week"
    private const val KEY_HAPTICS = "pref_haptics"
    private const val KEY_REDUCED_MOTION = "pref_reduced_motion"
    private const val KEY_REMINDERS = "pref_reminders"
    private const val KEY_REMINDER_HOUR = "pref_reminder_hour"
    private const val KEY_REMINDER_MINUTE = "pref_reminder_minute"
    private const val KEY_REMINDER_SUGGESTIONS = "pref_reminder_suggestions"
  }
}
