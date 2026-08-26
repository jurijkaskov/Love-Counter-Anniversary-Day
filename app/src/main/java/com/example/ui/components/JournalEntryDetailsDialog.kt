package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WbSunny
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.StoryModel
import com.example.ui.share.ShareCardDialog
import com.example.ui.share.ShareCardPayloadFactory
import com.example.ui.theme.LocalCherishExtendedColors
import java.io.File

@Composable
fun JournalEntryDetailsDialog(
  entry: JournalEntryModel,
  associatedStory: StoryModel?,
  connectedPhotos: List<MemoryPhotoModel> = emptyList(),
  onPhotoClick: (MemoryPhotoModel) -> Unit = {},
  onAddPhotoClick: () -> Unit = {},
  onDismiss: () -> Unit,
  onEdit: (JournalEntryModel) -> Unit,
  onDelete: (String) -> Unit,
  onToggleFavorite: (String) -> Unit
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  var showDeleteConfirmation by remember { mutableStateOf(false) }
  var showShareCardDialog by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp)
        .testTag("journal_details_dialog"),
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
        // Top Navigation Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { onToggleFavorite(entry.id) },
              modifier = Modifier.testTag("journal_details_fav_btn")
            ) {
              Icon(
                imageVector = if (entry.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (entry.isFavorite) "Favorited" else "Favorite",
                tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary else extColors.textMuted
              )
            }

            IconButton(
              onClick = { showShareCardDialog = true },
              modifier = Modifier.testTag("journal_details_share_btn")
            ) {
              Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Share Memory Card",
                tint = extColors.textMuted
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(extColors.goldContainer)
              .padding(horizontal = 12.dp, vertical = 5.dp)
          ) {
            Text(
              text = stringResource(R.string.timeline_type_memory),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = extColors.goldAccent
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("journal_details_close_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(R.string.event_close),
              tint = extColors.textMuted
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Romantic Decorative Banner Icon
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
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (entry.tags.contains("Sunset")) Icons.Outlined.WbSunny else Icons.Outlined.MenuBook,
            contentDescription = null,
            tint = extColors.goldAccent,
            modifier = Modifier.size(34.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Memory Title
        Text(
          text = entry.displayTitle,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center,
          modifier = Modifier.testTag("journal_details_title")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Date Display
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.CalendarToday,
            contentDescription = null,
            tint = extColors.textMuted,
            modifier = Modifier.size(15.dp)
          )
          Text(
            text = entry.formattedDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Associated Story Badge
        if (associatedStory != null) {
          Spacer(modifier = Modifier.height(12.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(extColors.rosewoodContainer)
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = stringResource(R.string.journal_card_related_to, associatedStory.displayTitle),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rich Memory Written Content Card
        CherishCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          containerColor = extColors.rosewoodContainer.copy(alpha = 0.35f),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)
        ) {
          Text(
            text = if (entry.content.isNotBlank()) entry.content else "No written memory added.",
            style = MaterialTheme.typography.bodyLarge.copy(
              lineHeight = 24.sp,
              fontSize = 15.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("journal_details_content")
          )
        }

        // Visual Photos Section
        Spacer(modifier = Modifier.height(18.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(R.string.photos_connected_title),
            style = MaterialTheme.typography.labelLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )

          TextButton(
            onClick = onAddPhotoClick,
            modifier = Modifier.testTag("btn_journal_add_photo")
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
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
              )
            )
          }
        }

        if (connectedPhotos.isEmpty()) {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .clickable(onClick = onAddPhotoClick),
            shape = RoundedCornerShape(14.dp),
            color = extColors.rosewoodContainer.copy(alpha = 0.25f)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.PhotoLibrary,
                contentDescription = null,
                tint = extColors.textMuted,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = stringResource(R.string.photos_connected_empty),
                style = MaterialTheme.typography.bodySmall.copy(
                  fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = extColors.textMuted
              )
            }
          }
        } else {
          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(connectedPhotos, key = { it.id }) { photo ->
              val photoFile = File(File(context.filesDir, "memory_photos"), photo.filePath)
              Box(
                modifier = Modifier
                  .size(80.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .border(1.dp, extColors.cardBorderSubtle, RoundedCornerShape(14.dp))
                  .clickable { onPhotoClick(photo) }
                  .testTag("journal_photo_thumb_${photo.id}")
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

        // Tags List
        if (entry.tags.isNotEmpty()) {
          Spacer(modifier = Modifier.height(16.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            entry.tags.forEach { tag ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(extColors.cardBorderSubtle)
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "#$tag",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = extColors.textSecondary
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Action Buttons Row (Share, Delete, Edit)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = { showShareCardDialog = true },
            modifier = Modifier
              .weight(1f)
              .testTag("journal_details_share_card_btn"),
            shape = RoundedCornerShape(16.dp)
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

          OutlinedButton(
            onClick = { showDeleteConfirmation = true },
            modifier = Modifier
              .weight(0.9f)
              .testTag("journal_details_delete_btn"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = MaterialTheme.colorScheme.error
            ),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            )
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = null,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = stringResource(R.string.journal_delete_btn),
              style = MaterialTheme.typography.labelMedium
            )
          }

          PrimaryButton(
            text = stringResource(R.string.event_edit_button),
            onClick = {
              onDismiss()
              onEdit(entry)
            },
            modifier = Modifier.weight(1f),
            testTag = "journal_details_edit_btn",
            icon = Icons.Default.Edit
          )
        }
      }
    }
  }

  if (showShareCardDialog) {
    ShareCardDialog(
      payload = ShareCardPayloadFactory.fromJournalEntry(
        entry = entry,
        associatedStory = associatedStory,
        connectedPhoto = connectedPhotos.firstOrNull()
      ),
      onDismiss = { showShareCardDialog = false }
    )
  }

  // Delete Confirmation Alert Dialog
  if (showDeleteConfirmation) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmation = false },
      title = {
        Text(
          text = stringResource(R.string.journal_delete_dialog_title),
          style = MaterialTheme.typography.headlineSmall.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          )
        )
      },
      text = {
        Text(
          text = stringResource(R.string.journal_delete_dialog_msg, entry.displayTitle),
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteConfirmation = false
            onDismiss()
            onDelete(entry.id)
          },
          colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error
          )
        ) {
          Text(stringResource(R.string.journal_delete_btn))
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirmation = false }) {
          Text(stringResource(R.string.btn_cancel))
        }
      }
    )
  }
}
