package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AccentColorStyle
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun AccentColorSelectionRow(
  selectedAccent: AccentColorStyle,
  onSelectAccent: (AccentColorStyle) -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current

  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(vertical = 4.dp)
      .testTag("accent_color_selection_row"),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    AccentColorStyle.entries.forEach { accent ->
      val isSelected = accent == selectedAccent
      val accentColor = Color(accent.lightPrimaryHex)
      val containerColor = Color(accent.lightContainerHex)

      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .clickable { onSelectAccent(accent) }
          .testTag("accent_option_${accent.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
          width = if (isSelected) 1.5.dp else 1.dp,
          color = if (isSelected) accentColor else extColors.cardBorder
        )
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Palette Circle Swatch
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(accentColor)
              .border(
                width = 2.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
            }
          }

          Column {
            Text(
              text = stringResource(accent.titleResId),
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = stringResource(accent.subtitleResId),
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
