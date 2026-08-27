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
        title = context.getString(R.string.app_name),
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
    val isToday = targetStory.isToday
    val countValue = String.format("%,d", targetStory.totalDays)
    val countLabel = when {
      isToday -> context.getString(R.string.countdown_today_tag)
      isPast -> context.getString(R.string.widget_days_together_caps)
      else -> context.getString(R.string.widget_days_left_caps)
    }

    val dateSubtitle = if (isPast || isToday) {
      context.getString(R.string.widget_since_label, targetStory.formattedDate)
    } else {
      targetStory.formattedDate
    }

    val footerText = when {
      isToday -> context.getString(R.string.countdown_celebrate_today)
      isPast -> context.getString(R.string.widget_footer_next_anniversary, targetStory.getNextAnniversaryTitle(context), targetStory.daysUntilNextAnniversary.toInt())
      else -> context.resources.getQuantityString(R.plurals.plural_in_days, targetStory.totalDays.toInt().coerceAtLeast(1), targetStory.totalDays.toInt())
    }

    return WidgetDisplayData(
      title = targetStory.getDisplayTitle(context),
      count = countValue,
      countLabel = countLabel,
      breakdownOrNote = targetStory.getFormattedPeriodBreakdown(context),
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
        title = context.getString(R.string.widget_no_upcoming_events),
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
    val badge = targetEvent.getCountdownBadgeText(context).uppercase()
    val eventDateFormatted = targetEvent.formattedNextOccurrenceDate
    val noteOrDescription = if (targetEvent.note.isNotBlank()) {
      targetEvent.note
    } else {
      targetEvent.getFormattedPeriodBreakdown(context)
    }

    return WidgetDisplayData(
      title = targetEvent.getDisplayTitle(context),
      count = daysUntil.toString(),
      countLabel = context.getString(R.string.widget_days_left_caps),
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
        title = context.getString(R.string.app_name),
        count = "0",
        countLabel = context.getString(R.string.moments_create_first_action),
        breakdownOrNote = "",
        dateSubtitle = "",
        badgeText = "",
        iconResId = R.drawable.ic_widget_heart,
        isEmpty = true,
        targetScreenRoute = "countdown"
      )
    }

    val countValue = String.format("%,d", targetStory.totalDays)
    val label = when {
      targetStory.isToday -> context.getString(R.string.countdown_days_today_suffix)
      targetStory.isPastDate -> context.resources.getQuantityString(R.plurals.plural_days_together_suffix, targetStory.totalDays.toInt().coerceAtLeast(0))
      else -> context.resources.getQuantityString(R.plurals.plural_days_to_go_suffix, targetStory.totalDays.toInt().coerceAtLeast(1))
    }

    return WidgetDisplayData(
      title = targetStory.getDisplayTitle(context),
      count = countValue,
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
    val futureEvents = stories.filter { !it.isPastDate && !it.localDate.isBefore(today) }
      .sortedBy { it.daysUntilNextOccurrence }

    if (futureEvents.isNotEmpty()) {
      return futureEvents.first()
    }

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
