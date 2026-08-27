package com.example.ui.share

import android.content.Context
import com.example.R
import com.example.data.models.EventCategory
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.MilestoneModel
import com.example.data.models.MilestoneWithTasks
import com.example.data.models.ShareCardPayload
import com.example.data.models.ShareCardType
import com.example.data.models.StoryModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ShareCardPayloadFactory {

  /**
   * Constructs a ShareCardPayload from a StoryModel (Time Together or Anniversary / Countdown).
   */
  fun fromStory(context: Context, story: StoryModel, connectedPhoto: MemoryPhotoModel? = null): ShareCardPayload {
    val isPast = story.isPastDate
    val isToday = story.isToday
    val cardType = when {
      isToday -> ShareCardType.TIME_TOGETHER
      isPast -> if (story.category == EventCategory.WEDDING) ShareCardType.ANNIVERSARY else ShareCardType.TIME_TOGETHER
      else -> ShareCardType.COUNTDOWN
    }

    val subtitle = when {
      isToday -> context.getString(R.string.countdown_today_tag)
      isPast -> if (cardType == ShareCardType.ANNIVERSARY) context.getString(R.string.share_label_anniversary_celebration) else context.getString(R.string.share_label_together_for)
      else -> context.getString(R.string.share_label_countdown_special)
    }

    val highlight = when {
      isToday -> context.getString(R.string.countdown_celebrate_today)
      isPast -> context.resources.getQuantityString(R.plurals.plural_days, story.totalDays.toInt().coerceAtLeast(0), story.totalDays.toInt())
      else -> context.resources.getQuantityString(R.plurals.plural_in_days, story.totalDays.toInt().coerceAtLeast(1), story.totalDays.toInt())
    }

    val supportingText = when {
      isToday -> story.getFormattedPeriodBreakdown(context)
      isPast -> story.getFormattedPeriodBreakdown(context)
      else -> context.getString(R.string.share_upcoming_celebration)
    }

    val dateString = if (isPast || isToday) context.getString(R.string.share_since_prefix, story.formattedDate) else story.formattedDate

    return ShareCardPayload(
      cardType = cardType,
      title = story.displayTitle,
      subtitle = subtitle,
      mainHighlight = highlight,
      supportingText = supportingText,
      dateString = dateString,
      quoteOrNote = story.note.ifBlank { context.getString(R.string.countdown_quote).replace("“", "").replace("”", "") },
      photoPath = connectedPhoto?.filePath,
      initialsOrIcon = story.getDisplayInitials(context),
      watermarkText = context.getString(R.string.share_watermark),
      sourceStoryId = story.id
    )
  }

  fun fromMilestone(
    context: Context,
    milestoneWithTasks: MilestoneWithTasks,
    connectedPhoto: MemoryPhotoModel? = null
  ): ShareCardPayload {
    val milestone = milestoneWithTasks.milestone
    val isCompleted = milestoneWithTasks.isFullyCompleted

    val highlight = if (isCompleted) {
      context.getString(R.string.share_label_milestone_complete)
    } else {
      context.getString(R.string.share_milestone_progress, milestoneWithTasks.completedTasksCount, milestoneWithTasks.totalTasksCount)
    }

    val subtitle = context.getString(R.string.share_label_relationship_milestone)
    val dateStr = milestone.targetDateEpochDay?.let {
      LocalDate.ofEpochDay(it).format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }

    return ShareCardPayload(
      cardType = ShareCardType.MILESTONE,
      title = milestone.title,
      subtitle = subtitle,
      mainHighlight = highlight,
      supportingText = milestone.description.ifBlank { context.getString(R.string.share_milestone_ready_footer) },
      dateString = dateStr,
      quoteOrNote = if (isCompleted) context.getString(R.string.share_milestone_complete_footer) else null,
      photoPath = connectedPhoto?.filePath,
      watermarkText = context.getString(R.string.share_watermark),
      sourceMilestoneId = milestone.id,
      sourceStoryId = milestone.associatedStoryId
    )
  }

  fun fromJournalEntry(
    context: Context,
    entry: JournalEntryModel,
    associatedStory: StoryModel? = null,
    connectedPhoto: MemoryPhotoModel? = null
  ): ShareCardPayload {
    val dateStr = LocalDate.ofEpochDay(entry.dateEpochDay).format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

    return ShareCardPayload(
      cardType = ShareCardType.MEMORY,
      title = entry.title,
      subtitle = associatedStory?.displayTitle?.uppercase() ?: context.getString(R.string.timeline_type_memory).uppercase(),
      mainHighlight = context.getString(R.string.share_label_cherished_memory),
      supportingText = dateStr,
      dateString = dateStr,
      quoteOrNote = entry.content.take(180),
      photoPath = connectedPhoto?.filePath,
      watermarkText = context.getString(R.string.share_watermark),
      sourceJournalId = entry.id,
      sourceStoryId = entry.associatedStoryId
    )
  }

  fun fromPhoto(
    context: Context,
    photo: MemoryPhotoModel,
    associatedStory: StoryModel? = null,
    associatedJournal: JournalEntryModel? = null
  ): ShareCardPayload {
    val dateStr = LocalDate.ofEpochDay(photo.dateEpochDay).format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    val title = associatedStory?.displayTitle ?: associatedJournal?.title ?: context.getString(R.string.share_precious_moment_title)

    return ShareCardPayload(
      cardType = ShareCardType.MEMORY,
      title = title,
      subtitle = context.getString(R.string.timeline_type_photo).uppercase(),
      mainHighlight = context.getString(R.string.share_label_captured_with_love),
      supportingText = dateStr,
      dateString = dateStr,
      quoteOrNote = photo.caption.ifBlank { associatedJournal?.content?.take(160) ?: context.getString(R.string.share_photo_frozen_moment) },
      photoPath = photo.filePath,
      watermarkText = context.getString(R.string.share_watermark),
      sourcePhotoId = photo.id,
      sourceStoryId = photo.associatedStoryId,
      sourceJournalId = photo.associatedJournalId
    )
  }
}
