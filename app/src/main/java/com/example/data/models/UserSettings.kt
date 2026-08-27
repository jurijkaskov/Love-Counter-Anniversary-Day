package com.example.data.models

import com.example.R
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class ThemeMode(val titleResId: Int, val descResId: Int) {
  SYSTEM(R.string.theme_mode_system, R.string.theme_mode_system_desc),
  LIGHT(R.string.theme_mode_light, R.string.theme_mode_light_desc),
  DARK(R.string.theme_mode_dark, R.string.theme_mode_dark_desc)
}

enum class AccentColorStyle(
  val titleResId: Int,
  val subtitleResId: Int,
  val lightPrimaryHex: Long,
  val lightContainerHex: Long,
  val darkPrimaryHex: Long,
  val darkContainerHex: Long,
  val goldHighlightHex: Long
) {
  CHAMPAGNE_GOLD(
    titleResId = R.string.accent_gold_title,
    subtitleResId = R.string.accent_gold_sub,
    lightPrimaryHex = 0xFFC99252,
    lightContainerHex = 0xFFFBF2E6,
    darkPrimaryHex = 0xFFE4AD70,
    darkContainerHex = 0xFF382918,
    goldHighlightHex = 0xFFC99252
  ),
  WARM_ROSE(
    titleResId = R.string.accent_rose_title,
    subtitleResId = R.string.accent_rose_sub,
    lightPrimaryHex = 0xFFB8644A,
    lightContainerHex = 0xFFFCEEEA,
    darkPrimaryHex = 0xFFDE8C75,
    darkContainerHex = 0xFF38231C,
    goldHighlightHex = 0xFFD4826B
  ),
  DEEP_BERRY(
    titleResId = R.string.accent_berry_title,
    subtitleResId = R.string.accent_berry_sub,
    lightPrimaryHex = 0xFF8E3E63,
    lightContainerHex = 0xFFFAEDF2,
    darkPrimaryHex = 0xFFD2769E,
    darkContainerHex = 0xFF381926,
    goldHighlightHex = 0xFFE594B8
  ),
  FOREST(
    titleResId = R.string.accent_forest_title,
    subtitleResId = R.string.accent_forest_sub,
    lightPrimaryHex = 0xFF3D6B56,
    lightContainerHex = 0xFFEDF5F1,
    darkPrimaryHex = 0xFF6BB08F,
    darkContainerHex = 0xFF1A2F25,
    goldHighlightHex = 0xFF88C9A8
  ),
  MIDNIGHT_BLUE(
    titleResId = R.string.accent_midnight_title,
    subtitleResId = R.string.accent_midnight_sub,
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
  val labelResId: Int,
  val pattern: String,
  val example: String
) {
  MONTH_DAY_YEAR(R.string.date_format_mdy, "MMMM d, yyyy", "June 14, 2027"),
  DAY_MONTH_YEAR(R.string.date_format_dmy, "d MMMM yyyy", "14 June 2027"),
  SLASH_MDY(R.string.date_format_slash_mdy, "MM/dd/yyyy", "06/14/2027"),
  SLASH_DMY(R.string.date_format_slash_dmy, "dd/MM/yyyy", "14/06/2027");

  fun format(date: LocalDate): String {
    return try {
      val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
      date.format(formatter)
    } catch (_: Exception) {
      date.toString()
    }
  }
}

enum class FirstDayOfWeekOption(val titleResId: Int) {
  SYSTEM(R.string.first_day_system),
  MONDAY(R.string.first_day_monday),
  SUNDAY(R.string.first_day_sunday)
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
