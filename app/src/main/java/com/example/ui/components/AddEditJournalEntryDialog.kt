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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.models.JournalEntryModel
import com.example.data.models.StoryModel
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val AVAILABLE_JOURNAL_TAGS = listOf(
  "Travel",
  "Sunset",
  "Date Night",
  "Milestone",
  "Love Note",
  "Coffee",
  "First Date",
  "Adventure"
)

@Composable
fun AddEditJournalEntryDialog(
  entry: JournalEntryModel? = null,
  availableStories: List<StoryModel> = emptyList(),
  initialStoryId: String? = null,
  onDismiss: () -> Unit,
  onSave: (JournalEntryModel) -> Unit
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  val isEditing = entry != null

  var title by remember { mutableStateOf(entry?.title ?: "") }
  var content by remember { mutableStateOf(entry?.content ?: "") }
  var dateEpochDay by remember {
    mutableLongStateOf(entry?.dateEpochDay ?: LocalDate.now().toEpochDay())
  }
  var associatedStoryId by remember {
    mutableStateOf(entry?.associatedStoryId ?: initialStoryId)
  }
  var selectedTags by remember {
    mutableStateOf(entry?.tags?.toSet() ?: emptySet())
  }

  var showStoryDropdown by remember { mutableStateOf(false) }

  val selectedDate = LocalDate.ofEpochDay(dateEpochDay)
  val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

  val datePickerDialog = remember {
    DatePickerDialog(
      context,
      { _, year, month, dayOfMonth ->
        val chosenDate = LocalDate.of(year, month + 1, dayOfMonth)
        dateEpochDay = chosenDate.toEpochDay()
      },
      selectedDate.year,
      selectedDate.monthValue - 1,
      selectedDate.dayOfMonth
    )
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 20.dp)
        .testTag("add_edit_journal_dialog"),
      shape = RoundedCornerShape(26.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      shadowElevation = 10.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(if (isEditing) R.string.journal_edit_title else R.string.journal_create_title),
            style = MaterialTheme.typography.headlineSmall.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("journal_dialog_close")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(R.string.event_close),
              tint = extColors.textMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Title Input Field
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text(stringResource(R.string.journal_input_title)) },
          placeholder = { Text(stringResource(R.string.journal_input_title_hint)) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("journal_input_title"),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorder,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = extColors.rosewoodContainer.copy(alpha = 0.2f)
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Date Picker Row
        Text(
          text = stringResource(R.string.journal_input_date),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = extColors.textSecondary,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
            .testTag("journal_date_picker_btn"),
          shape = RoundedCornerShape(16.dp),
          color = extColors.rosewoodContainer.copy(alpha = 0.25f),
          border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Text(
              text = stringResource(R.string.date_picker_action),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Associated Story / Event Link Picker
        Text(
          text = stringResource(R.string.journal_connect_event_label),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = extColors.textSecondary,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
          val connectedStory = availableStories.find { it.id == associatedStoryId }
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showStoryDropdown = true }
              .testTag("journal_link_story_btn"),
            shape = RoundedCornerShape(16.dp),
            color = extColors.goldContainer.copy(alpha = 0.35f),
            border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
              ) {
                Icon(
                  imageVector = Icons.Outlined.Link,
                  contentDescription = null,
                  tint = extColors.goldAccent,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = connectedStory?.displayTitle ?: stringResource(R.string.journal_connect_none),
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1
                )
              }

              Text(
                text = "Select",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = extColors.goldAccent
                )
              )
            }
          }

          DropdownMenu(
            expanded = showStoryDropdown,
            onDismissRequest = { showStoryDropdown = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
          ) {
            DropdownMenuItem(
              text = { Text(stringResource(R.string.journal_connect_none)) },
              onClick = {
                associatedStoryId = null
                showStoryDropdown = false
              }
            )
            availableStories.forEach { story ->
              DropdownMenuItem(
                text = { Text(story.displayTitle) },
                onClick = {
                  associatedStoryId = story.id
                  showStoryDropdown = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tags Selection Row
        Text(
          text = stringResource(R.string.journal_tags_label),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = extColors.textSecondary,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          AVAILABLE_JOURNAL_TAGS.forEach { tag ->
            val isSelected = selectedTags.contains(tag)
            val tagBg = if (isSelected) MaterialTheme.colorScheme.primary else extColors.rosewoodContainer.copy(alpha = 0.4f)
            val tagColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else extColors.textSecondary

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(tagBg)
                .border(
                  width = 1.dp,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorder,
                  shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                  selectedTags = if (isSelected) {
                    selectedTags - tag
                  } else {
                    selectedTags + tag
                  }
                }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("tag_chip_$tag")
            ) {
              Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 11.5.sp
                ),
                color = tagColor
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Multiline Written Memory Content
        Text(
          text = stringResource(R.string.journal_input_content),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = extColors.textSecondary,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          placeholder = { Text(stringResource(R.string.journal_input_content_hint)) },
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp, max = 220.dp)
            .testTag("journal_input_content"),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorder,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = extColors.rosewoodContainer.copy(alpha = 0.2f)
          )
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          SecondaryButton(
            text = stringResource(R.string.btn_cancel),
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            testTag = "journal_cancel_btn"
          )

          PrimaryButton(
            text = stringResource(R.string.btn_save),
            onClick = {
              val finalTitle = title.trim().ifEmpty { "Memory on $formattedDate" }
              val updated = (entry ?: JournalEntryModel()).copy(
                title = finalTitle,
                content = content.trim(),
                dateEpochDay = dateEpochDay,
                associatedStoryId = associatedStoryId,
                tags = selectedTags.toList()
              )
              onSave(updated)
            },
            modifier = Modifier.weight(1.3f),
            testTag = "journal_save_btn"
          )
        }
      }
    }
  }
}
