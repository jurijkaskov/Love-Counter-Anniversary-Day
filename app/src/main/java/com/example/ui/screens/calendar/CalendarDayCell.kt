package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.format.DateTimeFormatter

@Composable
fun CalendarDayCell(
  dayUiModel: CalendarDayUiModel,
  onSelectDate: (java.time.LocalDate) -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  val date = dayUiModel.date
  val isSelected = dayUiModel.isSelected
  val isToday = dayUiModel.isToday
  val isCurrentMonth = dayUiModel.isCurrentMonth
  val hasEvents = dayUiModel.hasEvents
  val eventCount = dayUiModel.eventCount

  // Colors based on state
  val primaryColor = MaterialTheme.colorScheme.primary
  val onSurface = MaterialTheme.colorScheme.onSurface
  val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

  val cellContentDesc = buildString {
    append(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")))
    if (isToday) append(", Today")
    if (isSelected) append(", Selected")
    if (hasEvents) append(", $eventCount moments")
  }

  Box(
    modifier = modifier
      .size(48.dp)
      .clip(CircleShape)
      .semantics {
        role = Role.Button
        selected = isSelected
        contentDescription = cellContentDesc
      }
      .clickable(
        onClick = { onSelectDate(date) }
      )
      .testTag("calendar_day_${date.year}_${date.monthValue}_${date.dayOfMonth}"),
    contentAlignment = Alignment.Center
  ) {
    // Selected Day Background Circle (matches reference image: dark solid circle or primary color)
    if (isSelected) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(primaryColor)
      )
    } else if (isToday) {
      // Today subtle highlight ring
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .border(1.5.dp, primaryColor.copy(alpha = 0.8f), CircleShape)
          .background(extColors.goldContainer.copy(alpha = 0.3f))
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(bottom = 2.dp)
    ) {
      // Day Number
      val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !isCurrentMonth -> onSurfaceVariant.copy(alpha = 0.35f)
        isToday -> primaryColor
        else -> onSurface
      }

      Text(
        text = dayUiModel.dayNumber.toString(),
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
          fontSize = 14.sp
        ),
        color = textColor
      )

      // Event Indicator Dots / Hearts
      if (hasEvents) {
        Spacer(modifier = Modifier.height(2.dp))
        val indicatorColor = when {
          isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
          !isCurrentMonth -> extColors.goldAccent.copy(alpha = 0.4f)
          else -> extColors.goldAccent
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Display 1 dot for 1 event, 2 tiny hearts/dots for multiple events (like in mockup on date 5)
          if (eventCount >= 2) {
            Box(
              modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(indicatorColor)
            )
            Box(
              modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(indicatorColor)
            )
          } else {
            Box(
              modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(indicatorColor)
            )
          }
        }
      } else {
        // Placeholder space so day number doesn't shift
        Spacer(modifier = Modifier.height(6.dp))
      }
    }
  }
}
