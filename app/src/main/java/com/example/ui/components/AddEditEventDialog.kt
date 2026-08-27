package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.models.EventCategory
import com.example.data.models.ReminderConfig
import com.example.data.models.StoryModel
import com.example.ui.screens.create.AvailableCategoryOptions
import com.example.ui.screens.create.AvailableSymbols
import com.example.ui.screens.create.getIconForCategory
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun AddEditEventDialog(
  eventToEdit: StoryModel? = null,
  initialDateEpochDay: Long? = null,
  onDismiss: () -> Unit,
  onSave: (StoryModel) -> Unit
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  var selectedCategory by remember {
    mutableStateOf(eventToEdit?.category ?: EventCategory.WEDDING)
  }

  var titleText by remember {
    mutableStateOf(eventToEdit?.title ?: "")
  }

  var dateEpochDay by remember {
    mutableLongStateOf(eventToEdit?.dateEpochDay ?: initialDateEpochDay ?: LocalDate.now().toEpochDay())
  }

  var noteText by remember {
    mutableStateOf(eventToEdit?.note ?: "")
  }

  var selectedSymbolKey by remember {
    mutableStateOf(eventToEdit?.iconKey ?: "celebration")
  }

  var reminderConfig by remember {
    mutableStateOf(eventToEdit?.reminderConfig ?: ReminderConfig.defaultForCategory(selectedCategory))
  }

  var showReminderDialog by remember { mutableStateOf(false) }

  var errorMessage by remember { mutableStateOf<String?>(null) }

  val selectedDate = remember(dateEpochDay) { LocalDate.ofEpochDay(dateEpochDay) }
  val formattedDate = remember(selectedDate) {
    selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
  }

  val datePickerDialog = remember(selectedDate) {
    DatePickerDialog(
      context,
      { _, year, month, dayOfMonth ->
        val newDate = LocalDate.of(year, month + 1, dayOfMonth)
        dateEpochDay = newDate.toEpochDay()
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
        .fillMaxWidth(0.94f)
        .padding(vertical = 20.dp)
        .testTag("add_edit_event_dialog"),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      shadowElevation = 16.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (eventToEdit == null) stringResource(R.string.event_add_title) else stringResource(R.string.event_edit_title),
            style = MaterialTheme.typography.headlineSmall.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("add_edit_header_title")
          )

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_add_edit")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(R.string.event_close),
              tint = extColors.textMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Category Selection
        Text(
          text = stringResource(R.string.event_category_label),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          AvailableCategoryOptions.forEach { option ->
            val isSelected = option.category == selectedCategory
            val categoryTitle = stringResource(option.titleResId)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                  if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorderSubtle
                )
                .clickable {
                  selectedCategory = option.category
                  selectedSymbolKey = option.defaultIconKey
                  val currentDefaultTitle = context.resources.getString(selectedCategory.titleResId)
                  if (titleText.isBlank() || titleText == currentDefaultTitle) {
                    titleText = categoryTitle
                  }
                }
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("category_option_${option.category.id}"),
              contentAlignment = Alignment.Center
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = option.icon,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = categoryTitle,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  ),
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = stringResource(R.string.event_title_label),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = titleText,
          onValueChange = {
            titleText = it
            errorMessage = null
          },
          placeholder = {
            Text(
              text = stringResource(selectedCategory.titleResId),
              color = extColors.textMuted
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_event_title"),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorderSubtle
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Date Selection
        Text(
          text = stringResource(R.string.event_date_label),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))

        CherishCard(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_select_event_date"),
          shape = RoundedCornerShape(16.dp),
          containerColor = MaterialTheme.colorScheme.surface,
          borderColor = extColors.cardBorderSubtle,
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
          onClick = { datePickerDialog.show() }
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
              Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Text(
              text = stringResource(R.string.btn_change),
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = extColors.goldAccent
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Symbol Selection
        Text(
          text = stringResource(R.string.choose_symbol_label),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          AvailableSymbols.forEach { symbol ->
            val isSelected = symbol.key == selectedSymbolKey
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                  if (isSelected) extColors.goldContainer else extColors.cardBorderSubtle.copy(alpha = 0.5f)
                )
                .border(
                  width = if (isSelected) 2.dp else 1.dp,
                  color = if (isSelected) extColors.goldAccent else Color.Transparent,
                  shape = CircleShape
                )
                .clickable { selectedSymbolKey = symbol.key }
                .testTag("symbol_option_${symbol.key}"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = symbol.icon,
                contentDescription = stringResource(symbol.labelResId),
                tint = if (isSelected) extColors.goldAccent else extColors.textMuted,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Note / Memory
        Text(
          text = stringResource(R.string.input_personal_note),
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = noteText,
          onValueChange = { noteText = it },
          placeholder = {
            Text(
              text = stringResource(R.string.input_personal_note_hint),
              color = extColors.textMuted
            )
          },
          maxLines = 3,
          minLines = 2,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_event_note"),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorderSubtle
          )
        )

        // 6. Reminders Section
        Spacer(modifier = Modifier.height(18.dp))
        ReminderSummarySection(
          reminderConfig = reminderConfig,
          onConfigureClick = { showReminderDialog = true },
          modifier = Modifier.testTag("add_edit_reminder_section")
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = errorMessage!!,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.testTag("add_edit_error_message")
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save & Cancel Buttons
        Row(
          modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          TextButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
              .testTag("btn_cancel_add_edit"),
            shape = RoundedCornerShape(24.dp)
          ) {
            Text(
              text = stringResource(R.string.btn_cancel),
              style = MaterialTheme.typography.labelLarge,
              color = extColors.textMuted
            )
          }

          Button(
            onClick = {
              val finalTitle = titleText.trim().ifBlank { context.getString(selectedCategory.titleResId) }
              val updatedModel = StoryModel(
                id = eventToEdit?.id ?: UUID.randomUUID().toString(),
                category = selectedCategory,
                yourName = eventToEdit?.yourName ?: "",
                partnerName = eventToEdit?.partnerName ?: "",
                title = finalTitle,
                dateEpochDay = dateEpochDay,
                note = noteText.trim(),
                iconKey = selectedSymbolKey,
                themeAccent = eventToEdit?.themeAccent ?: "rosewood",
                isPrimary = eventToEdit?.isPrimary ?: false,
                isFavorite = eventToEdit?.isFavorite ?: false,
                reminderConfig = reminderConfig,
                createdAtEpochMillis = eventToEdit?.createdAtEpochMillis ?: System.currentTimeMillis()
              )
              onSave(updatedModel)
              onDismiss()
            },
            modifier = Modifier
              .weight(1.5f)
              .fillMaxHeight()
              .testTag("btn_save_event"),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            )
          ) {
            Text(
              text = stringResource(R.string.btn_save),
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }

  if (showReminderDialog) {
    val tempStory = StoryModel(
      id = eventToEdit?.id ?: "temp-story",
      category = selectedCategory,
      title = titleText.ifBlank { context.getString(selectedCategory.titleResId) },
      dateEpochDay = dateEpochDay,
      reminderConfig = reminderConfig
    )
    ReminderSettingsDialog(
      story = tempStory,
      onDismiss = { showReminderDialog = false },
      onSave = { updated ->
        reminderConfig = updated
        showReminderDialog = false
      }
    )
  }
}
