package com.example.ui.screens.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.MilestoneModel
import com.example.data.models.StoryModel
import com.example.data.models.TimelineFilterType
import com.example.data.models.TimelineItem
import com.example.data.models.TimelineItemType
import com.example.ui.components.PremiumEmptyState
import com.example.ui.components.PrimaryButton
import com.example.ui.components.TimelineNodeItem
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate

@Composable
fun TimelineView(
  stories: List<StoryModel>,
  entries: List<JournalEntryModel>,
  milestones: List<MilestoneModel>,
  photos: List<MemoryPhotoModel> = emptyList(),
  onStoryClick: (StoryModel) -> Unit,
  onEntryClick: (JournalEntryModel) -> Unit,
  onMilestoneClick: (MilestoneModel) -> Unit,
  onPhotoClick: (MemoryPhotoModel) -> Unit = {},
  onWriteMemoryClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  var selectedFilter by remember { mutableStateOf(TimelineFilterType.ALL) }

  // Build unified chronological timeline
  val timelineItems by remember(stories, entries, milestones, photos, selectedFilter) {
    derivedStateOf {
      val rawItems = mutableListOf<TimelineItem>()

      // 1. Stories / Events
      if (selectedFilter == TimelineFilterType.ALL || selectedFilter == TimelineFilterType.EVENTS) {
        stories.forEach { story ->
          val storyPhotos = photos.filter { it.associatedStoryId == story.id }
          rawItems.add(
            TimelineItem(
              id = "story_${story.id}",
              type = TimelineItemType.EVENT,
              dateEpochDay = story.dateEpochDay,
              title = story.displayTitle,
              subtitle = if (story.note.isNotBlank()) story.note else story.formattedDate,
              categoryLabel = story.category.defaultTitle,
              iconKey = story.iconKey,
              accent = story.themeAccent,
              isFavorite = story.isFavorite,
              storyModel = story,
              connectedPhotos = storyPhotos
            )
          )
        }
      }

      // 2. Journal Memories
      if (selectedFilter == TimelineFilterType.ALL || selectedFilter == TimelineFilterType.MEMORIES) {
        entries.forEach { entry ->
          val associatedStory = stories.find { it.id == entry.associatedStoryId }
          val entryPhotos = photos.filter { it.associatedJournalId == entry.id }
          rawItems.add(
            TimelineItem(
              id = "journal_${entry.id}",
              type = TimelineItemType.MEMORY,
              dateEpochDay = entry.dateEpochDay,
              title = entry.displayTitle,
              subtitle = entry.previewSnippet,
              categoryLabel = if (associatedStory != null) "Memory • ${associatedStory.displayTitle}" else "Written Memory",
              iconKey = entry.iconKey,
              accent = entry.moodAccent,
              isFavorite = entry.isFavorite,
              associatedStory = associatedStory,
              journalEntry = entry,
              connectedPhotos = entryPhotos,
              tags = entry.tags
            )
          )
        }
      }

      // 3. Photo Memories
      if (selectedFilter == TimelineFilterType.ALL || selectedFilter == TimelineFilterType.PHOTOS) {
        photos.forEach { photo ->
          // Only show standalone or featured photo items in timeline
          val associatedStory = stories.find { it.id == photo.associatedStoryId }
          rawItems.add(
            TimelineItem(
              id = "photo_${photo.id}",
              type = TimelineItemType.PHOTO,
              dateEpochDay = photo.dateEpochDay,
              title = if (photo.caption.isNotBlank()) photo.caption else photo.formattedDate,
              subtitle = if (associatedStory != null) "From ${associatedStory.displayTitle}" else photo.formattedDate,
              categoryLabel = if (associatedStory != null) "Photo • ${associatedStory.displayTitle}" else "Photo Memory",
              iconKey = "photo",
              accent = "rosewood",
              isFavorite = photo.isFavorite,
              photoModel = photo,
              associatedStory = associatedStory,
              tags = photo.tags
            )
          )
        }
      }

      // 4. Milestones
      if (selectedFilter == TimelineFilterType.ALL || selectedFilter == TimelineFilterType.MILESTONES) {
        milestones.forEach { milestone ->
          val epochDay = milestone.targetDateEpochDay ?: LocalDate.now().toEpochDay()
          rawItems.add(
            TimelineItem(
              id = "milestone_${milestone.id}",
              type = TimelineItemType.MILESTONE,
              dateEpochDay = epochDay,
              title = milestone.title,
              subtitle = milestone.description.ifBlank { "Journey milestone" },
              categoryLabel = "Milestone • ${milestone.category.name.lowercase().replaceFirstChar { it.uppercase() }}",
              iconKey = milestone.iconKey,
              accent = "gold",
              milestoneModel = milestone
            )
          )
        }
      }

      // Sort chronologically ascending (from earliest beginning to present/future)
      val sorted = rawItems.sortedBy { it.dateEpochDay }.toMutableList()

      // Add "Today" ongoing marker if we have items
      if (sorted.isNotEmpty()) {
        val todayEpochDay = LocalDate.now().toEpochDay()
        val todayMarker = TimelineItem(
          id = "today_marker",
          type = TimelineItemType.TODAY_MARKER,
          dateEpochDay = todayEpochDay,
          title = "And so the adventure continues…",
          subtitle = "Today",
          categoryLabel = "Today",
          iconKey = "favorite",
          accent = "rosewood"
        )
        // Find insert position
        val insertIndex = sorted.indexOfFirst { it.dateEpochDay > todayEpochDay }
        if (insertIndex >= 0) {
          sorted.add(insertIndex, todayMarker)
        } else {
          sorted.add(todayMarker)
        }
      }

      sorted
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("timeline_view")
  ) {
    // Header Banner
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Text(
        text = stringResource(R.string.timeline_header_title),
        style = MaterialTheme.typography.headlineMedium.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = stringResource(R.string.timeline_header_subtitle),
        style = MaterialTheme.typography.bodyMedium.copy(
          fontFamily = FontFamily.Serif,
          fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        ),
        color = extColors.textMuted
      )
    }

    // Filter Chips Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      TimelineFilterChip(
        label = stringResource(R.string.timeline_filter_all),
        isSelected = selectedFilter == TimelineFilterType.ALL,
        onClick = { selectedFilter = TimelineFilterType.ALL },
        testTag = "filter_all"
      )
      TimelineFilterChip(
        label = stringResource(R.string.timeline_filter_memories),
        isSelected = selectedFilter == TimelineFilterType.MEMORIES,
        onClick = { selectedFilter = TimelineFilterType.MEMORIES },
        testTag = "filter_memories"
      )
      TimelineFilterChip(
        label = stringResource(R.string.timeline_filter_photos),
        isSelected = selectedFilter == TimelineFilterType.PHOTOS,
        onClick = { selectedFilter = TimelineFilterType.PHOTOS },
        testTag = "filter_photos"
      )
      TimelineFilterChip(
        label = stringResource(R.string.timeline_filter_events),
        isSelected = selectedFilter == TimelineFilterType.EVENTS,
        onClick = { selectedFilter = TimelineFilterType.EVENTS },
        testTag = "filter_events"
      )
      TimelineFilterChip(
        label = stringResource(R.string.timeline_filter_milestones),
        isSelected = selectedFilter == TimelineFilterType.MILESTONES,
        onClick = { selectedFilter = TimelineFilterType.MILESTONES },
        testTag = "filter_milestones"
      )
    }

    // Content List or Empty State
    if (timelineItems.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        PremiumEmptyState(
          title = stringResource(R.string.timeline_empty_title),
          description = stringResource(R.string.timeline_empty_description),
          actionButtonText = stringResource(R.string.timeline_empty_action),
          onActionClick = onWriteMemoryClick,
          icon = Icons.Outlined.AutoAwesome,
          testTag = "timeline_empty_state"
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
      ) {
        itemsIndexed(timelineItems, key = { _, item -> item.id }) { index, item ->
          val isFirst = index == 0
          val isLast = index == timelineItems.lastIndex

          TimelineNodeItem(
            item = item,
            isFirst = isFirst,
            isLast = isLast,
            onClick = {
              when (item.type) {
                TimelineItemType.EVENT -> item.storyModel?.let(onStoryClick)
                TimelineItemType.MEMORY -> item.journalEntry?.let(onEntryClick)
                TimelineItemType.PHOTO -> item.photoModel?.let(onPhotoClick)
                TimelineItemType.MILESTONE -> item.milestoneModel?.let(onMilestoneClick)
                TimelineItemType.TODAY_MARKER -> onWriteMemoryClick()
              }
            }
          )
        }

        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }
  }
}

@Composable
private fun TimelineFilterChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  val extColors = LocalCherishExtendedColors.current
  val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else extColors.rosewoodContainer.copy(alpha = 0.4f)
  val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else extColors.textSecondary

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .border(
        width = 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorder,
        shape = RoundedCornerShape(12.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 7.dp)
      .testTag(testTag)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 12.sp
      ),
      color = textColor
    )
  }
}
