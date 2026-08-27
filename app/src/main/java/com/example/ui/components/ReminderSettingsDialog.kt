package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.models.EventCategory
import com.example.data.models.ReminderConfig
import com.example.data.models.ReminderOffset
import com.example.data.models.StoryModel
import com.example.notifications.ReminderScheduler
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReminderSettingsDialog(
  story: StoryModel,
  onDismiss: () -> Unit,
  onSave: (ReminderConfig) -> Unit
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  var isEnabled by remember { mutableStateOf(story.reminderConfig.isEnabled) }
  var selectedOffsets by remember { mutableStateOf(story.reminderConfig.offsets.toMutableList()) }
  var timeHour by remember { mutableIntStateOf(story.reminderConfig.timeHour) }
  var timeMinute by remember { mutableIntStateOf(story.reminderConfig.timeMinute) }

  var showTimePicker by remember { mutableStateOf(false) }
  var showCustomOffsetDialog by remember { mutableStateOf(false) }

  val currentConfig = remember(isEnabled, selectedOffsets, timeHour, timeMinute) {
    ReminderConfig(
      isEnabled = isEnabled,
      offsets = selectedOffsets.toList(),
      timeHour = timeHour,
      timeMinute = timeMinute
    )
  }

  // Calculate next upcoming trigger preview
  val noNotifStr = stringResource(R.string.reminder_no_notifications_scheduled)
  val nextNotifFormat = stringResource(R.string.reminder_next_notification_format)
  val nextNotifCycleStr = stringResource(R.string.reminder_next_notification_cycle)
  val atStr = stringResource(R.string.reminder_time_picker_at)

  val nextTriggerPreview = remember(currentConfig, story, noNotifStr, nextNotifFormat, nextNotifCycleStr, atStr) {
    if (!isEnabled || selectedOffsets.isEmpty()) {
      noNotifStr
    } else {
      val sortedOffsets = selectedOffsets.sortedBy { it.daysBefore }
      val now = System.currentTimeMillis()
      val nextMillis = sortedOffsets.firstNotNullOfOrNull { offset ->
        ReminderScheduler.calculateNextTriggerMillis(story.copy(reminderConfig = currentConfig), offset, now)
      }
      if (nextMillis != null) {
        val dt = Instant.ofEpochMilli(nextMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy '$atStr' h:mm a", Locale.getDefault())
        nextNotifFormat.format(dt.format(formatter))
      } else {
        nextNotifCycleStr
      }
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    CherishCard(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp)
        .testTag("reminder_settings_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(extColors.rosewoodContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.NotificationsActive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
            }

            Column {
              Text(
                text = stringResource(R.string.reminder_title),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = story.displayTitle,
                style = MaterialTheme.typography.bodySmall,
                color = extColors.textMuted
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Master Switch
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { isEnabled = !isEnabled }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("reminder_master_toggle_row"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = stringResource(R.string.reminder_enable_switch),
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = stringResource(R.string.reminder_subtitle),
              style = MaterialTheme.typography.bodySmall,
              color = extColors.textMuted
            )
          }

          Switch(
            checked = isEnabled,
            onCheckedChange = { isEnabled = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
              checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("reminder_master_switch")
          )
        }

        AnimatedVisibility(visible = isEnabled) {
          Column {
            Spacer(modifier = Modifier.height(16.dp))

            // Smart Suggestion Card
            val suggestions = remember(story.category) {
              ReminderConfig.suggestionsForCategory(story.category)
            }
            val categoryLabel = stringResource(story.category.titleResId)

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(extColors.goldContainer.copy(alpha = 0.6f))
                .padding(12.dp)
                .testTag("smart_suggestion_box")
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = extColors.goldAccent,
                    modifier = Modifier.size(18.dp)
                  )
                  Column {
                    Text(
                      text = stringResource(R.string.reminder_smart_suggestions_title, categoryLabel),
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = suggestions.joinToString(" · ") { it.getShortLabel(context) },
                      style = MaterialTheme.typography.bodySmall,
                      color = extColors.textMuted
                    )
                  }
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                      selectedOffsets = suggestions.toMutableList()
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("apply_suggestion_btn"),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = stringResource(R.string.reminder_apply_suggestion),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder Offsets Multi-Selector
            Text(
              text = stringResource(R.string.reminder_offsets_label),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif
              ),
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Option Chips
            val quickOptions = ReminderOffset.DEFAULT_QUICK_OPTIONS
            Column(
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                quickOptions.take(3).forEach { option ->
                  val isSelected = selectedOffsets.any { it.daysBefore == option.daysBefore }
                  ReminderChip(
                    offset = option,
                    isSelected = isSelected,
                    context = context,
                    onClick = {
                      val current = selectedOffsets.toMutableList()
                      if (isSelected) {
                        current.removeAll { it.daysBefore == option.daysBefore }
                      } else {
                        current.add(option)
                      }
                      selectedOffsets = current
                    },
                    modifier = Modifier.weight(1f)
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                quickOptions.drop(3).take(3).forEach { option ->
                  val isSelected = selectedOffsets.any { it.daysBefore == option.daysBefore }
                  ReminderChip(
                    offset = option,
                    isSelected = isSelected,
                    context = context,
                    onClick = {
                      val current = selectedOffsets.toMutableList()
                      if (isSelected) {
                        current.removeAll { it.daysBefore == option.daysBefore }
                      } else {
                        current.add(option)
                      }
                      selectedOffsets = current
                    },
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }

            // Custom offsets chips if present
            val customOffsets = selectedOffsets.filter { offset ->
              quickOptions.none { it.daysBefore == offset.daysBefore }
            }

            if (customOffsets.isNotEmpty()) {
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                customOffsets.forEach { customOffset ->
                  ReminderChip(
                    offset = customOffset,
                    isSelected = true,
                    context = context,
                    onClick = {
                      val current = selectedOffsets.toMutableList()
                      current.removeAll { it.daysBefore == customOffset.daysBefore }
                      selectedOffsets = current
                    }
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Custom Offset Button
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(extColors.cardBorderSubtle)
                .clickable { showCustomOffsetDialog = true }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("add_custom_offset_chip"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stringResource(R.string.reminder_custom_option),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Time Row
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable { showTimePicker = true }
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("reminder_time_picker_row"),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.Schedule,
                  contentDescription = null,
                  tint = extColors.goldAccent,
                  modifier = Modifier.size(20.dp)
                )
                Column {
                  Text(
                    text = stringResource(R.string.reminder_time_label),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = currentConfig.formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = extColors.textMuted
                  )
                }
              }

              Text(
                text = stringResource(R.string.reminder_change_button),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Schedule Preview
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(extColors.rosewoodContainer.copy(alpha = 0.4f))
                .padding(10.dp)
                .testTag("reminder_schedule_preview_box")
            ) {
              Text(
                text = nextTriggerPreview,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          SecondaryButton(
            text = stringResource(R.string.btn_cancel),
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .testTag("reminder_dialog_cancel")
          )

          PrimaryButton(
            text = stringResource(R.string.btn_save),
            onClick = {
              onSave(currentConfig)
            },
            modifier = Modifier
              .weight(1f)
              .testTag("reminder_dialog_save")
          )
        }
      }
    }
  }

  if (showTimePicker) {
    ReminderTimePickerDialog(
      initialHour = timeHour,
      initialMinute = timeMinute,
      onDismiss = { showTimePicker = false },
      onConfirm = { h, m ->
        timeHour = h
        timeMinute = m
        showTimePicker = false
      }
    )
  }

  if (showCustomOffsetDialog) {
    CustomReminderOffsetDialog(
      onDismiss = { showCustomOffsetDialog = false },
      onAddOffset = { days ->
        val current = selectedOffsets.toMutableList()
        if (current.none { it.daysBefore == days }) {
          current.add(ReminderOffset.custom(days))
          selectedOffsets = current
        }
        showCustomOffsetDialog = false
      }
    )
  }
}

@Composable
private fun ReminderChip(
  offset: ReminderOffset,
  isSelected: Boolean,
  context: android.content.Context,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorderSubtle)
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 8.dp)
      .testTag("offset_chip_${offset.daysBefore}"),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (isSelected) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier
            .size(14.dp)
            .padding(end = 2.dp)
        )
      }
      Text(
        text = offset.getShortLabel(context),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        ),
        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
      )
    }
  }
}
