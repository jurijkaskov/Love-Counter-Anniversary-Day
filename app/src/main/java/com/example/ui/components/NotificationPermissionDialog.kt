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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun NotificationPermissionDialog(
  onDismiss: () -> Unit,
  onRequestPermission: () -> Unit
) {
  val extColors = LocalCherishExtendedColors.current

  Dialog(onDismissRequest = onDismiss) {
    CherishCard(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("notification_permission_dialog")
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(extColors.rosewoodContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Outlined.NotificationsActive,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = stringResource(R.string.reminder_permission_dialog_title),
          style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = stringResource(R.string.reminder_permission_dialog_desc),
          style = MaterialTheme.typography.bodyMedium,
          color = extColors.textMuted,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          SecondaryButton(
            text = stringResource(R.string.reminder_permission_later),
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .testTag("permission_later_btn")
          )

          PrimaryButton(
            text = stringResource(R.string.reminder_permission_grant),
            onClick = {
              onDismiss()
              onRequestPermission()
            },
            modifier = Modifier
              .weight(1f)
              .testTag("permission_grant_btn")
          )
        }
      }
    }
  }
}
