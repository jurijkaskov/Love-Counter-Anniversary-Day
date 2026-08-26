package com.example.data.demo

/**
 * SCREENSHOT / DEMO DATA MODE CONFIGURATION
 *
 * • When `USE_DEMO_DATA = true`:
 *   Automatically populates the app with realistic predefined demo data on first
 *   installation / first launch. This data is intended for creating Google Play
 *   screenshots and makes all important screens look complete, attractive, and
 *   populated with realistic content.
 *
 * • When `USE_DEMO_DATA = false`:
 *   The app behaves normally as a clean production installation with zero fake or
 *   predefined user data.
 *
 * Note: There is no visible setting or toggle inside the app UI; this flag is for
 * development and build configuration only.
 */
object DemoConfig {
  const val USE_DEMO_DATA = false
}
