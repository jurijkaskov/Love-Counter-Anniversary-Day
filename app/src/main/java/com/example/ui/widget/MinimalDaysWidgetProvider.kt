package com.example.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class MinimalDaysWidgetProvider : AppWidgetProvider() {

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    WidgetUpdateHelper.updateMinimalDaysWidgets(context, appWidgetManager, appWidgetIds)
  }

  override fun onEnabled(context: Context) {
    super.onEnabled(context)
    WidgetUpdateHelper.scheduleDailyUpdate(context)
  }

  override fun onDeleted(context: Context, appWidgetIds: IntArray) {
    super.onDeleted(context, appWidgetIds)
    val preferences = WidgetPreferences(context)
    for (id in appWidgetIds) {
      preferences.removeConfig(id)
    }
  }
}
