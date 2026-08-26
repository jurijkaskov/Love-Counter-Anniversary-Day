package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalCherishExtendedColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDataDialog(
  storiesCount: Int,
  milestonesCount: Int,
  tasksCount: Int,
  onGenerateExportJson: () -> String,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp)
      .testTag("export_data_dialog")
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder),
      shadowElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        // Header
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(extColors.rosewoodContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.Backup,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
          }

          Column {
            Text(
              text = stringResource(R.string.settings_backup_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = stringResource(R.string.settings_backup_subtitle),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Data Summary Card
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = stringResource(R.string.settings_backup_ready_label),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
              ),
              color = MaterialTheme.colorScheme.onSurface
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              ExportStatItem(label = stringResource(R.string.settings_backup_stories_label), count = storiesCount)
              ExportStatItem(label = stringResource(R.string.settings_backup_milestones_label), count = milestonesCount)
              ExportStatItem(label = stringResource(R.string.settings_backup_tasks_label), count = tasksCount)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = stringResource(R.string.settings_backup_desc_footer),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Actions
        Column(
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          PrimaryButton(
            text = stringResource(R.string.settings_backup_share_btn),
            icon = Icons.Default.Share,
            onClick = {
              val json = onGenerateExportJson()
              val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Cherish Relationship Backup")
                putExtra(Intent.EXTRA_TEXT, json)
              }
              val chooser = Intent.createChooser(sendIntent, "Export Cherish Data")
              context.startActivity(chooser)
              onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "btn_share_export"
          )

          SecondaryButton(
            text = stringResource(R.string.settings_backup_copy_btn),
            icon = Icons.Default.ContentCopy,
            onClick = {
              val json = onGenerateExportJson()
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
              val clip = ClipData.newPlainText("Cherish Backup", json)
              clipboard?.setPrimaryClip(clip)
              Toast.makeText(context, context.getString(R.string.settings_backup_copied_toast), Toast.LENGTH_SHORT).show()
              onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "btn_copy_export"
          )

          SecondaryButton(
            text = stringResource(R.string.event_close),
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}

@Composable
private fun ExportStatItem(
  label: String,
  count: Int
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = count.toString(),
      style = MaterialTheme.typography.titleLarge.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.primary
    )
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
