package com.example.ui.widget

import com.example.R

enum class WidgetType(val id: String, val titleResId: Int) {
  MAIN_COUNTDOWN("main_countdown", R.string.widget_type_countdown),
  NEXT_EVENT("next_event", R.string.widget_type_next_event),
  MINIMAL_DAYS("minimal_days", R.string.widget_type_minimal);

  companion object {
    fun fromId(id: String?): WidgetType {
      return entries.find { it.id == id } ?: MAIN_COUNTDOWN
    }
  }
}

enum class WidgetThemePreference(val id: String, val titleResId: Int) {
  LIGHT("light", R.string.widget_config_theme_light),
  DARK("dark", R.string.widget_config_theme_dark),
  SYSTEM("system", R.string.widget_config_theme_system);

  companion object {
    fun fromId(id: String?): WidgetThemePreference {
      return entries.find { it.id == id } ?: SYSTEM
    }
  }
}

data class WidgetConfig(
  val appWidgetId: Int,
  val widgetType: WidgetType,
  val targetStoryId: String? = null,
  val autoNextEvent: Boolean = true,
  val themePreference: WidgetThemePreference = WidgetThemePreference.SYSTEM
)

data class WidgetDisplayData(
  val title: String,
  val count: String,
  val countLabel: String,
  val breakdownOrNote: String,
  val dateSubtitle: String,
  val badgeText: String,
  val iconResId: Int,
  val storyId: String? = null,
  val isPast: Boolean = true,
  val isEmpty: Boolean = false,
  val targetScreenRoute: String = "countdown"
)
