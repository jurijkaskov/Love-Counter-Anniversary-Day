package com.example.ui.widget

enum class WidgetType(val id: String, val defaultTitle: String) {
  MAIN_COUNTDOWN("main_countdown", "Cherish Countdown"),
  NEXT_EVENT("next_event", "Next Moment"),
  MINIMAL_DAYS("minimal_days", "Minimal Days");

  companion object {
    fun fromId(id: String?): WidgetType {
      return entries.find { it.id == id } ?: MAIN_COUNTDOWN
    }
  }
}

enum class WidgetThemePreference(val id: String, val title: String) {
  LIGHT("light", "Warm Ivory (Light)"),
  DARK("dark", "Espresso Night (Dark)"),
  SYSTEM("system", "Follow Device Theme");

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
