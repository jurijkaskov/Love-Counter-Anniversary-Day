package com.example.ui.screens.calendar

import com.example.data.models.MilestoneModel
import com.example.data.models.MilestoneTaskModel
import com.example.data.models.StoryModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class CalendarEventItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val iconKey: String,
  val categoryName: String,
  val date: LocalDate,
  val isAllDay: Boolean = true,
  val timeText: String? = null,
  val badgeText: String? = null,
  val originalStory: StoryModel? = null,
  val originalMilestone: MilestoneModel? = null,
  val originalTask: MilestoneTaskModel? = null
)

data class CalendarDayUiModel(
  val date: LocalDate,
  val isCurrentMonth: Boolean,
  val isToday: Boolean,
  val isSelected: Boolean,
  val events: List<CalendarEventItem>
) {
  val dayNumber: Int get() = date.dayOfMonth
  val hasEvents: Boolean get() = events.isNotEmpty()
  val eventCount: Int get() = events.size
}

object CalendarDataHelper {

  /**
   * Evaluates if a StoryModel occurs on a given target date.
   * Handles both one-time events and recurring annual celebrations (Anniversaries, Birthdays, etc.).
   */
  fun getStoryEventForDate(story: StoryModel, targetDate: LocalDate): CalendarEventItem? {
    val storyDate = story.localDate

    // Exact match on original date
    val isOriginalDate = storyDate == targetDate

    // Recurring annual check (same month and day)
    val isAnnualMatch = isSameAnnualDay(storyDate, targetDate)

    if (!isOriginalDate && !isAnnualMatch) {
      return null
    }

    // Determine subtitle & celebratory context
    val yearsElapsed = targetDate.year - storyDate.year
    val subtitle = when {
      isOriginalDate && story.isPastDate && yearsElapsed == 0 -> "Initial Date"
      isAnnualMatch && yearsElapsed > 0 -> {
        val suffix = when {
          yearsElapsed % 100 in 11..13 -> "th"
          yearsElapsed % 10 == 1 -> "st"
          yearsElapsed % 10 == 2 -> "nd"
          yearsElapsed % 10 == 3 -> "rd"
          else -> "th"
        }
        "$yearsElapsed$suffix Celebration"
      }
      else -> "All day"
    }

    return CalendarEventItem(
      id = "story_${story.id}_${targetDate}",
      title = story.displayTitle,
      subtitle = subtitle,
      iconKey = story.iconKey,
      categoryName = story.category.defaultTitle,
      date = targetDate,
      isAllDay = true,
      badgeText = if (yearsElapsed > 0 && isAnnualMatch) "$yearsElapsed yrs" else null,
      originalStory = story
    )
  }

  /**
   * Helper to check if two dates fall on the same recurring annual day.
   * Handles leap years (Feb 29 on non-leap years maps to Feb 28).
   */
  fun isSameAnnualDay(originalDate: LocalDate, targetDate: LocalDate): Boolean {
    if (originalDate.isAfter(targetDate)) return false // cannot recur before it happened

    val origMonth = originalDate.monthValue
    val origDay = originalDate.dayOfMonth

    if (origMonth == 2 && origDay == 29) {
      val isTargetLeap = targetDate.isLeapYear
      if (isTargetLeap) {
        return targetDate.monthValue == 2 && targetDate.dayOfMonth == 29
      } else {
        return targetDate.monthValue == 2 && targetDate.dayOfMonth == 28
      }
    }

    return targetDate.monthValue == origMonth && targetDate.dayOfMonth == origDay
  }

  /**
   * Collects all events occurring on a specific date from stories, milestones, and tasks.
   */
  fun getAllEventsForDate(
    date: LocalDate,
    stories: List<StoryModel>,
    milestones: List<MilestoneModel>,
    tasks: List<MilestoneTaskModel>
  ): List<CalendarEventItem> {
    val result = mutableListOf<CalendarEventItem>()

    // 1. Stories / Moments
    stories.forEach { story ->
      getStoryEventForDate(story, date)?.let { result.add(it) }
    }

    // 2. Milestones with target dates
    milestones.forEach { milestone ->
      val targetDate = milestone.targetDate
      if (targetDate != null && targetDate == date) {
        result.add(
          CalendarEventItem(
            id = "milestone_${milestone.id}",
            title = milestone.title,
            subtitle = if (milestone.description.isNotBlank()) milestone.description else "Milestone Target Date",
            iconKey = milestone.iconKey,
            categoryName = "Milestone",
            date = date,
            isAllDay = true,
            badgeText = "Milestone",
            originalMilestone = milestone
          )
        )
      }
    }

    // 3. Milestone Tasks with due dates
    tasks.forEach { task ->
      val dueDate = task.dueDate
      if (dueDate != null && dueDate == date) {
        result.add(
          CalendarEventItem(
            id = "task_${task.id}",
            title = task.title,
            subtitle = if (task.isCompleted) "Completed Preparation" else "Preparation Due",
            iconKey = "checklist",
            categoryName = "Preparation",
            date = date,
            isAllDay = true,
            badgeText = if (task.isCompleted) "Done" else "Task",
            originalTask = task
          )
        )
      }
    }

    return result
  }
}
