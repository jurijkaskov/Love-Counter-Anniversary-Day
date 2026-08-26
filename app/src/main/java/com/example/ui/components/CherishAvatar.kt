package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCherishExtendedColors

/**
 * A beautiful rounded avatar that displays initials or a heart icon.
 * Automatically scales font size to ensure long initials (like "A & B") fit perfectly.
 */
@Composable
fun CherishAvatar(
  initials: String,
  modifier: Modifier = Modifier,
  size: Dp = 46.dp,
  backgroundColor: Color = LocalCherishExtendedColors.current.rosewoodContainer,
  borderColor: Color = LocalCherishExtendedColors.current.goldAccent.copy(alpha = 0.4f),
  borderWidth: Dp = 1.5.dp,
  testTag: String = "cherish_avatar"
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(backgroundColor)
      .border(borderWidth, borderColor, CircleShape),
    contentAlignment = Alignment.Center
  ) {
    // Dynamic font scaling based on string length and avatar size
    // Base size is 15.sp for 46.dp avatar
    val scaleFactor = size.value / 46f
    val baseFontSize = when {
      initials.length > 4 -> 10.sp
      initials.length > 2 -> 11.sp
      else -> 15.sp
    }
    val finalFontSize = (baseFontSize.value * scaleFactor).sp

    Text(
      text = initials,
      style = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = finalFontSize,
        // Tighter spacing for multi-character initials to help them fit
        letterSpacing = if (initials.length > 2) (-0.5).sp else 0.sp
      ),
      color = MaterialTheme.colorScheme.primary,
      maxLines = 1,
      softWrap = false,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 4.dp).testTag("${testTag}_text")
    )
  }
}
