package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.example.data.MilestoneRepository
import com.example.data.PreferencesManager
import com.example.data.StoryRepository
import com.example.data.models.AccentColorStyle
import com.example.data.models.DateFormatOption
import com.example.data.models.FirstDayOfWeekOption
import com.example.data.models.ThemeMode
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CherishTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SettingsScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var storyRepository: StoryRepository
  private lateinit var milestoneRepository: MilestoneRepository
  private lateinit var preferencesManager: PreferencesManager

  @Before
  fun setUp() {
    val context: Context = ApplicationProvider.getApplicationContext()
    storyRepository = StoryRepository(context)
    milestoneRepository = MilestoneRepository(context, storyRepository)
    preferencesManager = PreferencesManager(context)
  }

  @Test
  fun `settings screen renders all major sections and preview`() {
    composeTestRule.setContent {
      CherishTheme {
        SettingsScreen(
          preferencesManager = preferencesManager,
          storyRepository = storyRepository,
          milestoneRepository = milestoneRepository
        )
      }
    }

    composeTestRule.onNodeWithTag("profile_hero_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("settings_appearance_header").assertIsDisplayed()
    composeTestRule.onNodeWithTag("theme_card_system").assertIsDisplayed()
    composeTestRule.onNodeWithTag("theme_card_light").assertIsDisplayed()
    composeTestRule.onNodeWithTag("theme_card_dark").assertIsDisplayed()

    // Scroll to check preview card
    composeTestRule.onNodeWithTag("settings_lazy_column")
      .performScrollToNode(hasTestTag("theme_preview_card"))
    composeTestRule.onNodeWithTag("theme_preview_card").assertIsDisplayed()

    // Scroll to check further sections in LazyColumn
    composeTestRule.onNodeWithTag("settings_lazy_column")
      .performScrollToNode(hasTestTag("settings_preferences_header"))
    composeTestRule.onNodeWithTag("settings_preferences_header").assertIsDisplayed()

    composeTestRule.onNodeWithTag("settings_lazy_column")
      .performScrollToNode(hasTestTag("settings_data_header"))
    composeTestRule.onNodeWithTag("settings_data_header").assertIsDisplayed()

    composeTestRule.onNodeWithTag("settings_lazy_column")
      .performScrollToNode(hasTestTag("settings_about_header"))
    composeTestRule.onNodeWithTag("settings_about_header").assertIsDisplayed()
  }

  @Test
  fun `switching theme mode and accent style updates preferences`() {
    preferencesManager.setThemeMode(ThemeMode.DARK)
    assertEquals(ThemeMode.DARK, preferencesManager.settings.value.themeMode)

    preferencesManager.setAccentColorStyle(AccentColorStyle.WARM_ROSE)
    assertEquals(AccentColorStyle.WARM_ROSE, preferencesManager.settings.value.accentColorStyle)

    preferencesManager.setDateFormat(DateFormatOption.SLASH_MDY)
    assertEquals(DateFormatOption.SLASH_MDY, preferencesManager.settings.value.dateFormat)

    preferencesManager.setFirstDayOfWeek(FirstDayOfWeekOption.MONDAY)
    assertEquals(FirstDayOfWeekOption.MONDAY, preferencesManager.settings.value.firstDayOfWeek)
  }

  @Test
  fun `export all data creates valid json backup`() {
    val exportJson = preferencesManager.exportAllDataJson(storyRepository, milestoneRepository)
    assertNotNull(exportJson)
    assertTrue(exportJson.contains("Love Counter: Anniversary Day"))
    assertTrue(exportJson.contains("settings"))
    assertTrue(exportJson.contains("stories"))
    assertTrue(exportJson.contains("milestones"))
  }
}
