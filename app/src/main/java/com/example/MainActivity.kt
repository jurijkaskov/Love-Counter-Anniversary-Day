package com.example

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.example.ui.CherishApp
import com.example.ui.widget.WidgetUpdateHelper

class MainActivity : ComponentActivity() {

  private val initialRouteState = mutableStateOf<String?>(null)
  private val initialStoryIdState = mutableStateOf<String?>(null)
  private val initialOpenCreateState = mutableStateOf(false)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    enableEdgeToEdge()
    handleWidgetIntent(intent)

    setContent {
      CherishApp(
        initialRoute = initialRouteState.value,
        initialStoryId = initialStoryIdState.value,
        initialOpenCreate = initialOpenCreateState.value
      )
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleWidgetIntent(intent)
  }

  private fun handleWidgetIntent(intent: Intent?) {
    if (intent == null) return
    val route = intent.getStringExtra(WidgetUpdateHelper.EXTRA_SCREEN_ROUTE)
    val storyId = intent.getStringExtra(WidgetUpdateHelper.EXTRA_STORY_ID)
    val openCreate = intent.getBooleanExtra(WidgetUpdateHelper.EXTRA_OPEN_CREATE_FLOW, false)

    if (route != null) {
      initialRouteState.value = route
    }
    if (storyId != null) {
      initialStoryIdState.value = storyId
    }
    if (openCreate) {
      initialOpenCreateState.value = true
    }
  }
}


