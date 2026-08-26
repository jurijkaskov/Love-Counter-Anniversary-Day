package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun PremiumEmptyState(
  title: String,
  description: String,
  modifier: Modifier = Modifier,
  icon: ImageVector = Icons.Outlined.FavoriteBorder,
  actionButtonText: String? = null,
  onActionClick: (() -> Unit)? = null,
  testTag: String = "empty_state"
) {
  val extColors = LocalCherishExtendedColors.current

  CherishCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag),
    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(32.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(extColors.rosewoodContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(34.dp)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      if (actionButtonText != null && onActionClick != null) {
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(
          text = actionButtonText,
          onClick = onActionClick,
          testTag = "${testTag}_button"
        )
      }
    }
  }
}
