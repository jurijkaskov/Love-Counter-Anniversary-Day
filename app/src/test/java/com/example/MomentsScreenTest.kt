package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.StoryRepository
import com.example.ui.screens.MomentsScreen
import com.example.ui.theme.CherishTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MomentsScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var storyRepository: StoryRepository

  @Before
  fun setUp() {
    val context: Context = ApplicationProvider.getApplicationContext()
    storyRepository = StoryRepository(context)
    storyRepository.seedDefaultStoryIfEmpty()
  }

  @Test
  fun `moments screen renders header and filter chips`() {
    composeTestRule.setContent {
      CherishTheme {
        MomentsScreen(storyRepository = storyRepository)
      }
    }

    composeTestRule.onNodeWithTag("moments_header").assertIsDisplayed()
    composeTestRule.onNodeWithTag("category_filter_row").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_chip_all").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_chip_timeline").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_chip_special").assertExists()
  }

  @Test
  fun `moments screen shows next event highlight card and allows opening details`() {
    composeTestRule.setContent {
      CherishTheme {
        MomentsScreen(storyRepository = storyRepository)
      }
    }

    composeTestRule.onNodeWithTag("moments_featured_next_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("moments_featured_next_card").performClick()
    composeTestRule.onNodeWithTag("event_details_dialog").assertIsDisplayed()
  }

  @Test
  fun `add event button opens add edit dialog`() {
    composeTestRule.setContent {
      CherishTheme {
        MomentsScreen(storyRepository = storyRepository)
      }
    }

    composeTestRule.onNodeWithTag("add_moment_top_button").performClick()
    composeTestRule.onNodeWithTag("add_edit_event_dialog").assertIsDisplayed()
  }
}
