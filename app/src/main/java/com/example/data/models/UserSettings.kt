package com.example.data.models

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class ThemeMode(val title: String, val description: String) {
  SYSTEM("System Default", "Follows your device settings"),
  LIGHT("Light", "Warm ivory and soft cream"),
  DARK("Dark", "Warm espresso night ambiance")
}

enum class AccentColorStyle(
  val title: String,
  val subtitle: String,
  val lightPrimaryHex: Long,
  val lightContainerHex: Long,
  val darkPrimaryHex: Long,
  val darkContainerHex: Long,
  val goldHighlightHex: Long
) {
  CHAMPAGNE_GOLD(
    title = "Champagne Gold",
    subtitle = "Warm golden elegance",
    lightPrimaryHex = 0xFFC99252,
    lightContainerHex = 0xFFFBF2E6,
    darkPrimaryHex = 0xFFE4AD70,
    darkContainerHex = 0xFF382918,
    goldHighlightHex = 0xFFC99252
  ),
  WARM_ROSE(
    title = "Warm Rose",
    subtitle = "Deep romantic rosewood",
    lightPrimaryHex = 0xFFB8644A,
    lightContainerHex = 0xFFFCEEEA,
    darkPrimaryHex = 0xFFDE8C75,
    darkContainerHex = 0xFF38231C,
    goldHighlightHex = 0xFFD4826B
  ),
  DEEP_BERRY(
    title = "Deep Berry",
    subtitle = "Rich plum and wine",
    lightPrimaryHex = 0xFF8E3E63,
    lightContainerHex = 0xFFFAEDF2,
    darkPrimaryHex = 0xFFD2769E,
    darkContainerHex = 0xFF381926,
    goldHighlightHex = 0xFFE594B8
  ),
  FOREST(
    title = "Forest Sage",
    subtitle = "Calm eucalyptus and evergreen",
    lightPrimaryHex = 0xFF3D6B56,
    lightContainerHex = 0xFFEDF5F1,
    darkPrimaryHex = 0xFF6BB08F,
    darkContainerHex = 0xFF1A2F25,
    goldHighlightHex = 0xFF88C9A8
  ),
  MIDNIGHT_BLUE(
    title = "Midnight Blue",
    subtitle = "Serene starry twilight",
    lightPrimaryHex = 0xFF355070,
    lightContainerHex = 0xFFEDF2F8,
    darkPrimaryHex = 0xFF6D94C7,
    darkContainerHex = 0xFF1A2838,
    goldHighlightHex = 0xFF8AB3E6
  );

  fun getPrimaryColor(isDark: Boolean): Color {
    return Color(if (isDark) darkPrimaryHex else lightPrimaryHex)
  }

  fun getContainerColor(isDark: Boolean): Color {
    return Color(if (isDark) darkContainerHex else lightContainerHex)
  }
}

enum class DateFormatOption(
  val label: String,
  val pattern: String,
  val example: String
) {
  MONTH_DAY_YEAR("Month Day, Year", "MMMM d, yyyy", "June 14, 2027"),
  DAY_MONTH_YEAR("Day Month Year", "d MMMM yyyy", "14 June 2027"),
  SLASH_MDY("MM/DD/YYYY", "MM/dd/yyyy", "06/14/2027"),
  SLASH_DMY("DD/MM/YYYY", "dd/MM/yyyy", "14/06/2027");

  fun format(date: LocalDate): String {
    return try {
      val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
      date.format(formatter)
    } catch (_: Exception) {
      date.toString()
    }
  }
}

enum class FirstDayOfWeekOption(val title: String) {
  SYSTEM("System Default"),
  MONDAY("Monday"),
  SUNDAY("Sunday")
}

data class UserSettings(
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val accentColorStyle: AccentColorStyle = AccentColorStyle.CHAMPAGNE_GOLD,
  val dateFormat: DateFormatOption = DateFormatOption.MONTH_DAY_YEAR,
  val firstDayOfWeek: FirstDayOfWeekOption = FirstDayOfWeekOption.SYSTEM,
  val hapticFeedbackEnabled: Boolean = true,
  val reducedAnimations: Boolean = false,
  val remindersEnabled: Boolean = true,
  val defaultReminderHour: Int = 9,
  val defaultReminderMinute: Int = 0,
  val defaultSuggestionsEnabled: Boolean = true
) {
  val smartSuggestionsEnabled: Boolean get() = defaultSuggestionsEnabled

  val formattedDefaultTime: String
    get() {
      val period = if (defaultReminderHour >= 12) "PM" else "AM"
      val h = when {
        defaultReminderHour == 0 -> 12
        defaultReminderHour > 12 -> defaultReminderHour - 12
        else -> defaultReminderHour
      }
      return "%02d:%02d %s".format(h, defaultReminderMinute, period)
    }
}
