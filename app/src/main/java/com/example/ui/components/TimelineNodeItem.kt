package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.models.TimelineItem
import com.example.data.models.TimelineItemType
import com.example.ui.theme.LocalCherishExtendedColors
import java.io.File

@Composable
fun TimelineNodeItem(
  item: TimelineItem,
  isFirst: Boolean,
  isLast: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  val nodeIcon: ImageVector = when (item.type) {
    TimelineItemType.MEMORY -> when (item.iconKey) {
      "sunset" -> Icons.Outlined.WbSunny
      "flight" -> Icons.Outlined.Flight
      "ring" -> Icons.Outlined.Star
      else -> Icons.Outlined.MenuBook
    }
    TimelineItemType.PHOTO -> Icons.Outlined.PhotoCamera
    TimelineItemType.EVENT -> when (item.iconKey) {
      "celebration" -> Icons.Outlined.Celebration
      "ring" -> Icons.Outlined.Star
      "flight" -> Icons.Outlined.Flight
      else -> Icons.Outlined.Favorite
    }
    TimelineItemType.MILESTONE -> Icons.Outlined.Star
    TimelineItemType.TODAY_MARKER -> Icons.Outlined.Favorite
  }

  val isTodayMarker = item.type == TimelineItemType.TODAY_MARKER

  val nodeBgColor = when {
    isTodayMarker -> extColors.rosewoodContainer
    item.type == TimelineItemType.MEMORY -> extColors.goldContainer
    item.type == TimelineItemType.PHOTO -> extColors.rosewoodContainer
    item.type == TimelineItemType.MILESTONE -> extColors.rosewoodContainer
    else -> extColors.goldContainer
  }

  val nodeIconTint = when {
    isTodayMarker -> MaterialTheme.colorScheme.primary
    item.type == TimelineItemType.MEMORY -> extColors.goldAccent
    item.type == TimelineItemType.PHOTO -> MaterialTheme.colorScheme.primary
    item.type == TimelineItemType.MILESTONE -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.primary
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .testTag("timeline_item_${item.id}"),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.Top
  ) {
    // 1. Vertical timeline line and node indicator column
    Column(
      modifier = Modifier
        .width(44.dp)
        .padding(top = 2.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top line segment
      Box(
        modifier = Modifier
          .width(2.dp)
          .height(if (isFirst) 0.dp else 12.dp)
          .background(if (isFirst) Color.Transparent else extColors.cardBorderSubtle)
      )

      // Circular Node
      Box(
        modifier = Modifier
          .size(if (isTodayMarker) 36.dp else 34.dp)
          .clip(CircleShape)
          .background(nodeBgColor)
          .border(
            width = if (isTodayMarker) 2.dp else 1.5.dp,
            color = if (isTodayMarker) MaterialTheme.colorScheme.primary else extColors.cardBorder,
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = nodeIcon,
          contentDescription = null,
          tint = nodeIconTint,
          modifier = Modifier.size(if (isTodayMarker) 18.dp else 16.dp)
        )
      }

      // Bottom connecting line segment
      if (!isLast) {
        Box(
          modifier = Modifier
            .width(2.dp)
            .height(72.dp)
            .background(extColors.cardBorderSubtle)
        )
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    // 2. Timeline Item Content Card
    if (isTodayMarker) {
      // Elegant minimal card for the ongoing adventure marker
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
          .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = extColors.rosewoodContainer.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorderSubtle)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = item.title,
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = item.subtitle ?: "Today",
              style = MaterialTheme.typography.bodySmall,
              color = extColors.textMuted
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Today",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              ),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    } else {
      CherishCard(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 14.dp)
          .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Date & Category Tag Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = item.formattedDate,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
              ),
              color = extColors.textMuted
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
              if (item.categoryLabel != null) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                      if (item.type == TimelineItemType.MEMORY) extColors.goldContainer else extColors.rosewoodContainer
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = item.categoryLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.SemiBold
                    ),
                    color = if (item.type == TimelineItemType.MEMORY) extColors.goldAccent else MaterialTheme.colorScheme.primary
                  )
                }
              }

              if (item.isFavorite) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Filled.Favorite,
                  contentDescription = "Favorited",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Item Title
          Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          // Subtitle / Preview Snippet
          if (!item.subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = item.subtitle,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.5.sp,
                lineHeight = 19.sp
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }

          // Photo preview for photo items or connected photos
          if (item.type == TimelineItemType.PHOTO && item.photoModel != null) {
            Spacer(modifier = Modifier.height(10.dp))
            val photoFile = File(File(context.filesDir, "memory_photos"), item.photoModel.filePath)
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, extColors.cardBorderSubtle, RoundedCornerShape(14.dp))
            ) {
              SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                  .data(photoFile)
                  .crossfade(true)
                  .build(),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            }
          } else if (item.connectedPhotos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              item.connectedPhotos.take(3).forEach { photo ->
                val photoFile = File(File(context.filesDir, "memory_photos"), photo.filePath)
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, extColors.cardBorderSubtle, RoundedCornerShape(10.dp))
                ) {
                  SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                      .data(photoFile)
                      .crossfade(true)
                      .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                }
              }
              if (item.connectedPhotos.size > 3) {
                Box(
                  modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(extColors.rosewoodContainer.copy(alpha = 0.5f))
                    .border(1.dp, extColors.cardBorderSubtle, RoundedCornerShape(10.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "+${item.connectedPhotos.size - 3}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  )
                }
              }
            }
          }

          // Tags row if available
          if (item.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              item.tags.take(3).forEach { tag ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(extColors.cardBorderSubtle)
                    .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.sp,
                      color = extColors.textSecondary
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
