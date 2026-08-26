package com.example

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.OnboardingManager
import com.example.data.StoryRepository
import com.example.ui.CherishApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val onboardingManager = OnboardingManager(context)
    val storyRepository = StoryRepository(context)
    onboardingManager.resetOnboarding()
    storyRepository.resetAll()
    context.getSharedPreferences("demo_data_internal_prefs", Context.MODE_PRIVATE).edit().clear().apply()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Love Counter: Anniversary Day", appName)
  }

  @Test
  fun `onboarding flow leads into first story creation flow`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val onboardingManager = OnboardingManager(context)
    val storyRepository = StoryRepository(context)
    onboardingManager.resetOnboarding()
    storyRepository.resetAll()

    var onboardingFinished by androidx.compose.runtime.mutableStateOf(false)
    var storyCreationFinished by androidx.compose.runtime.mutableStateOf(false)

    composeTestRule.setContent {
      com.example.ui.theme.CherishTheme {
        if (!onboardingFinished) {
          com.example.ui.screens.onboarding.OnboardingScreen(
            onComplete = {
              onboardingManager.hasCompletedOnboarding = true
              onboardingFinished = true
            }
          )
        } else {
          com.example.ui.screens.create.CreateStoryFlowScreen(
            storyRepository = storyRepository,
            onStoryCreated = { storyCreationFinished = true },
            onCancel = { storyCreationFinished = true }
          )
        }
      }
    }

    // Step 1: Welcome is displayed
    composeTestRule.onNodeWithTag("onboarding_page_0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("onboarding_primary_button").assertIsDisplayed()

    // Advance to Step 2
    composeTestRule.onNodeWithTag("onboarding_primary_button").performClick()
    composeTestRule.waitForIdle()

    // Advance to Step 3
    composeTestRule.onNodeWithTag("onboarding_primary_button").performClick()
    composeTestRule.waitForIdle()

    // Complete Onboarding -> launches First Story Guided Setup
    composeTestRule.onNodeWithTag("onboarding_primary_button").performClick()
    composeTestRule.waitForIdle()
    assertTrue(onboardingFinished)

    // First story flow is now displayed on Step 1: Category selection
    composeTestRule.onNodeWithTag("create_step_1_container").assertIsDisplayed()
    composeTestRule.onNodeWithTag("category_option_relationship").assertIsDisplayed()

    // Advance to Step 2: Names & Title
    composeTestRule.onNodeWithTag("create_flow_next_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("create_step_2_container").assertIsDisplayed()

    // Enter names
    composeTestRule.onNodeWithTag("input_your_name_field").performTextInput("Eleanor")
    composeTestRule.onNodeWithTag("input_partner_name_field").performTextInput("Julian")
    composeTestRule.waitForIdle()

    // Advance to Step 3: Date
    composeTestRule.onNodeWithTag("create_flow_next_button").performClick()
    composeTestRule.mainClock.advanceTimeBy(1000)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("create_step_3_container").assertIsDisplayed()

    // Advance to Step 4: Personal Touch
    composeTestRule.onNodeWithTag("create_flow_next_button").performClick()
    composeTestRule.mainClock.advanceTimeBy(1000)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("create_step_4_container").assertIsDisplayed()

    // Advance to Step 5: Preview
    composeTestRule.onNodeWithTag("create_flow_next_button").performClick()
    composeTestRule.mainClock.advanceTimeBy(1000)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("create_step_5_container").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmation_preview_card").assertIsDisplayed()

    // Complete flow and save story
    composeTestRule.onNodeWithTag("create_flow_next_button").performClick()
    composeTestRule.mainClock.advanceTimeBy(1000)
    composeTestRule.waitForIdle()

    assertTrue(onboardingManager.hasCompletedOnboarding)
    assertTrue(storyRepository.hasAnyStory())
    assertTrue(storyCreationFinished)
  }
}
