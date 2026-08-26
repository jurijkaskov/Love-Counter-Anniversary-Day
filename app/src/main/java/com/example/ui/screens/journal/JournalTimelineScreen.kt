package com.example.ui.screens.journal

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.MilestoneModel
import com.example.data.models.StoryModel
import com.example.ui.theme.LocalCherishExtendedColors

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
  photos: List<MemoryPhotoModel> = emptyList(),
  onStoryClick: (StoryModel) -> Unit,
  onEntryClick: (JournalEntryModel) -> Unit,
  onMilestoneClick: (MilestoneModel) -> Unit,
  onPhotoClick: (MemoryPhotoModel) -> Unit = {},
  onAddPhotoClick: () -> Unit = {},
  onWriteMemoryClick: () -> Unit,
  onToggleEntryFavorite: (String) -> Unit,
  onTogglePhotoFavorite: (String) -> Unit = {},
  initialTab: JournalTimelineTab = JournalTimelineTab.TIMELINE,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  var activeTab by remember { mutableStateOf(initialTab) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("journal_timeline_screen")
  ) {
    // Segmented Switcher Pill (3 tabs: Timeline, Journal, Photos)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.Center
    ) {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = extColors.rosewoodContainer.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          // Tab 1: Timeline
          val isTimeline = activeTab == JournalTimelineTab.TIMELINE
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isTimeline) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
              .clickable { activeTab = JournalTimelineTab.TIMELINE }
              .padding(vertical = 9.dp)
              .testTag("tab_timeline"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = stringResource(R.string.timeline_tab_timeline),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isTimeline) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.5.sp
              ),
              color = if (isTimeline) MaterialTheme.colorScheme.onPrimary else extColors.textSecondary
            )
          }

          // Tab 2: Journal
          val isJournal = activeTab == JournalTimelineTab.JOURNAL
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isJournal) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
              .clickable { activeTab = JournalTimelineTab.JOURNAL }
              .padding(vertical = 9.dp)
              .testTag("tab_journal"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = stringResource(R.string.timeline_tab_journal),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isJournal) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.5.sp
              ),
              color = if (isJournal) MaterialTheme.colorScheme.onPrimary else extColors.textSecondary
            )
          }

          // Tab 3: Photos
          val isPhotos = activeTab == JournalTimelineTab.PHOTOS
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isPhotos) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
              .clickable { activeTab = JournalTimelineTab.PHOTOS }
              .padding(vertical = 9.dp)
              .testTag("tab_photos"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = stringResource(R.string.photos_tab_title),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isPhotos) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.5.sp
              ),
              color = if (isPhotos) MaterialTheme.colorScheme.onPrimary else extColors.textSecondary
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Animated Tab Content
    AnimatedContent(
      targetState = activeTab,
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
