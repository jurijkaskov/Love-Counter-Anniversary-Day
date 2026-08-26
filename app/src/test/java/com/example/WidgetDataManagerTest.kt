package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.StoryRepository
import com.example.data.models.EventCategory
import com.example.data.models.StoryModel
import com.example.ui.widget.WidgetConfig
import com.example.ui.widget.WidgetDataManager
import com.example.ui.widget.WidgetPreferences
import com.example.ui.widget.WidgetThemePreference
import com.example.ui.widget.WidgetType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class WidgetDataManagerTest {

  private lateinit var context: Context
  private lateinit var storyRepository: StoryRepository
  private lateinit var dataManager: WidgetDataManager
  private lateinit var widgetPreferences: WidgetPreferences

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    storyRepository = StoryRepository(context)
    dataManager = WidgetDataManager(context, storyRepository)
    widgetPreferences = WidgetPreferences(context)
  }

  @Test
  fun testWidgetPreferencesSaveAndRetrieve() {
    val config = WidgetConfig(
      appWidgetId = 101,
      widgetType = WidgetType.MAIN_COUNTDOWN,
      targetStoryId = "custom-story-id",
      autoNextEvent = false,
      themePreference = WidgetThemePreference.DARK
    )

    widgetPreferences.saveConfig(config)
    val loaded = widgetPreferences.getConfig(101, WidgetType.MAIN_COUNTDOWN)

    assertEquals(101, loaded.appWidgetId)
    assertEquals(WidgetType.MAIN_COUNTDOWN, loaded.widgetType)
    assertEquals("custom-story-id", loaded.targetStoryId)
    assertFalse(loaded.autoNextEvent)
    assertEquals(WidgetThemePreference.DARK, loaded.themePreference)
  }

  @Test
  fun testMainCountdownWidgetDataWithStories() {
    val today = LocalDate.now()
    val testStory = StoryModel(
      id = "test-story-widget",
      category = EventCategory.RELATIONSHIP,
      yourName = "Sophia",
      partnerName = "Liam",
      title = "Sophia & Liam",
      dateEpochDay = today.minusDays(500).toEpochDay(),
      isPrimary = true
    )
    storyRepository.saveStory(testStory)

    val config = WidgetConfig(
      appWidgetId = 201,
      widgetType = WidgetType.MAIN_COUNTDOWN,
      targetStoryId = "test-story-widget",
      autoNextEvent = true,
      themePreference = WidgetThemePreference.LIGHT
    )

    val data = dataManager.getMainCountdownData(config)

    assertFalse(data.isEmpty)
    assertEquals("Sophia & Liam", data.title)
    assertEquals("500", data.count)
    assertTrue(data.countLabel.contains("TOGETHER"))
    assertEquals("countdown", data.targetScreenRoute)
  }

  @Test
  fun testNextEventWidgetDataResolution() {
    val today = LocalDate.now()
    val pastStory = StoryModel(
      id = "past-story",
      category = EventCategory.RELATIONSHIP,
      title = "Our Story",
      dateEpochDay = today.minusDays(300).toEpochDay(),
      isPrimary = true
    )
    val upcomingEvent = StoryModel(
      id = "upcoming-anniversary",
      category = EventCategory.WEDDING,
      title = "Wedding Anniversary",
      dateEpochDay = today.plusDays(45).toEpochDay(),
      isPrimary = false
    )

    storyRepository.saveStory(pastStory)
    storyRepository.saveStory(upcomingEvent)

    val config = WidgetConfig(
      appWidgetId = 202,
      widgetType = WidgetType.NEXT_EVENT,
      autoNextEvent = true
    )

    val data = dataManager.getNextEventData(config)

    assertFalse(data.isEmpty)
    assertNotNull(data.title)
    assertTrue(data.badgeText.contains("DAYS") || data.badgeText.contains("DAY") || data.badgeText.contains("TODAY"))
    assertEquals("moments", data.targetScreenRoute)
  }

  @Test
  fun testMinimalDaysWidgetData() {
    val today = LocalDate.now()
    val story = StoryModel(
      id = "minimal-test-story",
      category = EventCategory.RELATIONSHIP,
      title = "Elena & Lucas",
      dateEpochDay = today.minusDays(1000).toEpochDay(),
      isPrimary = true
    )
    storyRepository.saveStory(story)

    val config = WidgetConfig(
      appWidgetId = 203,
      widgetType = WidgetType.MINIMAL_DAYS,
      targetStoryId = "minimal-test-story"
    )

    val data = dataManager.getMinimalDaysData(config)

    assertFalse(data.isEmpty)
    assertEquals("1,000", data.count)
    assertEquals("days together", data.countLabel)
  }
}
