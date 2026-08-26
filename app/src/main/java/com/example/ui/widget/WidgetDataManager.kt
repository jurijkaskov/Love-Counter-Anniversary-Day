package com.example.ui.widget

import android.content.Context
import com.example.R
import com.example.data.StoryRepository
import com.example.data.models.EventCategory
import com.example.data.models.StoryModel
import java.time.LocalDate

class WidgetDataManager(
  private val context: Context,
  private val storyRepository: StoryRepository = StoryRepository(context)
) {

  fun getMainCountdownData(config: WidgetConfig): WidgetDisplayData {
    val stories = storyRepository.stories.value
    val targetStory = if (!config.targetStoryId.isNullOrBlank()) {
      storyRepository.getStoryById(config.targetStoryId) ?: storyRepository.primaryStory.value
    } else {
      storyRepository.primaryStory.value ?: stories.firstOrNull()
    }

    if (targetStory == null) {
      return WidgetDisplayData(
        title = "Love Counter: Anniversary Day",
        count = "0",
        countLabel = context.getString(R.string.widget_days_together_caps),
        breakdownOrNote = context.getString(R.string.widget_empty_prompt),
        dateSubtitle = "",
        badgeText = "",
        iconResId = R.drawable.ic_widget_heart,
        isEmpty = true,
        targetScreenRoute = "countdown"
      )
    }

    val isPast = targetStory.isPastDate
    val countValue = String.format("%,d", targetStory.totalDays)
    val countLabel = if (isPast) {
      context.getString(R.string.widget_days_together_caps)
    } else {
      context.getString(R.string.widget_days_left_caps)
    }

    val dateSubtitle = if (isPast) {
      context.getString(R.string.widget_since_label, targetStory.formattedDate)
    } else {
      targetStory.formattedDate
    }

    val footerText = if (isPast) {
      "Next: ${targetStory.nextAnniversaryTitle} in ${targetStory.daysUntilNextAnniversary} days"
    } else {
      context.getString(R.string.widget_in_days, targetStory.totalDays.toString())
    }

    return WidgetDisplayData(
      title = targetStory.displayTitle,
      count = countValue,
      countLabel = countLabel,
      breakdownOrNote = targetStory.formattedPeriodBreakdown,
      dateSubtitle = dateSubtitle,
      badgeText = footerText,
      iconResId = getCategoryIconRes(targetStory.category),
      storyId = targetStory.id,
      isPast = isPast,
      isEmpty = false,
      targetScreenRoute = "countdown"
    )
  }

  fun getNextEventData(config: WidgetConfig): WidgetDisplayData {
    val stories = storyRepository.stories.value
    if (stories.isEmpty()) {
      return WidgetDisplayData(
        title = "No Upcoming Events",
        count = "0",
        countLabel = "",
        breakdownOrNote = context.getString(R.string.widget_empty_prompt),
        dateSubtitle = "",
        badgeText = "",
        iconResId = R.drawable.ic_widget_sparkle,
        isEmpty = true,
        targetScreenRoute = "moments"
      )
    }

    val targetEvent: StoryModel = if (!config.targetStoryId.isNullOrBlank()) {
      storyRepository.getStoryById(config.targetStoryId) ?: findNearestUpcomingEvent(stories)
    } else {
      findNearestUpcomingEvent(stories)
    }

    val daysUntil = targetEvent.daysUntilNextOccurrence
    val badge = targetEvent.countdownBadgeText.uppercase()
    val eventDateFormatted = targetEvent.formattedNextOccurrenceDate
    val noteOrDescription = if (targetEvent.note.isNotBlank()) {
      targetEvent.note
    } else {
      targetEvent.formattedPeriodBreakdown
    }

    return WidgetDisplayData(
      title = targetEvent.displayTitle,
      count = daysUntil.toString(),
      countLabel = "DAYS REMAINING",
      breakdownOrNote = noteOrDescription,
      dateSubtitle = eventDateFormatted,
      badgeText = badge,
      iconResId = getCategoryIconRes(targetEvent.category),
      storyId = targetEvent.id,
      isPast = false,
      isEmpty = false,
      targetScreenRoute = "moments"
    )
  }

  fun getMinimalDaysData(config: WidgetConfig): WidgetDisplayData {
    val stories = storyRepository.stories.value
    val targetStory = if (!config.targetStoryId.isNullOrBlank()) {
      storyRepository.getStoryById(config.targetStoryId) ?: storyRepository.primaryStory.value
    } else {
      storyRepository.primaryStory.value ?: stories.firstOrNull()
    }

    if (targetStory == null) {
      return WidgetDisplayData(
        title = "Cherish",
        count = "0",
        countLabel = "create moment",
        breakdownOrNote = "",
        dateSubtitle = "",
        badgeText = "",
        iconResId = R.drawable.ic_widget_heart,
        isEmpty = true,
        targetScreenRoute = "countdown"
      )
    }

    val count = String.format("%,d", targetStory.totalDays)
    val label = if (targetStory.isPastDate) {
      "days together"
    } else {
      "days until ${targetStory.displayTitle.lowercase()}"
    }

    return WidgetDisplayData(
      title = targetStory.displayTitle,
      count = count,
      countLabel = label,
      breakdownOrNote = "",
      dateSubtitle = "",
      badgeText = "",
      iconResId = getCategoryIconRes(targetStory.category),
      storyId = targetStory.id,
      isPast = targetStory.isPastDate,
      isEmpty = false,
      targetScreenRoute = "countdown"
    )
  }

  private fun findNearestUpcomingEvent(stories: List<StoryModel>): StoryModel {
    val today = LocalDate.now()
    // First priority: any non-past event with date >= today
    val futureEvents = stories.filter { !it.isPastDate && !it.localDate.isBefore(today) }
      .sortedBy { it.daysUntilNextOccurrence }

    if (futureEvents.isNotEmpty()) {
      return futureEvents.first()
    }

    // Second priority: nearest upcoming yearly anniversary/occurrence
    return stories.minByOrNull { it.daysUntilNextOccurrence } ?: stories.first()
  }

  private fun getCategoryIconRes(category: EventCategory): Int {
    return when (category) {
      EventCategory.RELATIONSHIP -> R.drawable.ic_widget_heart
      EventCategory.WEDDING, EventCategory.ENGAGEMENT -> R.drawable.ic_widget_ring
      EventCategory.BIRTHDAY -> R.drawable.ic_widget_cake
      EventCategory.TRIP -> R.drawable.ic_widget_sparkle
      else -> R.drawable.ic_widget_sparkle
    }
  }
}
