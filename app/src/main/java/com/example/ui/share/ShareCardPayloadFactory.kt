package com.example.ui.share

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
  fun fromStory(story: StoryModel, connectedPhoto: MemoryPhotoModel? = null): ShareCardPayload {
    val isPast = story.isPastDate
    val cardType = if (isPast) {
      if (story.category == EventCategory.WEDDING) ShareCardType.ANNIVERSARY else ShareCardType.TIME_TOGETHER
    } else {
      ShareCardType.COUNTDOWN
    }

    val subtitle = if (isPast) {
      if (cardType == ShareCardType.ANNIVERSARY) "ANNIVERSARY CELEBRATION" else "TOGETHER FOR"
    } else {
      "COUNTDOWN TO SPECIAL DAY"
    }

    val highlight = if (isPast) {
      "${String.format("%,d", story.totalDays)} Days"
    } else {
      "In ${story.totalDays} Days"
    }

    val supportingText = if (isPast) {
      story.formattedPeriodBreakdown
    } else {
      "Upcoming celebration of our love"
    }

    val dateString = if (isPast) "Since ${story.formattedDate}" else story.formattedDate

    return ShareCardPayload(
      cardType = cardType,
      title = story.displayTitle,
      subtitle = subtitle,
      mainHighlight = highlight,
      supportingText = supportingText,
      dateString = dateString,
      quoteOrNote = story.note.ifBlank { "Every day with you is my favorite day." },
      photoPath = connectedPhoto?.filePath,
      initialsOrIcon = story.displayInitials,
      sourceStoryId = story.id
    )
  }

  /**
   * Constructs a ShareCardPayload from a MilestoneWithTasks or MilestoneModel.
   */
  fun fromMilestone(
    milestoneWithTasks: MilestoneWithTasks,
    connectedPhoto: MemoryPhotoModel? = null
  ): ShareCardPayload {
    val milestone = milestoneWithTasks.milestone
    val isCompleted = milestoneWithTasks.isFullyCompleted

    val highlight = if (isCompleted) {
      "Milestone Complete"
    } else {
      "${milestoneWithTasks.completedTasksCount} of ${milestoneWithTasks.totalTasksCount} Completed"
    }

    val subtitle = "RELATIONSHIP MILESTONE"
    val dateStr = milestone.targetDateEpochDay?.let {
      LocalDate.ofEpochDay(it).format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }

    return ShareCardPayload(
      cardType = ShareCardType.MILESTONE,
      title = milestone.title,
      subtitle = subtitle,
      mainHighlight = highlight,
      supportingText = milestone.description.ifBlank { "Ready for the next chapter of our story." },
      dateString = dateStr,
      quoteOrNote = if (isCompleted) "Another beautiful chapter written together." else null,
      photoPath = connectedPhoto?.filePath,
      sourceMilestoneId = milestone.id,
      sourceStoryId = milestone.associatedStoryId
    )
  }

  /**
   * Constructs a ShareCardPayload from a JournalEntryModel.
   */
  fun fromJournalEntry(
    entry: JournalEntryModel,
    associatedStory: StoryModel? = null,
    connectedPhoto: MemoryPhotoModel? = null
  ): ShareCardPayload {
    val dateStr = LocalDate.ofEpochDay(entry.dateEpochDay).format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

    return ShareCardPayload(
      cardType = ShareCardType.MEMORY,
      title = entry.title,
      subtitle = associatedStory?.displayTitle?.uppercase() ?: "WRITTEN MEMORY",
      mainHighlight = "Cherished Memory",
      supportingText = dateStr,
      dateString = dateStr,
      quoteOrNote = entry.content.take(180),
      photoPath = connectedPhoto?.filePath,
      sourceJournalId = entry.id,
      sourceStoryId = entry.associatedStoryId
    )
  }

  /**
   * Constructs a ShareCardPayload from a MemoryPhotoModel.
   */
  fun fromPhoto(
    photo: MemoryPhotoModel,
    associatedStory: StoryModel? = null,
    associatedJournal: JournalEntryModel? = null
  ): ShareCardPayload {
    val dateStr = LocalDate.ofEpochDay(photo.dateEpochDay).format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    val title = associatedStory?.displayTitle ?: associatedJournal?.title ?: "Our Precious Moment"

    return ShareCardPayload(
      cardType = ShareCardType.MEMORY,
      title = title,
      subtitle = "PHOTO MEMORY",
      mainHighlight = "Captured With Love",
      supportingText = dateStr,
      dateString = dateStr,
      quoteOrNote = photo.caption.ifBlank { associatedJournal?.content?.take(160) ?: "A moment frozen in time forever." },
      photoPath = photo.filePath,
      sourcePhotoId = photo.id,
      sourceStoryId = photo.associatedStoryId,
      sourceJournalId = photo.associatedJournalId
    )
  }
}
