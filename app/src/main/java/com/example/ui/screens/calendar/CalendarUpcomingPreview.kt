package com.example.ui.screens.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.StoryModel
import com.example.ui.components.MilestoneItemRow
import com.example.ui.screens.create.getIconForStory
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun CalendarUpcomingPreview(
  upcomingStories: List<StoryModel>,
  onStoryClick: (StoryModel) -> Unit,
  onViewAllTimeline: () -> Unit,
  onToggleFavorite: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  if (upcomingStories.isEmpty()) return

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("calendar_upcoming_section"),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = stringResource(R.string.calendar_upcoming_moments_title),
        style = MaterialTheme.typography.titleLarge.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.SemiBold,
          fontSize = 19.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )

      TextButton(
        onClick = onViewAllTimeline,
        modifier = Modifier.testTag("calendar_view_timeline_btn")
      ) {
        Text(
          text = stringResource(R.string.calendar_view_timeline),
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold
          ),
          color = MaterialTheme.colorScheme.primary
        )
      }
    }

    upcomingStories.take(3).forEach { story ->
      MilestoneItemRow(
        title = story.getDisplayTitle(context),
        dateFormatted = story.formattedNextOccurrenceDate,
        badgeText = story.getCountdownBadgeText(context),
        icon = getIconForStory(story),
        iconBackground = extColors.goldContainer,
        iconTint = extColors.goldAccent,
        isFavorite = story.isFavorite,
        onFavoriteClick = { onToggleFavorite(story.id) },
        onClick = { onStoryClick(story) },
        testTag = "calendar_upcoming_${story.id}"
      )
    }
  }
}
