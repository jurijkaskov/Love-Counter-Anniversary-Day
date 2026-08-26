package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.StoryModel
import com.example.ui.screens.create.getIconForStory
import com.example.ui.theme.CherishGold
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun NextEventHighlightCard(
  event: StoryModel,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {},
  onFavoriteClick: (() -> Unit)? = null,
  testTag: String = "next_event_highlight_card"
) {
  val extColors = LocalCherishExtendedColors.current
  val icon = getIconForStory(event)

  CherishCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag),
    shape = RoundedCornerShape(24.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    borderColor = extColors.goldAccent.copy(alpha = 0.45f),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
    onClick = onClick
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Top row: Label badge + Countdown badge + Favorite button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(extColors.goldContainer)
              .padding(horizontal = 10.dp, vertical = 4.dp)
              .testTag("next_event_category_badge")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = extColors.goldAccent,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = stringResource(R.string.moments_next_highlight_label).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.8.sp
                ),
                color = extColors.goldAccent
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(extColors.rosewoodContainer)
              .padding(horizontal = 10.dp, vertical = 4.dp)
              .testTag("next_event_countdown_pill")
          ) {
            Text(
              text = event.countdownBadgeText,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
              ),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        if (onFavoriteClick != null) {
          IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier
              .size(36.dp)
              .testTag("next_event_favorite_btn")
          ) {
            Icon(
              imageVector = if (event.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
              contentDescription = if (event.isFavorite) "Favorited" else "Favorite",
              tint = if (event.isFavorite) MaterialTheme.colorScheme.primary else extColors.textMuted,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Main content: Icon & Title + Date
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(
                  extColors.goldContainer,
                  extColors.rosewoodContainer
                )
              )
            )
            .testTag("next_event_icon_container"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = extColors.goldAccent,
            modifier = Modifier.size(26.dp)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = event.displayTitle,
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.testTag("next_event_title")
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = event.formattedNextOccurrenceDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("next_event_date")
          )
        }

        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = "View Details",
          tint = extColors.textMuted,
          modifier = Modifier.size(18.dp)
        )
      }

      if (event.note.isNotBlank()) {
        Spacer(modifier = Modifier.height(12.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(extColors.cardBorderSubtle.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Text(
            text = "“${event.note}”",
            style = MaterialTheme.typography.bodySmall.copy(
              fontFamily = FontFamily.Serif
            ),
            color = extColors.textMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}
