package com.example.ui.screens.journal

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.MilestoneModel
import com.example.data.models.StoryModel

enum class JournalTimelineTab {
  TIMELINE,
  JOURNAL,
  PHOTOS
}

@Composable
fun JournalTimelineScreen(
  stories: List<StoryModel>,
  entries: List<JournalEntryModel>,
  milestones: List<MilestoneModel>,
  onStoryClick: (StoryModel) -> Unit,
  onEntryClick: (JournalEntryModel) -> Unit,
  onMilestoneClick: (MilestoneModel) -> Unit,
  onWriteMemoryClick: () -> Unit,
  onToggleEntryFavorite: (String) -> Unit,
  modifier: Modifier = Modifier,
  photos: List<MemoryPhotoModel> = emptyList(),
  onPhotoClick: (MemoryPhotoModel) -> Unit = {},
  onAddPhotoClick: () -> Unit = {},
  onTogglePhotoFavorite: (String) -> Unit = {},
  initialTab: JournalTimelineTab = JournalTimelineTab.TIMELINE
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("journal_timeline_screen")
  ) {
    AnimatedContent(
      targetState = initialTab,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      label = "journal_timeline_tab_transition"
    ) { currentTab ->
      when (currentTab) {
        JournalTimelineTab.TIMELINE -> {
          TimelineView(
            stories = stories,
            entries = entries,
            milestones = milestones,
            photos = photos,
            onStoryClick = onStoryClick,
            onEntryClick = onEntryClick,
            onMilestoneClick = onMilestoneClick,
            onPhotoClick = onPhotoClick,
            onWriteMemoryClick = onWriteMemoryClick
          )
        }
        JournalTimelineTab.JOURNAL -> {
          JournalView(
            entries = entries,
            stories = stories,
            onEntryClick = onEntryClick,
            onToggleFavorite = onToggleEntryFavorite,
            onWriteMemoryClick = onWriteMemoryClick
          )
        }
        JournalTimelineTab.PHOTOS -> {
          PhotoGalleryView(
            photos = photos,
            onPhotoClick = onPhotoClick,
            onAddPhotoClick = onAddPhotoClick,
            onToggleFavorite = onTogglePhotoFavorite
          )
        }
      }
    }
  }
}
