package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.models.MemoryPhotoModel
import com.example.ui.theme.LocalCherishExtendedColors
import java.io.File

@Composable
fun MemoryPhotoCard(
  photo: MemoryPhotoModel,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  onToggleFavorite: (() -> Unit)? = null,
  showCaption: Boolean = true,
  aspectRatioValue: Float = 1f
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  val photoFile = File(File(context.filesDir, "memory_photos"), photo.filePath)

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(aspectRatioValue)
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = 1.dp,
        color = if (photo.isFavorite) extColors.goldAccent.copy(alpha = 0.5f) else extColors.cardBorderSubtle,
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick)
      .testTag("memory_photo_card_${photo.id}"),
    shape = RoundedCornerShape(16.dp),
    color = extColors.rosewoodContainer.copy(alpha = 0.3f),
    shadowElevation = 2.dp
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
          .data(photoFile)
          .crossfade(true)
          .build(),
        contentDescription = photo.caption.ifBlank { "Memory Photo" },
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        loading = {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = MaterialTheme.colorScheme.primary,
              strokeWidth = 2.dp
            )
          }
        },
        error = {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(extColors.rosewoodContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.BrokenImage,
              contentDescription = "Image unavailable",
              tint = extColors.textMuted,
              modifier = Modifier.size(32.dp)
            )
          }
        }
      )

      // Bottom Gradient Overlay for subtle caption readability
      if (showCaption && photo.caption.isNotBlank()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.75f)
                )
              )
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
          Text(
            text = photo.caption,
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color.White,
              fontWeight = FontWeight.Medium,
              fontSize = 11.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Favorite indicator or toggle button
      if (onToggleFavorite != null) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(6.dp)
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable(onClick = onToggleFavorite)
            .testTag("btn_fav_photo_${photo.id}"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (photo.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (photo.isFavorite) "Favorited" else "Favorite",
            tint = if (photo.isFavorite) MaterialTheme.colorScheme.primary else Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      } else if (photo.isFavorite) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(6.dp)
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Favorited",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }
  }
}
