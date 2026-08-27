package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun CustomReminderOffsetDialog(
  onDismiss: () -> Unit,
  onAddOffset: (daysBefore: Int) -> Unit
) {
  val extColors = LocalCherishExtendedColors.current
  var daysText by remember { mutableStateOf("5") }
  val daysNumber = daysText.toIntOrNull() ?: 0

  Dialog(onDismissRequest = onDismiss) {
    CherishCard(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("custom_reminder_offset_dialog")
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
              imageVector = Icons.Outlined.NotificationsActive,
              contentDescription = null,
              tint = extColors.goldAccent,
              modifier = Modifier.size(20.dp)
            )
          }

          Text(
            text = stringResource(R.string.reminder_custom_dialog_title),
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = daysText,
          onValueChange = { input ->
            if (input.all { it.isDigit() } && input.length <= 3) {
              daysText = input
            }
          },
          label = { Text(stringResource(R.string.reminder_custom_dialog_hint)) },
          placeholder = { Text("5") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorderSubtle,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("custom_days_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = if (daysNumber > 0) {
            stringResource(R.string.reminder_custom_preview_format, daysNumber)
          } else {
            stringResource(R.string.reminder_custom_error_invalid)
          },
          style = MaterialTheme.typography.bodySmall,
          color = extColors.textMuted,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          SecondaryButton(
            text = stringResource(R.string.btn_cancel),
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .testTag("custom_offset_cancel")
          )

          PrimaryButton(
            text = stringResource(R.string.btn_add),
            onClick = {
              if (daysNumber > 0) {
                onAddOffset(daysNumber)
              }
            },
            enabled = daysNumber > 0,
            modifier = Modifier
              .weight(1f)
              .testTag("custom_offset_add")
          )
        }
      }
    }
  }
}
