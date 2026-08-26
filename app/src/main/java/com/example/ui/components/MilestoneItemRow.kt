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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun MilestoneItemRow(
  title: String,
  dateFormatted: String,
  modifier: Modifier = Modifier,
  badgeText: String? = null,
  icon: ImageVector? = null,
  iconBackground: Color = LocalCherishExtendedColors.current.rosewoodContainer,
  iconTint: Color = MaterialTheme.colorScheme.primary,
  isFavorite: Boolean = false,
  onFavoriteClick: (() -> Unit)? = null,
  onClick: (() -> Unit)? = null,
  testTag: String = "milestone_item_row"
) {
  val extColors = LocalCherishExtendedColors.current

  CherishCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag),
    shape = RoundedCornerShape(20.dp),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(
      horizontal = 16.dp,
      vertical = 14.dp
    ),
    onClick = onClick
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (icon != null) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(iconBackground),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
      }

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = dateFormatted,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (badgeText != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(extColors.goldContainer)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = badgeText,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold
              ),
              color = extColors.goldAccent
            )
          }
        }
      }

      if (onFavoriteClick != null) {
        IconButton(
          onClick = onFavoriteClick,
          modifier = Modifier.size(40.dp)
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "Favorited" else "Add to favorites",
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else extColors.textMuted,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
