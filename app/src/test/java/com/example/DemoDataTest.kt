package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.JournalRepository
import com.example.data.MilestoneRepository
import com.example.data.OnboardingManager
import com.example.data.PhotoRepository
import com.example.data.StoryRepository
import com.example.data.demo.DemoConfig
import com.example.data.demo.DemoDataProvider
import com.example.data.models.EventCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DemoDataTest {

  private lateinit var context: Context
  private lateinit var storyRepository: StoryRepository
  private lateinit var milestoneRepository: MilestoneRepository
  private lateinit var journalRepository: JournalRepository
  private lateinit var photoRepository: PhotoRepository
  private lateinit var onboardingManager: OnboardingManager

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    storyRepository = StoryRepository(context)
    milestoneRepository = MilestoneRepository(context, storyRepository)
    journalRepository = JournalRepository(context)
    photoRepository = PhotoRepository(context)
    onboardingManager = OnboardingManager(context)

    // Clear all storage for clean test setup
    storyRepository.resetAll()
    journalRepository.clearAll()
    photoRepository.clearAll()
    onboardingManager.resetOnboarding()
    context.getSharedPreferences("demo_data_internal_prefs", Context.MODE_PRIVATE).edit().clear().apply()
  }

  @Test
  fun `demo config flag is accessible and boolean`() {
    assertTrue(DemoConfig.USE_DEMO_DATA || !DemoConfig.USE_DEMO_DATA)
  }

  @Test
  fun `populating demo data creates rich realistic dataset across all features`() {
    DemoDataProvider.populateIfEnabled(
      context = context,
      storyRepository = storyRepository,
      milestoneRepository = milestoneRepository,
      journalRepository = journalRepository,
      photoRepository = photoRepository,
      onboardingManager = onboardingManager,
      force = true
    )

    // 1. Verify Onboarding completed
    assertTrue(onboardingManager.hasCompletedOnboarding)

    // 2. Verify Stories (Primary relationship, wedding, birthday, trips, engagement, etc.)
    val stories = storyRepository.stories.value
    assertTrue("Should have multiple demo stories", stories.size >= 8)

    val primary = storyRepository.primaryStory.value
    assertNotNull(primary)
    assertEquals("Eleanor", primary?.yourName)
    assertEquals("Julian", primary?.partnerName)
    assertEquals("Eleanor & Julian", primary?.title)
    assertTrue(primary?.isPrimary == true)

    // Verify relative dynamic dates: primary story is in the past (1253 days ago)
    val today = LocalDate.now()
    val primaryDate = LocalDate.ofEpochDay(primary!!.dateEpochDay)
    assertEquals(today.minusDays(1253), primaryDate)

    // Verify upcoming events exist (e.g. Wedding, Birthday, Upcoming Trip)
    val upcomingStories = stories.filter { it.dateEpochDay > today.toEpochDay() }
    assertTrue("Should have upcoming events for countdowns", upcomingStories.isNotEmpty())
    assertTrue(stories.any { it.category == EventCategory.WEDDING })
    assertTrue(stories.any { it.category == EventCategory.BIRTHDAY })
    assertTrue(stories.any { it.category == EventCategory.TRIP })
    assertTrue(stories.any { it.category == EventCategory.ENGAGEMENT })

    // 3. Verify Milestones & Tasks
    val milestones = milestoneRepository.milestones.value
    assertTrue("Should have multiple milestones", milestones.isNotEmpty())
    val tasks = milestoneRepository.tasks.value
    assertTrue("Should have checklist tasks", tasks.isNotEmpty())
    assertTrue("Should have completed tasks", tasks.any { it.isCompleted })
    assertTrue("Should have upcoming/pending tasks", tasks.any { !it.isCompleted })

    // 4. Verify Journal Entries
    val journalEntries = journalRepository.entries.value
    assertTrue("Should have rich journal entries", journalEntries.size >= 5)
    assertTrue(journalEntries.any { it.isFavorite })
    assertTrue(journalEntries.all { it.title.isNotBlank() && it.content.isNotBlank() })

    // 5. Verify Memory Photos
    val photos = photoRepository.photos.value
    assertTrue("Should have generated artistic memory photos", photos.isNotEmpty())

    // 6. Verify repeated calls do not duplicate data
    val storyCountBefore = stories.size
    val taskCountBefore = tasks.size
    val journalCountBefore = journalEntries.size
    val photoCountBefore = photos.size

    DemoDataProvider.populateIfEnabled(
      context = context,
      storyRepository = storyRepository,
      milestoneRepository = milestoneRepository,
      journalRepository = journalRepository,
      photoRepository = photoRepository,
      onboardingManager = onboardingManager,
      force = false
    )

    assertEquals(storyCountBefore, storyRepository.stories.value.size)
    assertEquals(taskCountBefore, milestoneRepository.tasks.value.size)
    assertEquals(journalCountBefore, journalRepository.entries.value.size)
    assertEquals(photoCountBefore, photoRepository.photos.value.size)
  }
}
