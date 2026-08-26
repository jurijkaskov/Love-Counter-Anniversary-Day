package com.example.data

import android.content.Context
import android.content.SharedPreferences

class OnboardingManager(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  var hasCompletedOnboarding: Boolean
    get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

  fun resetOnboarding() {
    prefs.edit().remove(KEY_ONBOARDING_COMPLETED).apply()
  }

  companion object {
    private const val PREFS_NAME = "cherish_prefs"
    private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
  }
}
