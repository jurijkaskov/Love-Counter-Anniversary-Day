package com.example.ui.components

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.JournalEntryModel
import com.example.data.models.StoryModel
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun JournalEntryCard(
  entry: JournalEntryModel,
  associatedStory: StoryModel?,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current

  CherishCard(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("journal_card_${entry.id}"),
    shape = RoundedCornerShape(22.dp),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Header: Date & Favorite Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.CalendarToday,
            contentDescription = null,
            tint = extColors.textMuted,
            modifier = Modifier.size(13.dp)
          )
          Text(
            text = entry.formattedDate,
            style = MaterialTheme.typography.labelMedium.copy(
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            ),
            color = extColors.textMuted
          )
        }

        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .size(28.dp)
            .testTag("journal_fav_btn_${entry.id}")
        ) {
          Icon(
            imageVector = if (entry.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (entry.isFavorite) "Favorited" else "Favorite",
            tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary else extColors.textMuted,
            modifier = Modifier.size(17.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Title
      Text(
        text = entry.getDisplayTitle(context),
        style = MaterialTheme.typography.headlineSmall.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          lineHeight = 24.sp
        ),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      // Preview Content Snippet
      if (entry.content.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = entry.previewSnippet,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            lineHeight = 20.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Associated story pill badge & tags row
      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (associatedStory != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(extColors.rosewoodContainer)
              .padding(horizontal = 9.dp, vertical = 4.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = associatedStory.getDisplayTitle(context),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        } else {
          Spacer(modifier = Modifier.width(1.dp))
        }

        // Tags
        if (entry.tags.isNotEmpty()) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            entry.tags.take(2).forEach { tag ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(extColors.cardBorderSubtle)
                  .padding(horizontal = 7.dp, vertical = 3.dp)
              ) {
                Text(
                  text = tag,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.5.sp,
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
