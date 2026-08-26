package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.data.models.StoryModel
import com.example.ui.screens.CountdownScreen
import com.example.ui.theme.CherishTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CountdownScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `countdown dashboard renders empty state when no story exists`() {
    composeTestRule.setContent {
      CherishTheme {
        CountdownScreen(
          primaryStory = null
        )
      }
    }

    composeTestRule.onNodeWithTag("countdown_header_bar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("hero_countdown_card_empty").assertIsDisplayed()
    composeTestRule.onNodeWithTag("btn_create_first_story_hero").assertIsDisplayed()
  }

  @Test
  fun `countdown dashboard renders hero and components with active story`() {
    val story = StoryModel(
      title = "Eleanor & Julian",
      yourName = "Eleanor",
      partnerName = "Julian",
      dateEpochDay = LocalDate.now().minusDays(1253).toEpochDay(),
      isPrimary = true
    )

    composeTestRule.setContent {
      CherishTheme {
        CountdownScreen(
          primaryStory = story
        )
      }
    }

    composeTestRule.onNodeWithTag("countdown_header_bar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("hero_countdown_card").assertIsDisplayed()
  }
}
