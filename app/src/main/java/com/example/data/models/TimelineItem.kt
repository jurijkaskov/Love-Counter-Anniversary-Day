package com.example.data.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class TimelineFilterType {
  ALL,
  MEMORIES,
  PHOTOS,
  EVENTS,
  MILESTONES
}

enum class TimelineItemType {
  MEMORY,
  PHOTO,
  EVENT,
  MILESTONE,
  TODAY_MARKER
}

data class TimelineItem(
  val id: String,
  val type: TimelineItemType,
  val dateEpochDay: Long,
  val title: String,
  val subtitle: String? = null,
  val categoryLabel: String? = null,
  val iconKey: String = "favorite",
  val accent: String = "rosewood",
  val isUpcoming: Boolean = false,
  val isFavorite: Boolean = false,
  val associatedStory: StoryModel? = null,
  val journalEntry: JournalEntryModel? = null,
  val photoModel: MemoryPhotoModel? = null,
  val connectedPhotos: List<MemoryPhotoModel> = emptyList(),
  val storyModel: StoryModel? = null,
  val milestoneModel: MilestoneModel? = null,
  val tags: List<String> = emptyList()
) {
  val localDate: LocalDate
    get() = LocalDate.ofEpochDay(dateEpochDay)

  val formattedDate: String
    get() {
      val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
      return localDate.format(formatter)
    }
}
