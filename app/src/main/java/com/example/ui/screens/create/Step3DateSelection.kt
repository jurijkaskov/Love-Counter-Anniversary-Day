package com.example.ui.screens.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Step3DateSelection(
  selectedDate: LocalDate,
  onDateSelected: (LocalDate) -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  var showDatePickerDialog by remember { mutableStateOf(false) }

  val today = LocalDate.now()
  val isPast = !selectedDate.isAfter(today)
  val daysDiff = abs(ChronoUnit.DAYS.between(selectedDate, today))

  val formattedDate = remember(selectedDate) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    selectedDate.format(formatter)
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("create_step_3_container"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Badge
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(extColors.rosewoodContainer)
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Text(
        text = stringResource(R.string.create_step3_badge),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp
        ),
        color = MaterialTheme.colorScheme.primary
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Headline
    Text(
      text = stringResource(R.string.create_step3_headline),
      style = MaterialTheme.typography.headlineMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Subtitle
    Text(
      text = stringResource(R.string.create_step3_subhead),
      style = MaterialTheme.typography.bodyMedium,
      color = extColors.textMuted
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Prominent Selected Date Card
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = extColors.goldAccent.copy(alpha = 0.15f))
        .clip(RoundedCornerShape(24.dp))
        .clickable { showDatePickerDialog = true }
        .testTag("selected_date_card"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      border = BorderStroke(1.5.dp, extColors.goldAccent.copy(alpha = 0.5f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(extColors.goldContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = extColors.goldAccent,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = stringResource(R.string.date_selected_label),
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = extColors.textMuted
            )
          }

          // Change button badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(extColors.rosewoodContainer)
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = stringResource(R.string.create_step3_tap_to_change),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Date Display
        Text(
          text = formattedDate,
          style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Days Counter Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPast) extColors.rosewoodContainer else extColors.goldContainer)
            .border(
              width = 1.dp,
              color = if (isPast) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else extColors.goldAccent.copy(alpha = 0.4f),
              shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = null,
              tint = if (isPast) MaterialTheme.colorScheme.primary else extColors.goldAccent,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isPast) {
                if (daysDiff == 0L) stringResource(R.string.create_step3_beginning_today) else stringResource(R.string.create_step3_together_for, daysDiff)
              } else {
                stringResource(R.string.create_step3_coming_in, daysDiff)
              },
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = if (isPast) MaterialTheme.colorScheme.primary else extColors.goldAccent
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Quick presets
    Text(
      text = stringResource(R.string.create_step3_quick_presets),
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
      color = extColors.textMuted,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(10.dp))

    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      QuickDateChip(
        label = stringResource(R.string.create_step3_preset_today),
        isSelected = selectedDate == today,
        onClick = { onDateSelected(today) }
      )
      QuickDateChip(
        label = stringResource(R.string.create_step3_preset_1yr),
        isSelected = selectedDate == today.minusYears(1),
        onClick = { onDateSelected(today.minusYears(1)) }
      )
      QuickDateChip(
        label = stringResource(R.string.create_step3_preset_3yr),
        isSelected = selectedDate == today.minusYears(3),
        onClick = { onDateSelected(today.minusYears(3)) }
      )
      QuickDateChip(
        label = stringResource(R.string.create_step3_preset_5yr),
        isSelected = selectedDate == today.minusYears(5),
        onClick = { onDateSelected(today.minusYears(5)) }
      )
      QuickDateChip(
        label = stringResource(R.string.create_step3_preset_custom),
        isSelected = false,
        icon = Icons.Default.EditCalendar,
        onClick = { showDatePickerDialog = true }
      )
    }

    // Material 3 Date Picker Dialog
    if (showDatePickerDialog) {
      val initialEpochMillis = remember(selectedDate) {
        selectedDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
      }
      val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialEpochMillis
      )

      DatePickerDialog(
        onDismissRequest = { showDatePickerDialog = false },
        confirmButton = {
          TextButton(
            onClick = {
              datePickerState.selectedDateMillis?.let { millis ->
                val newDate = Instant.ofEpochMilli(millis)
                  .atZone(ZoneId.of("UTC"))
                  .toLocalDate()
                onDateSelected(newDate)
              }
              showDatePickerDialog = false
            }
          ) {
            Text(stringResource(R.string.btn_close), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
          }
        },
        dismissButton = {
          TextButton(onClick = { showDatePickerDialog = false }) {
            Text(stringResource(R.string.btn_cancel), color = extColors.textMuted)
          }
        },
        colors = DatePickerDefaults.colors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      ) {
        DatePicker(
          state = datePickerState,
          colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            headlineContentColor = MaterialTheme.colorScheme.primary,
            weekdayContentColor = extColors.textMuted,
            subheadContentColor = MaterialTheme.colorScheme.onSurface,
            yearContentColor = MaterialTheme.colorScheme.onSurface,
            currentYearContentColor = MaterialTheme.colorScheme.primary,
            selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
            selectedYearContainerColor = MaterialTheme.colorScheme.primary,
            dayContentColor = MaterialTheme.colorScheme.onSurface,
            selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
            todayContentColor = MaterialTheme.colorScheme.primary,
            todayDateBorderColor = MaterialTheme.colorScheme.primary
          )
        )
      }
    }
  }
}

@Composable
private fun QuickDateChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
  val extColors = LocalCherishExtendedColors.current

  Surface(
    modifier = Modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .testTag("date_preset_$label"),
    shape = RoundedCornerShape(14.dp),
    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorder)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else extColors.goldAccent,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
      }
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        ),
        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
      )
    }
  }
}
