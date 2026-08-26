package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.JournalRepository
import com.example.data.MilestoneRepository
import com.example.data.OnboardingManager
import com.example.data.PhotoRepository
import com.example.data.PreferencesManager
import com.example.data.StoryRepository
import com.example.data.demo.DemoDataProvider
import com.example.ui.navigation.CherishBottomBar
import com.example.ui.navigation.CherishScreen
import com.example.ui.screens.CountdownScreen
import com.example.ui.screens.MomentsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.create.CreateStoryFlowScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.theme.CherishTheme

import androidx.compose.runtime.LaunchedEffect

@Composable
fun CherishApp(
  initialRoute: String? = null,
  initialStoryId: String? = null,
  initialOpenCreate: Boolean = false
) {
  val context = LocalContext.current
  val preferencesManager = remember { PreferencesManager(context) }
  val onboardingManager = remember { OnboardingManager(context) }
  val storyRepository = remember { StoryRepository(context) }
  val milestoneRepository = remember { MilestoneRepository(context, storyRepository) }
  val journalRepository = remember { JournalRepository(context) }
  val photoRepository = remember { PhotoRepository(context) }

  remember {
    DemoDataProvider.populateIfEnabled(
      context = context,
      storyRepository = storyRepository,
      milestoneRepository = milestoneRepository,
      journalRepository = journalRepository,
      photoRepository = photoRepository,
      onboardingManager = onboardingManager
    )
  }

  val userSettings by preferencesManager.settings.collectAsState()
  val primaryStory by storyRepository.primaryStory.collectAsState()

  var currentRoute by rememberSaveable { mutableStateOf(initialRoute ?: CherishScreen.Countdown.route) }
  var momentsInitialTab by rememberSaveable { mutableStateOf(com.example.ui.screens.MomentsTabCategory.ALL) }
  
  var showOnboarding by rememberSaveable {
    mutableStateOf(!onboardingManager.hasCompletedOnboarding)
  }

  var showCreateStoryFlow by rememberSaveable {
    mutableStateOf(initialOpenCreate)
  }

  // Synchronize when new intent comes in from widgets
  LaunchedEffect(initialRoute) {
    if (!initialRoute.isNullOrBlank()) {
      currentRoute = initialRoute
    }
  }

  LaunchedEffect(initialStoryId) {
    if (!initialStoryId.isNullOrBlank()) {
      val found = storyRepository.getStoryById(initialStoryId)
      if (found != null && !found.isPrimary) {
        // Switch view to this story if needed or keep primary
      }
    }
  }

  LaunchedEffect(initialOpenCreate) {
    if (initialOpenCreate) {
      showCreateStoryFlow = true
    }
  }

  // Handle system back gesture
  BackHandler(enabled = showOnboarding && onboardingManager.hasCompletedOnboarding) {
    showOnboarding = false
  }

  BackHandler(enabled = showCreateStoryFlow && storyRepository.hasAnyStory()) {
    showCreateStoryFlow = false
  }

  BackHandler(enabled = !showOnboarding && !showCreateStoryFlow && currentRoute != CherishScreen.Countdown.route) {
    currentRoute = CherishScreen.Countdown.route
  }

  CherishTheme(
    themeMode = userSettings.themeMode,
    accentStyle = userSettings.accentColorStyle
  ) {
    when {
      showOnboarding -> {
        OnboardingScreen(
          onComplete = {
            onboardingManager.hasCompletedOnboarding = true
            showOnboarding = false
            // Seamless transition: Onboarding -> Create First Story Flow
            showCreateStoryFlow = true
          }
        )
      }
      showCreateStoryFlow -> {
        CreateStoryFlowScreen(
          storyRepository = storyRepository,
          onStoryCreated = {
            showCreateStoryFlow = false
            currentRoute = CherishScreen.Countdown.route
          },
          onCancel = if (storyRepository.hasAnyStory()) {
            { showCreateStoryFlow = false }
          } else null
        )
      }
      else -> {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          contentWindowInsets = WindowInsets(0, 0, 0, 0),
          bottomBar = {
            CherishBottomBar(
              currentRoute = currentRoute,
              onNavigateTo = { destination ->
                currentRoute = destination.route
              }
            )
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            val animationDuration = if (userSettings.reducedAnimations) 0 else 180
            val exitDuration = if (userSettings.reducedAnimations) 0 else 140

            AnimatedContent(
              targetState = currentRoute,
              transitionSpec = {
                fadeIn(
                  animationSpec = tween(animationDuration, easing = FastOutSlowInEasing)
                ) togetherWith fadeOut(
                  animationSpec = tween(exitDuration, easing = FastOutSlowInEasing)
                )
              },
              label = "cherish_screen_transition"
            ) { route ->
              when (route) {
                CherishScreen.Countdown.route -> CountdownScreen(
                  primaryStory = primaryStory,
                  milestoneRepository = milestoneRepository,
                  onCreateStoryClick = { showCreateStoryFlow = true },
                  onNavigateToMoments = {
                    momentsInitialTab = com.example.ui.screens.MomentsTabCategory.ALL
                    currentRoute = CherishScreen.Moments.route
                  },
                  onNavigateToMilestones = {
                    momentsInitialTab = com.example.ui.screens.MomentsTabCategory.MILESTONES
                    currentRoute = CherishScreen.Moments.route
                  }
                )
                CherishScreen.Moments.route -> MomentsScreen(
                  storyRepository = storyRepository,
                  milestoneRepository = milestoneRepository,
                  journalRepository = journalRepository,
                  photoRepository = photoRepository,
                  preferencesManager = preferencesManager,
                  initialTab = momentsInitialTab,
                  onAddMoment = { showCreateStoryFlow = true }
                )
                CherishScreen.Settings.route -> SettingsScreen(
                  preferencesManager = preferencesManager,
                  storyRepository = storyRepository,
                  milestoneRepository = milestoneRepository,
                  journalRepository = journalRepository,
                  photoRepository = photoRepository,
                  primaryStory = primaryStory,
                  onCreateStory = { showCreateStoryFlow = true },
                  onReplayOnboarding = { showOnboarding = true }
                )
                else -> CountdownScreen(
                  primaryStory = primaryStory,
                  onCreateStoryClick = { showCreateStoryFlow = true }
                )
              }
            }
          }
        }
      }
    }
  }
}
