package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun ReminderTimePickerDialog(
  initialHour: Int,
  initialMinute: Int,
  onDismiss: () -> Unit,
  onConfirm: (hour: Int, minute: Int) -> Unit
) {
  val extColors = LocalCherishExtendedColors.current

  // 12-hour format calculations
  val initialIsPm = initialHour >= 12
  val initial12Hour = when {
    initialHour == 0 -> 12
    initialHour > 12 -> initialHour - 12
    else -> initialHour
  }

  var selected12Hour by remember { mutableIntStateOf(initial12Hour) }
  var selectedMinute by remember { mutableIntStateOf(initialMinute) }
  var isPm by remember { mutableStateOf(initialIsPm) }

  Dialog(onDismissRequest = onDismiss) {
    CherishCard(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("reminder_time_picker_dialog")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(extColors.goldContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.Schedule,
              contentDescription = null,
              tint = extColors.goldAccent,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = stringResource(R.string.reminder_time_picker_title),
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Time display & selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Hour Selector
          TimeWheelColumn(
            value = selected12Hour,
            range = 1..12,
            format = { "%02d".format(it) },
            onValueChange = { selected12Hour = it },
            label = "Hour",
            testTag = "picker_hour"
          )

          Text(
            text = ":",
            style = MaterialTheme.typography.headlineLarge.copy(
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Serif
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp)
          )

          // Minute Selector
          TimeWheelColumn(
            value = selectedMinute,
            range = 0..55 step 5,
            format = { "%02d".format(it) },
            onValueChange = { selectedMinute = it },
            label = "Min",
            testTag = "picker_minute"
          )

          Spacer(modifier = Modifier.width(16.dp))

          // AM / PM Toggle
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AmPmButton(
              text = "AM",
              isSelected = !isPm,
              onClick = { isPm = false },
              testTag = "picker_am"
            )
            AmPmButton(
              text = "PM",
              isSelected = isPm,
              onClick = { isPm = true },
              testTag = "picker_pm"
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          SecondaryButton(
            text = stringResource(R.string.btn_cancel),
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .testTag("time_picker_cancel")
          )

          PrimaryButton(
            text = stringResource(R.string.btn_save),
            onClick = {
              val hour24 = when {
                isPm && selected12Hour < 12 -> selected12Hour + 12
                !isPm && selected12Hour == 12 -> 0
                else -> selected12Hour
              }
              onConfirm(hour24, selectedMinute)
            },
            modifier = Modifier
              .weight(1f)
              .testTag("time_picker_confirm")
          )
        }
      }
    }
  }
}

@Composable
private fun TimeWheelColumn(
  value: Int,
  range: Iterable<Int>,
  format: (Int) -> String,
  onValueChange: (Int) -> Unit,
  label: String,
  testTag: String
) {
  val extColors = LocalCherishExtendedColors.current
  val list = remember(range) { range.toList() }
  val currentIndex = list.indexOf(value).coerceAtLeast(0)

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.testTag(testTag)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = extColors.textMuted
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Up button
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(extColors.cardBorderSubtle)
        .clickable {
          val nextIndex = (currentIndex - 1 + list.size) % list.size
          onValueChange(list[nextIndex])
        }
        .padding(horizontal = 12.dp, vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "▲",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    // Selected Value Box
    Box(
      modifier = Modifier
        .padding(vertical = 4.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(horizontal = 16.dp, vertical = 10.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = format(value),
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Serif
        ),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    // Down button
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(extColors.cardBorderSubtle)
        .clickable {
          val nextIndex = (currentIndex + 1) % list.size
          onValueChange(list[nextIndex])
        }
        .padding(horizontal = 12.dp, vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "▼",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun AmPmButton(
  text: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  val extColors = LocalCherishExtendedColors.current
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorderSubtle)
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      ),
      color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    )
  }
}
