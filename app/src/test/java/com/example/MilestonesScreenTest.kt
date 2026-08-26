package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.MilestoneRepository
import com.example.data.StoryRepository
import com.example.data.models.MilestoneCategory
import com.example.data.models.MilestoneModel
import com.example.data.models.MilestoneTaskModel
import com.example.ui.screens.MilestonesScreen
import com.example.ui.theme.CherishTheme
import org.junit.Assert.assertEquals
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
class MilestonesScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var storyRepository: StoryRepository
  private lateinit var milestoneRepository: MilestoneRepository

  @Before
  fun setUp() {
    val context: Context = ApplicationProvider.getApplicationContext()
    storyRepository = StoryRepository(context)
    milestoneRepository = MilestoneRepository(context, storyRepository)
    milestoneRepository.seedDefaultMilestonesIfEmpty()
  }

  @Test
  fun `milestones screen renders header and filter tabs and progress card`() {
    composeTestRule.setContent {
      CherishTheme {
        MilestonesScreen(
          milestoneRepository = milestoneRepository,
          storyRepository = storyRepository
        )
      }
    }

    composeTestRule.onNodeWithTag("milestones_header").assertIsDisplayed()
    composeTestRule.onNodeWithTag("milestone_filter_tabs").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_tab_all").assertIsDisplayed()
    composeTestRule.onNodeWithTag("overall_progress_card").assertIsDisplayed()
  }

  @Test
  fun `milestone repository correctly persists milestone and calculates task completion`() {
    val milestoneId = "test-milestone-${System.currentTimeMillis()}"
    val testMilestone = MilestoneModel(
      id = milestoneId,
      title = "Dream Vacation to Italy",
      category = MilestoneCategory.TRIP,
      targetDateEpochDay = LocalDate.now().plusMonths(3).toEpochDay()
    )

    milestoneRepository.saveMilestone(testMilestone)

    val task1 = MilestoneTaskModel(
      id = "task-1",
      milestoneId = milestoneId,
      title = "Book flights to Rome",
      isCompleted = false
    )
    val task2 = MilestoneTaskModel(
      id = "task-2",
      milestoneId = milestoneId,
      title = "Reserve boutique hotel in Florence",
      isCompleted = true
    )

    milestoneRepository.saveTask(task1)
    milestoneRepository.saveTask(task2)

    val mWithTasks = milestoneRepository.getMilestoneWithTasks(milestoneId)
    org.junit.Assert.assertNotNull(mWithTasks)
    assertEquals(2, mWithTasks?.totalTasksCount)
    assertEquals(1, mWithTasks?.completedTasksCount)
    assertEquals(50, mWithTasks?.progressPercent)

    // Toggle task1 to completed
    milestoneRepository.toggleTask("task-1")
    val updated = milestoneRepository.getMilestoneWithTasks(milestoneId)
    assertEquals(2, updated?.completedTasksCount)
    assertEquals(100, updated?.progressPercent)
    assertTrue(updated?.isFullyCompleted == true)
  }
}
