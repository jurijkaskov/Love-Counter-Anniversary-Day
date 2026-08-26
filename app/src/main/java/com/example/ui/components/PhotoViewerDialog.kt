package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun PhotoViewerDialog(
  photos: List<MemoryPhotoModel>,
  initialPhotoId: String,
  stories: List<StoryModel> = emptyList(),
  journalEntries: List<JournalEntryModel> = emptyList(),
  onDismiss: () -> Unit,
  onEditPhoto: (MemoryPhotoModel) -> Unit,
  onDeletePhoto: (String) -> Unit,
  onToggleFavorite: (String) -> Unit,
  onStoryClick: ((StoryModel) -> Unit)? = null,
  onJournalClick: ((JournalEntryModel) -> Unit)? = null
) {
  if (photos.isEmpty()) {
    onDismiss()
    return
  }

  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  val initialIndex = photos.indexOfFirst { it.id == initialPhotoId }.coerceAtLeast(0)
  var currentIndex by remember { mutableIntStateOf(initialIndex) }
  val currentPhoto = photos.getOrNull(currentIndex) ?: photos.first()

  val associatedStory = remember(currentPhoto.associatedStoryId, stories) {
    stories.find { it.id == currentPhoto.associatedStoryId }
  }
  val associatedJournal = remember(currentPhoto.associatedJournalId, journalEntries) {
    journalEntries.find { it.id == currentPhoto.associatedJournalId }
  }

  var showDeleteConfirmation by remember { mutableStateOf(false) }
  var showShareCardDialog by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .testTag("photo_viewer_dialog")
    ) {
      // Main Image Display
      val photoFile = File(File(context.filesDir, "memory_photos"), currentPhoto.filePath)

      SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
          .data(photoFile)
          .crossfade(true)
          .build(),
        contentDescription = currentPhoto.caption.ifBlank { "Memory Photo" },
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .fillMaxSize()
          .testTag("photo_viewer_image"),
        loading = {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(36.dp),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      )

      // Navigation Arrows (if multiple photos)
      if (photos.size > 1) {
        if (currentIndex > 0) {
          Box(
            modifier = Modifier
              .align(Alignment.CenterStart)
              .padding(start = 12.dp)
              .size(44.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.45f))
              .clickable { currentIndex -= 1 }
              .testTag("btn_photo_prev"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              contentDescription = "Previous Photo",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        if (currentIndex < photos.lastIndex) {
          Box(
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .padding(end = 12.dp)
              .size(44.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.45f))
              .clickable { currentIndex += 1 }
              .testTag("btn_photo_next"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
              contentDescription = "Next Photo",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }

      // Top Action Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.TopCenter)
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
            )
          )
          .statusBarsPadding()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Close button & Counter
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.testTag("btn_close_viewer")
            ) {
              Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Close",
                tint = Color.White
              )
            }

            if (photos.size > 1) {
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = stringResource(R.string.photos_viewer_counter, currentIndex + 1, photos.size),
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = Color.White.copy(alpha = 0.85f),
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }

          // Action buttons: Favorite, Share, Edit, Delete
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = { onToggleFavorite(currentPhoto.id) },
              modifier = Modifier.testTag("btn_viewer_favorite")
            ) {
              Icon(
                imageVector = if (currentPhoto.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (currentPhoto.isFavorite) MaterialTheme.colorScheme.primary else Color.White
              )
            }

            IconButton(
              onClick = { showShareCardDialog = true },
              modifier = Modifier.testTag("btn_viewer_share_card")
            ) {
              Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Share as Card",
                tint = Color.White
              )
            }

            IconButton(
              onClick = {
                onDismiss()
                onEditPhoto(currentPhoto)
              },
              modifier = Modifier.testTag("btn_viewer_edit")
            ) {
              Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                tint = Color.White
              )
            }

            IconButton(
              onClick = { showDeleteConfirmation = true },
              modifier = Modifier.testTag("btn_viewer_delete")
            ) {
              Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFFF6B6B)
              )
            }
          }
        }
      }

      // Bottom Metadata & Connections Overlay
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
            )
          )
          .navigationBarsPadding()
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Date
          Text(
            text = currentPhoto.formattedDate,
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.SemiBold,
              color = extColors.goldAccent
            )
          )

          // Caption
          if (currentPhoto.caption.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = currentPhoto.caption,
              style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Normal
              )
            )
          }

          // Connections Row (Story & Journal pills)
          if (associatedStory != null || associatedJournal != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              associatedStory?.let { story ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = Color.White.copy(alpha = 0.15f),
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(enabled = onStoryClick != null) {
                      onDismiss()
                      onStoryClick?.invoke(story)
                    }
                    .testTag("viewer_linked_story_pill")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Collections,
                      contentDescription = null,
                      tint = extColors.goldAccent,
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = story.displayTitle,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                      )
                    )
                  }
                }
              }

              associatedJournal?.let { entry ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = Color.White.copy(alpha = 0.15f),
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(enabled = onJournalClick != null) {
                      onDismiss()
                      onJournalClick?.invoke(entry)
                    }
                    .testTag("viewer_linked_journal_pill")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.MenuBook,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = entry.displayTitle,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                      )
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Delete Confirmation Dialog
  if (showDeleteConfirmation) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmation = false },
      title = {
        Text(
          text = stringResource(R.string.photos_delete_dialog_title),
          style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif)
        )
      },
      text = {
        Text(
          text = stringResource(R.string.photos_delete_dialog_msg),
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteConfirmation = false
            onDeletePhoto(currentPhoto.id)
            if (photos.size <= 1) {
              onDismiss()
            } else if (currentIndex >= photos.lastIndex) {
              currentIndex = photos.size - 2
            }
          },
          modifier = Modifier.testTag("btn_confirm_delete_photo")
        ) {
          Text(
            text = stringResource(R.string.photos_delete_btn),
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
          )
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showDeleteConfirmation = false },
          modifier = Modifier.testTag("btn_cancel_delete_photo")
        ) {
          Text(text = stringResource(R.string.btn_cancel))
        }
      }
    )
  }

  if (showShareCardDialog) {
    ShareCardDialog(
      payload = ShareCardPayloadFactory.fromPhoto(
        photo = currentPhoto,
        associatedStory = associatedStory,
        associatedJournal = associatedJournal
      ),
      onDismiss = { showShareCardDialog = false }
    )
  }
}
