package com.example.ui.widget

import android.content.Context
import android.content.SharedPreferences

class WidgetPreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  fun saveConfig(config: WidgetConfig) {
    prefs.edit()
      .putString("${KEY_TYPE_PREFIX}${config.appWidgetId}", config.widgetType.id)
      .putString("${KEY_STORY_PREFIX}${config.appWidgetId}", config.targetStoryId)
      .putBoolean("${KEY_AUTO_NEXT_PREFIX}${config.appWidgetId}", config.autoNextEvent)
      .putString("${KEY_THEME_PREFIX}${config.appWidgetId}", config.themePreference.id)
      .apply()
  }

  fun getConfig(appWidgetId: Int, defaultType: WidgetType): WidgetConfig {
    val typeId = prefs.getString("${KEY_TYPE_PREFIX}$appWidgetId", defaultType.id)
    val storyId = prefs.getString("${KEY_STORY_PREFIX}$appWidgetId", null)
    val autoNext = prefs.getBoolean("${KEY_AUTO_NEXT_PREFIX}$appWidgetId", defaultType == WidgetType.NEXT_EVENT)
    val themeId = prefs.getString("${KEY_THEME_PREFIX}$appWidgetId", WidgetThemePreference.SYSTEM.id)

    return WidgetConfig(
      appWidgetId = appWidgetId,
      widgetType = WidgetType.fromId(typeId),
      targetStoryId = storyId,
      autoNextEvent = autoNext,
      themePreference = WidgetThemePreference.fromId(themeId)
    )
  }

  fun removeConfig(appWidgetId: Int) {
    prefs.edit()
      .remove("${KEY_TYPE_PREFIX}$appWidgetId")
      .remove("${KEY_STORY_PREFIX}$appWidgetId")
      .remove("${KEY_AUTO_NEXT_PREFIX}$appWidgetId")
      .remove("${KEY_THEME_PREFIX}$appWidgetId")
      .apply()
  }

  companion object {
    private const val PREFS_NAME = "cherish_widget_prefs"
    private const val KEY_TYPE_PREFIX = "widget_type_"
    private const val KEY_STORY_PREFIX = "widget_story_id_"
    private const val KEY_AUTO_NEXT_PREFIX = "widget_auto_next_"
    private const val KEY_THEME_PREFIX = "widget_theme_"
  }
}
