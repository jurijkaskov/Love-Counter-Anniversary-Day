package com.example.ui.navigation

import com.example.R

sealed class CherishScreen(
  val route: String,
  val titleResId: Int,
  val selectedIconResId: Int,
  val unselectedIconResId: Int
) {
  data object Countdown : CherishScreen(
    route = "countdown",
    titleResId = R.string.nav_countdown,
    selectedIconResId = R.drawable.ic_nav_countdown_filled,
    unselectedIconResId = R.drawable.ic_nav_countdown
  )

  data object Moments : CherishScreen(
    route = "moments",
    titleResId = R.string.nav_moments,
    selectedIconResId = R.drawable.ic_nav_moments_filled,
    unselectedIconResId = R.drawable.ic_nav_moments
  )

  data object Settings : CherishScreen(
    route = "settings",
    titleResId = R.string.nav_settings,
    selectedIconResId = R.drawable.ic_nav_settings_filled,
    unselectedIconResId = R.drawable.ic_nav_settings
  )

  companion object {
    val items: List<CherishScreen>
      get() = listOf(Countdown, Moments, Settings)
  }
}

