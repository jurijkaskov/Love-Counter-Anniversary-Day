package com.example.ui.screens.calendar

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.StoryModel
import com.example.ui.components.CherishCard
import com.example.ui.components.SecondaryButton
import com.example.ui.screens.create.getIconForStory
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarSelectedDaySection(
  selectedDate: LocalDate,
  events: List<CalendarEventItem>,
  onEventClick: (CalendarEventItem) -> Unit,
  onAddEventForDate: (LocalDate) -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  val isToday = selectedDate == LocalDate.now()

  val formattedDateHeader = selectedDate.format(
    DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("calendar_selected_day_section"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Header for selected date: e.g. "August 12, 2026"
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (isToday) stringResource(R.string.calendar_today_dot_separator, formattedDateHeader) else formattedDateHeader,
        style = MaterialTheme.typography.titleLarge.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.SemiBold,
          fontSize = 19.sp
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("calendar_selected_date_title")
      )

      if (events.isNotEmpty()) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = extColors.rosewoodContainer
        ) {
          Text(
            text = stringResource(R.string.calendar_moments_count, events.size),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    // List of events or Empty State
    AnimatedContent(
      targetState = events,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      label = "day_events_anim"
    ) { currentEvents ->
      if (currentEvents.isEmpty()) {
        // Quiet, elegant empty state
        CherishCard(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_day_empty_card"),
          shape = RoundedCornerShape(18.dp),
          containerColor = MaterialTheme.colorScheme.surface,
          borderColor = extColors.cardBorderSubtle,
          contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = stringResource(R.string.calendar_empty_day_message),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SecondaryButton(
              text = stringResource(R.string.calendar_add_moment_for_date),
              icon = Icons.Default.Add,
              onClick = { onAddEventForDate(selectedDate) },
              modifier = Modifier.testTag("btn_add_moment_selected_date")
            )
          }
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          currentEvents.forEach { event ->
            CalendarEventCard(
              event = event,
              onClick = { onEventClick(event) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun CalendarEventCard(
  event: CalendarEventItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  val iconVector = getIconVectorForCalendarItem(event)

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .testTag("calendar_event_item_${event.id}"),
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder.copy(alpha = 0.8f)),
    shadowElevation = 1.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Circular Icon Badge (matches gold container style from reference image)
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(extColors.goldContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = iconVector,
          contentDescription = null,
          tint = extColors.goldAccent,
          modifier = Modifier.size(22.dp)
        )
      }

      // Title & Subtitle
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = event.title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
          ),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = event.subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Optional Badge
      if (event.badgeText != null) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = extColors.rosewoodContainer
        ) {
          Text(
            text = event.badgeText,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }
  }
}

fun getIconVectorForCalendarItem(event: CalendarEventItem): ImageVector {
  val originalStory = event.originalStory
  if (originalStory != null) {
    return getIconForStory(originalStory)
  }

  return when (event.iconKey.lowercase()) {
    "wedding", "ring" -> Icons.Default.Celebration
    "favorite", "heart" -> Icons.Default.Favorite
    "flight", "trip" -> Icons.Default.Flight
    "cake", "birthday" -> Icons.Default.Cake
    "star" -> Icons.Default.Star
    "checklist" -> if (event.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked
    else -> Icons.Outlined.Event
  }
}
