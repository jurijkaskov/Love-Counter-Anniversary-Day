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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.CherishIconButton
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarHeader(
  yearMonth: YearMonth,
  onPreviousMonth: () -> Unit,
  onNextMonth: () -> Unit,
  onJumpToToday: () -> Unit,
  onOpenMonthPicker: () -> Unit,
  onAddMoment: () -> Unit,
  onToggleViewMode: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  val today = LocalDate.now()
  val isCurrentMonth = yearMonth.year == today.year && yearMonth.monthValue == today.monthValue

  val monthYearText = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("calendar_header_section"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Top Bar: Screen Title & Actions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = stringResource(R.string.calendar_title),
          style = MaterialTheme.typography.displaySmall.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = stringResource(R.string.calendar_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = extColors.textMuted
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Today Jump Chip (visible if not current month or as quick reset)
        if (!isCurrentMonth) {
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .clickable(onClick = onJumpToToday)
              .testTag("calendar_jump_today_btn"),
            shape = RoundedCornerShape(16.dp),
            color = extColors.rosewoodContainer
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Today,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = stringResource(R.string.calendar_jump_to_today),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        // Optional View Mode Switcher
        if (onToggleViewMode != null) {
          CherishIconButton(
            icon = Icons.AutoMirrored.Outlined.List,
            contentDescription = "Switch to Timeline View",
            onClick = onToggleViewMode,
            backgroundColor = extColors.cardBorderSubtle,
            tint = MaterialTheme.colorScheme.onSurface,
            testTag = "btn_switch_to_timeline"
          )
        }

        // Add Moment Button
        CherishIconButton(
          icon = Icons.Default.Add,
          contentDescription = stringResource(R.string.moments_add_button),
          onClick = onAddMoment,
          backgroundColor = MaterialTheme.colorScheme.primary,
          tint = MaterialTheme.colorScheme.onPrimary,
          testTag = "calendar_add_moment_btn"
        )
      }
    }

    // 2. Month Navigation Row: <  August 2026  >
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Prev Month Button
      IconButton(
        onClick = onPreviousMonth,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .testTag("calendar_prev_month_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Previous Month",
          tint = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.size(18.dp)
        )
      }

      // Clickable Month & Year with subtle dropdown indicator
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable(onClick = onOpenMonthPicker)
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .testTag("calendar_month_picker_trigger"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        AnimatedContent(
          targetState = monthYearText,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "month_year_anim"
        ) { text ->
          Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Icon(
          imageVector = Icons.Default.ArrowDropDown,
          contentDescription = "Change Month",
          tint = extColors.textMuted,
          modifier = Modifier.size(20.dp)
        )
      }

      // Next Month Button
      IconButton(
        onClick = onNextMonth,
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .testTag("calendar_next_month_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "Next Month",
          tint = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
