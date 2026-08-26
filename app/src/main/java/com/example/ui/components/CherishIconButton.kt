package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun CherishIconButton(
  icon: ImageVector,
  contentDescription: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  tint: Color = MaterialTheme.colorScheme.primary,
  backgroundColor: Color = LocalCherishExtendedColors.current.rosewoodContainer,
  size: Dp = 44.dp,
  iconSize: Dp = 22.dp,
  testTag: String = "cherish_icon_button"
) {
  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = modifier
      .minimumInteractiveComponentSize()
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .background(backgroundColor)
        .clickable(
          interactionSource = interactionSource,
          indication = androidx.compose.material3.ripple(),
          onClick = onClick
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier.size(iconSize)
      )
    }
  }
}
