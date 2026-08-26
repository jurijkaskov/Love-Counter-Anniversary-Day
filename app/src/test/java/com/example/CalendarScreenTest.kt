package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.MilestoneRepository
import com.example.data.PreferencesManager
import com.example.data.StoryRepository
import com.example.data.models.EventCategory
import com.example.data.models.StoryModel
import com.example.ui.screens.MomentsScreen
import com.example.ui.screens.calendar.CalendarDataHelper
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.calendar.CalendarSelectedDaySection
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
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CalendarScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var storyRepository: StoryRepository
  private lateinit var milestoneRepository: MilestoneRepository
  private lateinit var preferencesManager: PreferencesManager

  @Before
  fun setUp() {
    val context: Context = ApplicationProvider.getApplicationContext()
    storyRepository = StoryRepository(context)
    storyRepository.seedDefaultStoryIfEmpty()
    milestoneRepository = MilestoneRepository(context, storyRepository)
    milestoneRepository.seedDefaultMilestonesIfEmpty()
    preferencesManager = PreferencesManager(context)
  }

  @Test
  fun `calendar screen renders header, weekday bar, and month grid`() {
    composeTestRule.setContent {
      CherishTheme {
        CalendarScreen(
          storyRepository = storyRepository,
          milestoneRepository = milestoneRepository,
          preferencesManager = preferencesManager,
          onNavigateToTimeline = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("calendar_screen").assertExists()
    composeTestRule.onNodeWithTag("calendar_header_section").assertExists()
    composeTestRule.onNodeWithTag("calendar_weekday_bar").assertExists()
    composeTestRule.onNodeWithTag("calendar_month_grid").assertExists()
  }

  @Test
  fun `calendar selected day section renders properly`() {
    val today = LocalDate.now()
    composeTestRule.setContent {
      CherishTheme {
        CalendarSelectedDaySection(
          selectedDate = today,
          events = emptyList(),
          onEventClick = {},
          onAddEventForDate = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("calendar_selected_day_section").assertIsDisplayed()
    composeTestRule.onNodeWithTag("btn_add_moment_selected_date").assertIsDisplayed()
    composeTestRule.onNodeWithTag("calendar_day_empty_card").assertIsDisplayed()
  }

  @Test
  fun `month navigation buttons work smoothly`() {
    composeTestRule.setContent {
      CherishTheme {
        CalendarScreen(
          storyRepository = storyRepository,
          milestoneRepository = milestoneRepository,
          preferencesManager = preferencesManager,
          onNavigateToTimeline = {}
        )
      }
    }

    // Tap next month
    composeTestRule.onNodeWithTag("calendar_next_month_btn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("calendar_next_month_btn").performClick()

    // Jump back to today
    composeTestRule.onNodeWithTag("calendar_jump_today_btn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("calendar_jump_today_btn").performClick()
  }

  @Test
  fun `month picker dialog opens and can be dismissed`() {
    composeTestRule.setContent {
      CherishTheme {
        CalendarScreen(
          storyRepository = storyRepository,
          milestoneRepository = milestoneRepository,
          preferencesManager = preferencesManager,
          onNavigateToTimeline = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("calendar_month_picker_trigger").performClick()
    composeTestRule.onNodeWithTag("month_year_picker_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("picker_confirm_button").performClick()
  }

  @Test
  fun `moments screen can toggle to calendar via filter chip`() {
    composeTestRule.setContent {
      CherishTheme {
        MomentsScreen(
          storyRepository = storyRepository,
          milestoneRepository = milestoneRepository,
          preferencesManager = preferencesManager
        )
      }
    }

    composeTestRule.onNodeWithTag("filter_chip_calendar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_chip_calendar").performClick()
    composeTestRule.onNodeWithTag("calendar_screen").assertIsDisplayed()
  }

  @Test
  fun `calendar recurring annual event calculation accurately maps anniversaries`() {
    val originalDate = LocalDate.of(2023, 10, 24)
    val story = StoryModel(
      id = "anniversary_story",
      category = EventCategory.WEDDING,
      title = "Our Wedding",
      dateEpochDay = originalDate.toEpochDay()
    )

    // Check year 2026 on October 24
    val targetDate2026 = LocalDate.of(2026, 10, 24)
    val event2026 = CalendarDataHelper.getStoryEventForDate(story, targetDate2026)

    assertNotNull(event2026)
    assertEquals("Our Wedding", event2026?.title)
    assertTrue(event2026?.subtitle?.contains("3rd Celebration") == true)
  }
}
