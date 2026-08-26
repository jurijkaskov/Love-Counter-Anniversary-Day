package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent?) {
    if (intent == null) return
    val action = intent.action
    if (action == Intent.ACTION_BOOT_COMPLETED ||
      action == Intent.ACTION_MY_PACKAGE_REPLACED ||
      action == Intent.ACTION_TIMEZONE_CHANGED ||
      action == Intent.ACTION_TIME_CHANGED
    ) {
      ReminderScheduler.rescheduleAll(context)
    }
  }
}
