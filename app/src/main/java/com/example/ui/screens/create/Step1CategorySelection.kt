package com.example.ui.screens.create

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.EventCategory
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun Step1CategorySelection(
  selectedCategory: EventCategory,
  onSelectCategory: (EventCategory) -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("create_step_1_container"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Badge
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(extColors.rosewoodContainer)
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Text(
        text = stringResource(R.string.create_step1_badge),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp
        ),
        color = MaterialTheme.colorScheme.primary
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Headline
    Text(
      text = stringResource(R.string.create_step1_headline),
      style = MaterialTheme.typography.headlineMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Subtitle
    Text(
      text = stringResource(R.string.create_step1_subhead),
      style = MaterialTheme.typography.bodyMedium,
      color = extColors.textMuted
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Category options list
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      AvailableCategoryOptions.forEach { option ->
        val isSelected = selectedCategory == option.category

        val borderColor by animateColorAsState(
          targetValue = if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorder,
          animationSpec = tween(200),
          label = "border_color"
        )
        val containerColor by animateColorAsState(
          targetValue = if (isSelected) extColors.rosewoodContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
          animationSpec = tween(200),
          label = "container_color"
        )

        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSelectCategory(option.category) }
            .testTag("category_option_${option.category.id}"),
          shape = RoundedCornerShape(20.dp),
          color = containerColor,
          border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Icon container
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else extColors.rosewoodContainer)
                .border(
                  width = 1.dp,
                  color = if (isSelected) extColors.goldAccent else Color.Transparent,
                  shape = CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(option.titleResId),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = stringResource(option.descResId),
                style = MaterialTheme.typography.bodySmall,
                color = extColors.textMuted
              )
            }

            // Radio / Check mark indicator
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(
                  width = 1.5.dp,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorder,
                  shape = CircleShape
                )
                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
              contentAlignment = Alignment.Center
            ) {
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
