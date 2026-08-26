package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.models.MilestoneCategory
import com.example.data.models.MilestoneModel
import com.example.data.models.StoryModel
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMilestoneDialog(
  milestoneToEdit: MilestoneModel?,
  availableStories: List<StoryModel>,
  onDismiss: () -> Unit,
  onSaveMilestone: (MilestoneModel) -> Unit,
  onDeleteMilestone: ((String) -> Unit)? = null
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  var title by remember { mutableStateOf(milestoneToEdit?.title ?: "") }
  var category by remember { mutableStateOf(milestoneToEdit?.category ?: MilestoneCategory.CUSTOM) }
  var description by remember { mutableStateOf(milestoneToEdit?.description ?: "") }
  var targetDateEpochDay by remember { mutableStateOf(milestoneToEdit?.targetDateEpochDay) }
  var associatedStoryId by remember { mutableStateOf(milestoneToEdit?.associatedStoryId) }
  var showDeleteConfirm by remember { mutableStateOf(false) }
  var dropdownExpanded by remember { mutableStateOf(false) }

  val targetDateFormatted = remember(targetDateEpochDay) {
    targetDateEpochDay?.let {
      LocalDate.ofEpochDay(it).format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }
  }

  val selectedStory = remember(associatedStoryId, availableStories) {
    availableStories.find { it.id == associatedStoryId }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .testTag("add_edit_milestone_dialog"),
      color = MaterialTheme.colorScheme.surface,
      shape = RoundedCornerShape(24.dp),
      shadowElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(24.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(
              if (milestoneToEdit == null) R.string.milestones_create_title else R.string.milestones_edit_title
            ),
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_milestone_dialog_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = extColors.textMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Journey Category Selector Chips
        Text(
          text = stringResource(R.string.milestones_category_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MilestoneCategory.entries.forEach { cat ->
            val isSelected = cat == category
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { category = cat }
                .testTag("category_chip_${cat.id}"),
              color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
              shape = RoundedCornerShape(20.dp),
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = getIconForMilestoneCategory(cat),
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = stringResource(cat.titleResId),
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                  ),
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title Input
        Text(
          text = stringResource(R.string.milestones_title_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          placeholder = {
            Text(
              text = stringResource(R.string.milestones_title_hint),
              color = extColors.textMuted
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_title_input"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorder
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description / Context Note
        Text(
          text = stringResource(R.string.milestones_desc_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          placeholder = {
            Text(
              text = stringResource(R.string.milestones_desc_hint),
              color = extColors.textMuted
            )
          },
          maxLines = 3,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_desc_input"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorder
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Target Date Picker
        Text(
          text = stringResource(R.string.milestones_target_date_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable {
              val initial = targetDateEpochDay?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now().plusMonths(6)
              DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                  val picked = LocalDate.of(year, month + 1, dayOfMonth)
                  targetDateEpochDay = picked.toEpochDay()
                },
                initial.year,
                initial.monthValue - 1,
                initial.dayOfMonth
              ).show()
            }
            .testTag("milestone_date_picker_button"),
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
                text = targetDateFormatted ?: "Select target date",
                style = MaterialTheme.typography.bodyMedium,
                color = if (targetDateFormatted != null) MaterialTheme.colorScheme.onSurface else extColors.textMuted
              )
            }
            if (targetDateEpochDay != null) {
              Text(
                text = "Clear",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = extColors.textMuted,
                modifier = Modifier.clickable { targetDateEpochDay = null }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Connect to Important Event Dropdown
        Text(
          text = stringResource(R.string.milestones_linked_event_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
          expanded = dropdownExpanded,
          onExpandedChange = { dropdownExpanded = it },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = selectedStory?.title ?: stringResource(R.string.milestones_none_linked),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
              ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
            },
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
              .fillMaxWidth()
              .testTag("linked_event_dropdown"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = extColors.cardBorder
            )
          )

          ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text(stringResource(R.string.milestones_none_linked)) },
              onClick = {
                associatedStoryId = null
                dropdownExpanded = false
              }
            )
            availableStories.forEach { story ->
              DropdownMenuItem(
                text = { Text(story.title) },
                onClick = {
                  associatedStoryId = story.id
                  // If target date is empty, sync with story date
                  if (targetDateEpochDay == null) {
                    targetDateEpochDay = story.dateEpochDay
                  }
                  dropdownExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (milestoneToEdit != null && onDeleteMilestone != null) {
            SecondaryButton(
              text = "Delete",
              onClick = { showDeleteConfirm = true },
              modifier = Modifier.weight(1f),
              icon = Icons.Default.Delete,
              testTag = "delete_milestone_button"
            )
          }

          PrimaryButton(
            text = stringResource(R.string.btn_save),
            onClick = {
              if (title.isNotBlank()) {
                val milestone = milestoneToEdit?.copy(
                  title = title.trim(),
                  category = category,
                  description = description.trim(),
                  targetDateEpochDay = targetDateEpochDay,
                  associatedStoryId = associatedStoryId,
                  iconKey = category.defaultIconKey
                ) ?: MilestoneModel(
                  id = UUID.randomUUID().toString(),
                  title = title.trim(),
                  category = category,
                  description = description.trim(),
                  targetDateEpochDay = targetDateEpochDay,
                  associatedStoryId = associatedStoryId,
                  iconKey = category.defaultIconKey
                )
                onSaveMilestone(milestone)
              }
            },
            enabled = title.isNotBlank(),
            modifier = Modifier.weight(if (milestoneToEdit != null) 1.4f else 1f),
            testTag = "save_milestone_button"
          )
        }
      }
    }
  }

  if (showDeleteConfirm && milestoneToEdit != null && onDeleteMilestone != null) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = {
        Text(
          text = stringResource(R.string.milestone_delete_dialog_title),
          fontFamily = FontFamily.Serif
        )
      },
      text = {
        Text(text = stringResource(R.string.milestone_delete_dialog_msg, milestoneToEdit.title))
      },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteConfirm = false
            onDeleteMilestone(milestoneToEdit.id)
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

fun getIconForMilestoneCategory(category: MilestoneCategory): ImageVector {
  return when (category) {
    MilestoneCategory.WEDDING -> Icons.Outlined.Celebration
    MilestoneCategory.ANNIVERSARY -> Icons.Default.Favorite
    MilestoneCategory.TRIP -> Icons.Default.Flight
    MilestoneCategory.CUSTOM -> Icons.Default.Star
  }
}
