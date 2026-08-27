package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.StoryModel
import com.example.ui.screens.create.getIconForStory
import com.example.ui.share.ShareCardDialog
import com.example.ui.share.ShareCardPayloadFactory
import com.example.ui.theme.LocalCherishExtendedColors
import java.io.File

@Composable
fun EventDetailsDialog(
  event: StoryModel,
  onDismiss: () -> Unit,
  onEdit: (StoryModel) -> Unit,
  onDelete: (String) -> Unit,
  onToggleFavorite: (String) -> Unit,
  onUpdateStory: (StoryModel) -> Unit = {},
  relatedEntries: List<JournalEntryModel> = emptyList(),
  relatedPhotos: List<MemoryPhotoModel> = emptyList(),
  onAddMemory: (String) -> Unit = {},
  onAddPhoto: (String) -> Unit = {},
  onEntryClick: (JournalEntryModel) -> Unit = {},
  onPhotoClick: (MemoryPhotoModel) -> Unit = {}
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  var showDeleteConfirmation by remember { mutableStateOf(false) }
  var showReminderDialog by remember { mutableStateOf(false) }
  var showShareCardDialog by remember { mutableStateOf(false) }
  val icon = getIconForStory(event)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp)
        .testTag("event_details_dialog"),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      shadowElevation = 12.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top navigation row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { onToggleFavorite(event.id) },
              modifier = Modifier.testTag("details_favorite_btn")
            ) {
              Icon(
                imageVector = if (event.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (event.isFavorite) stringResource(R.string.milestone_completed_tag) else stringResource(R.string.nav_moments),
                tint = if (event.isFavorite) MaterialTheme.colorScheme.primary else extColors.textMuted
              )
            }

            IconButton(
              onClick = { showShareCardDialog = true },
              modifier = Modifier.testTag("details_share_btn")
            ) {
              Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = stringResource(R.string.share_card_btn_share_moment),
                tint = extColors.textMuted
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(extColors.goldContainer)
              .padding(horizontal = 12.dp, vertical = 5.dp)
              .testTag("details_category_badge")
          ) {
            Text(
              text = stringResource(event.category.titleResId),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = extColors.goldAccent
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("details_close_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(R.string.event_close),
              tint = extColors.textMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large icon container
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(
                  extColors.goldContainer,
                  extColors.rosewoodContainer
                )
              )
            )
            .testTag("details_icon_container"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(34.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
          text = event.getDisplayTitle(context),
          style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center,
          modifier = Modifier.testTag("details_title")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Original or next occurrence date
        Text(
          text = event.formattedDate,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.testTag("details_date")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2 Stat cards in a row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Card 1: Countdown or Elapsed
          CherishCard(
            modifier = Modifier
              .weight(1f)
              .testTag("details_stat_countdown"),
            shape = RoundedCornerShape(18.dp),
            containerColor = extColors.rosewoodContainer.copy(alpha = 0.5f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
          ) {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = when {
                  event.isToday -> stringResource(R.string.countdown_today_tag)
                  event.isPastDate -> stringResource(R.string.event_time_together_label)
                  else -> stringResource(R.string.event_days_remaining_label)
                },
                style = MaterialTheme.typography.labelSmall,
                color = extColors.textMuted
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = when {
                  event.isToday -> stringResource(R.string.model_countdown_today) + " 🎉"
                  event.isPastDate -> event.getElapsedBadgeText(context)
                  else -> event.getCountdownBadgeText(context)
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          // Card 2: Calendar Breakdown
          CherishCard(
            modifier = Modifier
              .weight(1f)
              .testTag("details_stat_duration"),
            shape = RoundedCornerShape(18.dp),
            containerColor = extColors.goldContainer.copy(alpha = 0.5f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
          ) {
            Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = extColors.goldAccent,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = stringResource(R.string.event_exact_duration_label),
                style = MaterialTheme.typography.labelSmall,
                color = extColors.textMuted
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = event.getFormattedPeriodBreakdown(context),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
              )
            }
          }
        }

        // Note section
        if (event.note.isNotBlank()) {
          Spacer(modifier = Modifier.height(18.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .background(extColors.cardBorderSubtle.copy(alpha = 0.4f))
              .padding(16.dp)
              .testTag("details_note_card")
          ) {
            Column {
              Text(
                text = stringResource(R.string.event_note_label),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = extColors.goldAccent
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "“${event.note}”",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontFamily = FontFamily.Serif,
                  lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        // Reminders section
        Spacer(modifier = Modifier.height(18.dp))
        ReminderSummarySection(
          reminderConfig = event.reminderConfig,
          onConfigureClick = { showReminderDialog = true },
          modifier = Modifier.testTag("details_reminder_section")
        )

        // Related Memories Section
        Spacer(modifier = Modifier.height(18.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(extColors.goldContainer.copy(alpha = 0.35f))
            .padding(16.dp)
            .testTag("details_memories_section")
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.journal_related_memories_header),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = extColors.goldAccent
              )

              TextButton(
                onClick = {
                  onDismiss()
                  onAddMemory(event.id)
                },
                modifier = Modifier.testTag("btn_add_memory_for_event")
              ) {
                Text(
                  text = "+ Write Memory",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }
            }

            if (relatedEntries.isEmpty()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = stringResource(R.string.journal_no_related_memories),
                style = MaterialTheme.typography.bodySmall,
                color = extColors.textMuted
              )
            } else {
              Spacer(modifier = Modifier.height(8.dp))
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                relatedEntries.forEach { entry ->
                  Surface(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable {
                        onDismiss()
                        onEntryClick(entry)
                      },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorderSubtle)
                  ) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = entry.getDisplayTitle(context),
                          style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Serif
                          ),
                          color = MaterialTheme.colorScheme.onSurface
                        )
                        if (entry.content.isNotBlank()) {
                          Text(
                            text = entry.previewSnippet,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                          )
                        }
                      }
                      Text(
                        text = entry.formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = extColors.textMuted
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // Related Photo Memories Section
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          color = extColors.rosewoodContainer.copy(alpha = 0.35f),
          border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorderSubtle)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.photos_connected_title),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )

              TextButton(
                onClick = {
                  onDismiss()
                  onAddPhoto(event.id)
                },
                modifier = Modifier.testTag("btn_add_photo_for_event")
              ) {
                Icon(
                  imageVector = Icons.Outlined.AddPhotoAlternate,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = stringResource(R.string.photos_add_to_story),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }
            }

            if (relatedPhotos.isEmpty()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = stringResource(R.string.photos_connected_empty),
                style = MaterialTheme.typography.bodySmall.copy(
                  fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = extColors.textMuted
              )
            } else {
              Spacer(modifier = Modifier.height(8.dp))
              LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                items(relatedPhotos, key = { it.id }) { photo ->
                  val photoFile = File(File(context.filesDir, "memory_photos"), photo.filePath)
                  Box(
                    modifier = Modifier
                      .size(72.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .border(1.dp, extColors.cardBorderSubtle, RoundedCornerShape(12.dp))
                      .clickable {
                        onDismiss()
                        onPhotoClick(photo)
                      }
                      .testTag("event_photo_thumb_${photo.id}")
                  ) {
                    SubcomposeAsyncImage(
                      model = ImageRequest.Builder(context)
                        .data(photoFile)
                        .crossfade(true)
                        .build(),
                      contentDescription = photo.caption.ifBlank { "Memory Photo" },
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              onDismiss()
              onEdit(event)
            },
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("btn_edit_moment"),
            shape = RoundedCornerShape(22.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = stringResource(R.string.event_edit_button),
              style = MaterialTheme.typography.labelMedium
            )
          }

          OutlinedButton(
            onClick = { showShareCardDialog = true },
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("btn_share_moment_card"),
            shape = RoundedCornerShape(22.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.Share,
              contentDescription = null,
              modifier = Modifier.size(15.dp),
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = stringResource(R.string.share_card_btn_share_moment),
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.primary
            )
          }

          TextButton(
            onClick = { showDeleteConfirmation = true },
            modifier = Modifier
              .weight(0.9f)
              .height(44.dp)
              .testTag("btn_delete_moment"),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.textButtonColors(
              contentColor = MaterialTheme.colorScheme.error
            )
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = null,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = stringResource(R.string.event_delete_button),
              style = MaterialTheme.typography.labelMedium
            )
          }
        }
      }
    }
  }

  if (showShareCardDialog) {
    ShareCardDialog(
      payload = ShareCardPayloadFactory.fromStory(context, event, relatedPhotos.firstOrNull()),
      onDismiss = { showShareCardDialog = false }
    )
  }

  if (showReminderDialog) {
    ReminderSettingsDialog(
      story = event,
      onDismiss = { showReminderDialog = false },
      onSave = { updatedConfig ->
        val updatedStory = event.copy(reminderConfig = updatedConfig)
        onUpdateStory(updatedStory)
        showReminderDialog = false
      }
    )
  }

  if (showDeleteConfirmation) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmation = false },
      title = {
        Text(
          text = stringResource(R.string.event_delete_confirm_title),
          style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          )
        )
      },
      text = {
        Text(
          text = stringResource(R.string.event_delete_confirm_message, event.getDisplayTitle(context)),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteConfirmation = false
            onDismiss()
            onDelete(event.id)
          },
          colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
          modifier = Modifier.testTag("btn_confirm_delete")
        ) {
          Text(stringResource(R.string.event_delete_button), fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showDeleteConfirmation = false },
          modifier = Modifier.testTag("btn_cancel_delete")
        ) {
          Text(stringResource(R.string.btn_cancel))
        }
      },
      modifier = Modifier.testTag("delete_confirmation_dialog")
    )
  }
}
