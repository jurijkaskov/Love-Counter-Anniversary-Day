package com.example.ui.components

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.models.MilestoneTaskModel
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun AddEditTaskDialog(
  milestoneId: String,
  taskToEdit: MilestoneTaskModel?,
  onDismiss: () -> Unit,
  onSaveTask: (MilestoneTaskModel) -> Unit,
  onDeleteTask: ((String) -> Unit)? = null
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
  var note by remember { mutableStateOf(taskToEdit?.note ?: "") }
  var dueDateEpochDay by remember { mutableStateOf(taskToEdit?.dueDateEpochDay) }
  var showDeleteConfirm by remember { mutableStateOf(false) }

  val dueDateFormatted = remember(dueDateEpochDay) {
    dueDateEpochDay?.let {
      LocalDate.ofEpochDay(it).format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .testTag("add_edit_task_dialog"),
      color = MaterialTheme.colorScheme.surface,
      shape = RoundedCornerShape(24.dp),
      shadowElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        // Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(
              if (taskToEdit == null) R.string.task_add_dialog_title else R.string.task_edit_dialog_title
            ),
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_task_dialog_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = extColors.textMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Task Title Field
        Text(
          text = stringResource(R.string.task_title_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          placeholder = {
            Text(
              text = stringResource(R.string.task_title_hint),
              color = extColors.textMuted
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_title_input"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorder
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Due Date Picker Button
        Text(
          text = stringResource(R.string.task_due_date_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable {
              val initialDate = dueDateEpochDay?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now()
              DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                  val picked = LocalDate.of(year, month + 1, dayOfMonth)
                  dueDateEpochDay = picked.toEpochDay()
                },
                initialDate.year,
                initialDate.monthValue - 1,
                initialDate.dayOfMonth
              ).show()
            }
            .testTag("task_date_picker_button"),
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(14.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = dueDateFormatted ?: "Optional due date",
                style = MaterialTheme.typography.bodyMedium,
                color = if (dueDateFormatted != null) MaterialTheme.colorScheme.onSurface else extColors.textMuted
              )
            }
            if (dueDateEpochDay != null) {
              Text(
                text = "Clear",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = extColors.textMuted,
                modifier = Modifier.clickable { dueDateEpochDay = null }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Note Field
        Text(
          text = stringResource(R.string.task_note_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          placeholder = {
            Text(
              text = stringResource(R.string.task_note_hint),
              color = extColors.textMuted
            )
          },
          maxLines = 3,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_note_input"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorder
          )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (taskToEdit != null && onDeleteTask != null) {
            SecondaryButton(
              text = "Delete",
              onClick = { showDeleteConfirm = true },
              modifier = Modifier.weight(1f),
              icon = Icons.Default.Delete,
              testTag = "delete_task_button"
            )
          }

          PrimaryButton(
            text = stringResource(R.string.btn_save),
            onClick = {
              if (title.isNotBlank()) {
                val task = taskToEdit?.copy(
                  title = title.trim(),
                  note = note.trim(),
                  dueDateEpochDay = dueDateEpochDay
                ) ?: MilestoneTaskModel(
                  id = UUID.randomUUID().toString(),
                  milestoneId = milestoneId,
                  title = title.trim(),
                  isCompleted = false,
                  dueDateEpochDay = dueDateEpochDay,
                  note = note.trim()
                )
                onSaveTask(task)
              }
            },
            enabled = title.isNotBlank(),
            modifier = Modifier.weight(if (taskToEdit != null) 1.4f else 1f),
            testTag = "save_task_button"
          )
        }
      }
    }
  }

  if (showDeleteConfirm && taskToEdit != null && onDeleteTask != null) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = {
        Text(
          text = stringResource(R.string.task_delete_dialog_title),
          fontFamily = FontFamily.Serif
        )
      },
      text = {
        Text(text = stringResource(R.string.task_delete_dialog_msg, taskToEdit.title))
      },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteConfirm = false
            onDeleteTask(taskToEdit.id)
          }
        ) {
          Text(text = "Delete", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text(text = stringResource(R.string.btn_cancel))
        }
      }
    )
  }
}
