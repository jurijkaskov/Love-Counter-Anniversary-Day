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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.models.ReminderConfig
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun ReminderSummarySection(
  reminderConfig: ReminderConfig,
  onConfigureClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  val isEnabled = reminderConfig.isEnabled && reminderConfig.offsets.isNotEmpty()

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = stringResource(R.string.reminder_summary_header),
      style = MaterialTheme.typography.labelMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold
      ),
      color = extColors.textMuted
    )

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        .clickable { onConfigureClick() }
        .padding(horizontal = 14.dp, vertical = 12.dp)
        .testTag("reminder_summary_card"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isEnabled) extColors.goldContainer else extColors.cardBorderSubtle),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isEnabled) Icons.Outlined.NotificationsActive else Icons.Outlined.NotificationsOff,
            contentDescription = null,
            tint = if (isEnabled) extColors.goldAccent else extColors.textMuted,
            modifier = Modifier.size(18.dp)
          )
        }

        Column {
          Text(
            text = if (isEnabled) {
              reminderConfig.getSummaryText(context)
            } else {
              stringResource(R.string.reminder_no_reminders)
            },
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
          )

          if (isEnabled) {
            Text(
              text = stringResource(R.string.reminder_time_at_format, reminderConfig.formattedTime),
              style = MaterialTheme.typography.bodySmall,
              color = extColors.textMuted
            )
          }
        }
      }

      Text(
        text = stringResource(R.string.reminder_change_button),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp)
      )
    }
  }
}
