package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.data.models.ThemeMode
import com.example.ui.theme.CherishCardBorder
import com.example.ui.theme.CherishDarkBg
import com.example.ui.theme.CherishDarkCardBorder
import com.example.ui.theme.CherishDarkSurface
import com.example.ui.theme.CherishDarkTextPrimary
import com.example.ui.theme.CherishDarkTextSecondary
import com.example.ui.theme.CherishIvoryBg
import com.example.ui.theme.CherishSurface
import com.example.ui.theme.CherishTextPrimary
import com.example.ui.theme.CherishTextSecondary
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun ThemeSelectionRow(
  selectedThemeMode: ThemeMode,
  onSelectThemeMode: (ThemeMode) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth().height(IntrinsicSize.Max),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    ThemeMode.entries.forEach { mode ->
      ThemeModeCard(
        mode = mode,
        isSelected = mode == selectedThemeMode,
        onClick = { onSelectThemeMode(mode) },
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun ThemeModeCard(
  mode: ThemeMode,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  val borderColor by animateColorAsState(
    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorder,
    label = "border_color"
  )

  Surface(
    modifier = modifier
      .fillMaxHeight()
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .testTag("theme_card_${mode.name.lowercase()}"),
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = borderColor
    ),
    shadowElevation = if (isSelected) 2.dp else 0.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Mini Preview Representation
      MiniThemeVisual(mode = mode, isSelected = isSelected)

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = stringResource(mode.titleResId),
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          fontSize = 13.sp
        ),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun MiniThemeVisual(
  mode: ThemeMode,
  isSelected: Boolean
) {
  val extColors = LocalCherishExtendedColors.current

  Box(
    modifier = Modifier
      .size(width = 68.dp, height = 48.dp)
      .clip(RoundedCornerShape(10.dp))
      .border(
        width = 1.dp,
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else extColors.cardBorder,
        shape = RoundedCornerShape(10.dp)
      )
  ) {
    when (mode) {
      ThemeMode.LIGHT -> {
        // Pure Light Simulation
        Box(
          modifier = Modifier
            .matchParentSize()
            .background(CherishIvoryBg)
            .padding(6.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(CherishSurface)
              .border(0.5.dp, CherishCardBorder, RoundedCornerShape(6.dp))
              .padding(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(width = 24.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CherishTextPrimary.copy(alpha = 0.7f))
            )
            Spacer(modifier = Modifier.height(3.dp))
            Box(
              modifier = Modifier
                .size(width = 16.dp, height = 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CherishTextSecondary.copy(alpha = 0.5f))
            )
          }
        }
      }
      ThemeMode.DARK -> {
        // Pure Dark Simulation
        Box(
          modifier = Modifier
            .matchParentSize()
            .background(CherishDarkBg)
            .padding(6.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(CherishDarkSurface)
              .border(0.5.dp, CherishDarkCardBorder, RoundedCornerShape(6.dp))
              .padding(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(width = 24.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CherishDarkTextPrimary.copy(alpha = 0.8f))
            )
            Spacer(modifier = Modifier.height(3.dp))
            Box(
              modifier = Modifier
                .size(width = 16.dp, height = 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CherishDarkTextSecondary.copy(alpha = 0.6f))
            )
          }
        }
      }
      ThemeMode.SYSTEM -> {
        // Half light, half dark diagonal or vertical split
        Row(modifier = Modifier.matchParentSize()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .background(CherishIvoryBg)
              .padding(start = 4.dp, top = 6.dp, end = 2.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CherishSurface)
                .border(0.5.dp, CherishCardBorder, RoundedCornerShape(4.dp))
            )
          }
          Box(
            modifier = Modifier
              .weight(1f)
              .background(CherishDarkBg)
              .padding(start = 2.dp, top = 6.dp, end = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CherishDarkSurface)
                .border(0.5.dp, CherishDarkCardBorder, RoundedCornerShape(4.dp))
            )
          }
        }
      }
    }

    if (isSelected) {
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(4.dp)
          .size(16.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.size(10.dp)
        )
      }
    }
  }
}
