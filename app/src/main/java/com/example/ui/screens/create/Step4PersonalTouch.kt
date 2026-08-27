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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalCherishExtendedColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step4PersonalTouch(
  personalNote: String,
  onPersonalNoteChange: (String) -> Unit,
  selectedSymbolKey: String,
  onSelectSymbolKey: (String) -> Unit,
  selectedThemeAccent: String,
  onSelectThemeAccent: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("create_step_4_container"),
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
        text = stringResource(R.string.create_step4_badge),
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
      text = stringResource(R.string.create_step4_headline),
      style = MaterialTheme.typography.headlineMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Subtitle
    Text(
      text = stringResource(R.string.create_step4_subhead),
      style = MaterialTheme.typography.bodyMedium,
      color = extColors.textMuted
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Quote / Note Text Field
    OutlinedTextField(
      value = personalNote,
      onValueChange = onPersonalNoteChange,
      label = { Text(stringResource(R.string.input_personal_note)) },
      placeholder = { Text(stringResource(R.string.input_personal_note_hint), color = extColors.textMuted) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Outlined.FormatQuote,
          contentDescription = null,
          tint = extColors.goldAccent
        )
      },
      minLines = 2,
      maxLines = 3,
      shape = RoundedCornerShape(18.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = extColors.cardBorder,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface
      ),
      keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("input_personal_note_field")
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Symbol Selector Section
    Text(
      text = stringResource(R.string.choose_symbol_label),
      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      AvailableSymbols.forEach { symbol ->
        val isSelected = selectedSymbolKey == symbol.key
        val borderColor by animateColorAsState(
          targetValue = if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorder,
          label = "symbol_border"
        )
        val bgColor by animateColorAsState(
          targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
          label = "symbol_bg"
        )

        Surface(
          modifier = Modifier
            .size(52.dp)
            .shadow(if (isSelected) 4.dp else 1.dp, CircleShape)
            .clip(CircleShape)
            .clickable { onSelectSymbolKey(symbol.key) }
            .testTag("symbol_option_${symbol.key}"),
          shape = CircleShape,
          color = bgColor,
          border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
        ) {
          Box(
            modifier = Modifier.padding(12.dp),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = symbol.icon,
              contentDescription = stringResource(symbol.labelResId),
              tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Palette Theme Section
    Text(
      text = stringResource(R.string.choose_theme_label),
      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      AvailablePalettes.forEach { palette ->
        val isSelected = selectedThemeAccent == palette.id
        val borderColor by animateColorAsState(
          targetValue = if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorder,
          animationSpec = tween(200),
          label = "palette_border"
        )

        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelectThemeAccent(palette.id) }
            .testTag("palette_option_${palette.id}"),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Color dot preview
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(palette.primaryColor)
                .border(1.5.dp, palette.accentColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
              text = stringResource(palette.nameResId),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              ),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.weight(1f)
            )

            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}
