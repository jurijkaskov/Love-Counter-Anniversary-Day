package com.example.ui.components

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.StoryModel
import com.example.ui.theme.LocalCherishExtendedColors
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddEditPhotoDialog(
  photoToEdit: MemoryPhotoModel? = null,
  availableStories: List<StoryModel> = emptyList(),
  availableJournalEntries: List<JournalEntryModel> = emptyList(),
  initialStoryId: String? = null,
  initialJournalId: String? = null,
  onDismiss: () -> Unit,
  onSave: (dateEpochDay: Long, caption: String, selectedUri: Uri?, storyId: String?, journalId: String?) -> Unit
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  val isEditing = photoToEdit != null

  var selectedUri by remember { mutableStateOf<Uri?>(null) }
  var caption by remember { mutableStateOf(photoToEdit?.caption ?: "") }
  var dateEpochDay by remember {
    mutableLongStateOf(
      photoToEdit?.dateEpochDay
        ?: availableStories.find { it.id == initialStoryId }?.dateEpochDay
        ?: LocalDate.now().toEpochDay()
    )
  }
  var selectedStoryId by remember { mutableStateOf(photoToEdit?.associatedStoryId ?: initialStoryId) }
  var selectedJournalId by remember { mutableStateOf(photoToEdit?.associatedJournalId ?: initialJournalId) }

  var showStoryDropdown by remember { mutableStateOf(false) }
  var showJournalDropdown by remember { mutableStateOf(false) }

  // Photo Picker launcher
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      selectedUri = uri
    }
  }

  // DatePickerDialog
  val localDate = remember(dateEpochDay) { LocalDate.ofEpochDay(dateEpochDay) }
  val datePickerDialog = remember(dateEpochDay) {
    DatePickerDialog(
      context,
      { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
        val chosen = LocalDate.of(year, month + 1, dayOfMonth)
        dateEpochDay = chosen.toEpochDay()
      },
      localDate.year,
      localDate.monthValue - 1,
      localDate.dayOfMonth
    )
  }

  val selectedStory = availableStories.find { it.id == selectedStoryId }
  val selectedJournal = availableJournalEntries.find { it.id == selectedJournalId }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp)
        .clip(RoundedCornerShape(28.dp))
        .testTag("add_edit_photo_dialog"),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(24.dp)
      ) {
        // Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = stringResource(if (isEditing) R.string.photos_dialog_edit_title else R.string.photos_dialog_add_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = stringResource(R.string.photos_subtitle),
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Serif,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
              ),
              color = extColors.textMuted
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_photo_dialog")
          ) {
            Icon(
              imageVector = Icons.Outlined.Close,
              contentDescription = "Close",
              tint = extColors.textMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Image Picker Area / Preview
        val previewModel: Any? = when {
          selectedUri != null -> selectedUri
          photoToEdit != null -> File(File(context.filesDir, "memory_photos"), photoToEdit.filePath)
          else -> null
        }

        if (previewModel != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(1.2f)
              .clip(RoundedCornerShape(18.dp))
              .border(1.dp, extColors.cardBorderSubtle, RoundedCornerShape(18.dp))
          ) {
            AsyncImage(
              model = ImageRequest.Builder(context)
                .data(previewModel)
                .crossfade(true)
                .build(),
              contentDescription = "Selected memory photo",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth()
            )

            // Change Photo Chip
            Surface(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                }
                .testTag("btn_change_photo"),
              shape = RoundedCornerShape(12.dp),
              color = Color.Black.copy(alpha = 0.65f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.PhotoCamera,
                  contentDescription = stringResource(R.string.btn_change),
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = stringResource(R.string.photos_change_photo),
                  style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                )
              }
            }
          }
        } else {
          // Select Photo Placeholder Card
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
              .clip(RoundedCornerShape(18.dp))
              .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                shape = RoundedCornerShape(18.dp)
              )
              .clickable {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              }
              .testTag("btn_select_photo"),
            shape = RoundedCornerShape(18.dp),
            color = extColors.rosewoodContainer.copy(alpha = 0.35f)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Outlined.AddPhotoAlternate,
                  contentDescription = "Add Photo",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(26.dp)
                )
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = stringResource(R.string.photos_select_prompt),
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Date Selector Row
        Text(
          text = stringResource(R.string.photos_date_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = extColors.textMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, extColors.cardBorder, RoundedCornerShape(14.dp))
            .clickable { datePickerDialog.show() }
            .testTag("btn_photo_date"),
          shape = RoundedCornerShape(14.dp),
          color = extColors.rosewoodContainer.copy(alpha = 0.3f)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
            Text(
              text = localDate.format(formatter),
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Serif
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
              imageVector = Icons.Outlined.CalendarToday,
              contentDescription = stringResource(R.string.date_picker_action),
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Caption / Note Field
        Text(
          text = stringResource(R.string.photos_caption_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = extColors.textMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = caption,
          onValueChange = { caption = it },
          placeholder = {
            Text(
              text = stringResource(R.string.photos_caption_hint),
              style = MaterialTheme.typography.bodyMedium,
              color = extColors.textMuted.copy(alpha = 0.7f)
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_photo_caption"),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorder,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
          ),
          maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Connect to Important Moment (Story)
        if (availableStories.isNotEmpty()) {
          Text(
            text = stringResource(R.string.photos_link_story_label),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = extColors.textMuted
          )
          Spacer(modifier = Modifier.height(6.dp))
          Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, extColors.cardBorder, RoundedCornerShape(14.dp))
                .clickable { showStoryDropdown = true }
                .testTag("btn_select_story_link"),
              shape = RoundedCornerShape(14.dp),
              color = extColors.rosewoodContainer.copy(alpha = 0.25f)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = selectedStory?.displayTitle ?: stringResource(R.string.photos_link_none),
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selectedStory != null) FontWeight.SemiBold else FontWeight.Normal,
                    fontFamily = FontFamily.Serif
                  ),
                  color = if (selectedStory != null) MaterialTheme.colorScheme.primary else extColors.textMuted
                )
                Icon(
                  imageVector = Icons.Outlined.Collections,
                  contentDescription = stringResource(R.string.event_category_label),
                  tint = extColors.textMuted,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            DropdownMenu(
              expanded = showStoryDropdown,
              onDismissRequest = { showStoryDropdown = false },
              modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
              DropdownMenuItem(
                text = { Text(stringResource(R.string.photos_link_none)) },
                onClick = {
                  selectedStoryId = null
                  showStoryDropdown = false
                }
              )
              availableStories.forEach { story ->
                DropdownMenuItem(
                  text = { Text("${story.displayTitle} (${story.formattedDate})") },
                  onClick = {
                    selectedStoryId = story.id
                    // Auto-sync date if desired
                    dateEpochDay = story.dateEpochDay
                    showStoryDropdown = false
                  }
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
        }

        // Connect to Journal Entry
        if (availableJournalEntries.isNotEmpty()) {
          Text(
            text = stringResource(R.string.photos_link_journal_label),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = extColors.textMuted
          )
          Spacer(modifier = Modifier.height(6.dp))
          Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, extColors.cardBorder, RoundedCornerShape(14.dp))
                .clickable { showJournalDropdown = true }
                .testTag("btn_select_journal_link"),
              shape = RoundedCornerShape(14.dp),
              color = extColors.rosewoodContainer.copy(alpha = 0.25f)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = selectedJournal?.displayTitle ?: stringResource(R.string.photos_link_none),
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (selectedJournal != null) FontWeight.SemiBold else FontWeight.Normal,
                    fontFamily = FontFamily.Serif
                  ),
                  color = if (selectedJournal != null) MaterialTheme.colorScheme.primary else extColors.textMuted
                )
                Icon(
                  imageVector = Icons.Outlined.MenuBook,
                  contentDescription = stringResource(R.string.journal_title),
                  tint = extColors.textMuted,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            DropdownMenu(
              expanded = showJournalDropdown,
              onDismissRequest = { showJournalDropdown = false },
              modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
              DropdownMenuItem(
                text = { Text(stringResource(R.string.photos_link_none)) },
                onClick = {
                  selectedJournalId = null
                  showJournalDropdown = false
                }
              )
              availableJournalEntries.forEach { entry ->
                DropdownMenuItem(
                  text = { Text("${entry.displayTitle} (${entry.formattedDate})") },
                  onClick = {
                    selectedJournalId = entry.id
                    dateEpochDay = entry.dateEpochDay
                    showJournalDropdown = false
                  }
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(20.dp))
        }

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          SecondaryButton(
            text = stringResource(R.string.btn_cancel),
            onClick = onDismiss,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            testTag = "btn_cancel_photo"
          )

          val canSave = selectedUri != null || isEditing
          PrimaryButton(
            text = stringResource(R.string.btn_save),
            onClick = {
              if (canSave) {
                onSave(dateEpochDay, caption, selectedUri, selectedStoryId, selectedJournalId)
              }
            },
            enabled = canSave,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            testTag = "btn_save_photo"
          )
        }
      }
    }
  }
}
