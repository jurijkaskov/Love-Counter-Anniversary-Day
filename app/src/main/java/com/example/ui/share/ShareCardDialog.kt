package com.example.ui.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.models.ShareCardConfig
import com.example.data.models.ShareCardFormat
import com.example.data.models.ShareCardPayload
import com.example.data.models.ShareCardStyle
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.theme.LocalCherishExtendedColors
import kotlinx.coroutines.launch

@Composable
fun ShareCardDialog(
  payload: ShareCardPayload,
  onDismiss: () -> Unit,
  initialConfig: ShareCardConfig = ShareCardConfig()
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  val coroutineScope = rememberCoroutineScope()

  var config by remember { mutableStateOf(initialConfig) }
  var isExporting by remember { mutableStateOf(false) }
  var showCustomNoteEditor by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = {
      if (!isExporting) onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 16.dp)
        .testTag("share_card_dialog"),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      shadowElevation = 16.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = stringResource(R.string.share_card_dialog_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = stringResource(R.string.share_card_dialog_subtitle),
              style = MaterialTheme.typography.bodySmall,
              color = extColors.textMuted
            )
          }

          IconButton(
            onClick = onDismiss,
            enabled = !isExporting,
            modifier = Modifier.testTag("btn_close_share_card")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = stringResource(R.string.btn_close),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Interactive Preview
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          ShareCardPreview(
            payload = payload,
            config = config,
            modifier = Modifier
              .widthIn(max = if (config.format == ShareCardFormat.SQUARE_1_1) 320.dp else 260.dp)
              .testTag("live_card_preview")
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Controls Section 1: Card Format (Story vs Post)
        Text(
          text = stringResource(R.string.share_card_format_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = extColors.textMuted,
          modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FormatChip(
            title = stringResource(R.string.share_card_format_story),
            selected = config.format == ShareCardFormat.PORTRAIT_9_16,
            onClick = { config = config.copy(format = ShareCardFormat.PORTRAIT_9_16) },
            modifier = Modifier.weight(1f),
            testTag = "chip_format_story"
          )
          FormatChip(
            title = stringResource(R.string.share_card_format_post),
            selected = config.format == ShareCardFormat.SQUARE_1_1,
            onClick = { config = config.copy(format = ShareCardFormat.SQUARE_1_1) },
            modifier = Modifier.weight(1f),
            testTag = "chip_format_post"
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Controls Section 2: Visual Themes / Styles
        Text(
          text = stringResource(R.string.share_card_style_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = extColors.textMuted,
          modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StyleSelectorItem(
            name = stringResource(R.string.share_card_style_classic),
            style = ShareCardStyle.CLASSIC,
            colorSwatch = Color(0xFFFBF8F4),
            borderColor = Color(0xFFC99252),
            isSelected = config.style == ShareCardStyle.CLASSIC,
            onSelect = { config = config.copy(style = ShareCardStyle.CLASSIC) },
            modifier = Modifier.weight(1f),
            testTag = "style_chip_classic"
          )
          StyleSelectorItem(
            name = stringResource(R.string.share_card_style_midnight),
            style = ShareCardStyle.MIDNIGHT,
            colorSwatch = Color(0xFF1E1815),
            borderColor = Color(0xFFE4AD70),
            isSelected = config.style == ShareCardStyle.MIDNIGHT,
            onSelect = { config = config.copy(style = ShareCardStyle.MIDNIGHT) },
            modifier = Modifier.weight(1f),
            testTag = "style_chip_midnight"
          )
          StyleSelectorItem(
            name = stringResource(R.string.share_card_style_rose),
            style = ShareCardStyle.ROSE,
            colorSwatch = Color(0xFFFFF0EC),
            borderColor = Color(0xFFE89A89),
            isSelected = config.style == ShareCardStyle.ROSE,
            onSelect = { config = config.copy(style = ShareCardStyle.ROSE) },
            modifier = Modifier.weight(1f),
            testTag = "style_chip_rose"
          )
          StyleSelectorItem(
            name = stringResource(R.string.share_card_style_minimal),
            style = ShareCardStyle.MINIMAL,
            colorSwatch = Color(0xFFFFFFFF),
            borderColor = Color(0xFFD0D0D5),
            isSelected = config.style == ShareCardStyle.MINIMAL,
            onSelect = { config = config.copy(style = ShareCardStyle.MINIMAL) },
            modifier = Modifier.weight(1f),
            testTag = "style_chip_minimal"
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Controls Section 3: Customization Toggles
        Text(
          text = stringResource(R.string.share_card_options_label),
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = extColors.textMuted,
          modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Toggle Option: Quote
        OptionToggleRow(
          title = stringResource(R.string.share_card_toggle_quote),
          checked = config.showQuote,
          onCheckedChange = { config = config.copy(showQuote = it) },
          testTag = "toggle_quote"
        )

        // Toggle Option: Breakdown
        if (!payload.supportingText.isNullOrBlank()) {
          OptionToggleRow(
            title = stringResource(R.string.share_card_toggle_breakdown),
            checked = config.showBreakdown,
            onCheckedChange = { config = config.copy(showBreakdown = it) },
            testTag = "toggle_breakdown"
          )
        }

        // Toggle Option: Date
        if (!payload.dateString.isNullOrBlank()) {
          OptionToggleRow(
            title = stringResource(R.string.share_card_toggle_date),
            checked = config.showDate,
            onCheckedChange = { config = config.copy(showDate = it) },
            testTag = "toggle_date"
          )
        }

        // Toggle Option: Photo Background (if photo exists)
        if (!payload.photoPath.isNullOrBlank()) {
          OptionToggleRow(
            title = stringResource(R.string.share_card_toggle_photo),
            checked = config.usePhotoBackground,
            onCheckedChange = { config = config.copy(usePhotoBackground = it) },
            testTag = "toggle_photo"
          )
        }

        // Toggle Option: Watermark
        OptionToggleRow(
          title = stringResource(R.string.share_card_toggle_watermark),
          checked = config.showWatermark,
          onCheckedChange = { config = config.copy(showWatermark = it) },
          testTag = "toggle_watermark"
        )

        // Optional Custom Dedication / Note Input
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = config.customMessage,
          onValueChange = { config = config.copy(customMessage = it) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_share_custom_message"),
          placeholder = {
            Text(
              stringResource(R.string.share_card_custom_message_hint),
              style = MaterialTheme.typography.bodySmall
            )
          },
          label = { Text(stringResource(R.string.share_card_custom_note_label), style = MaterialTheme.typography.labelSmall) },
          maxLines = 2,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = extColors.cardBorder
          )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row: Save Image & Share Image
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          SecondaryButton(
            text = stringResource(R.string.share_card_action_save),
            onClick = {
              if (!isExporting) {
                isExporting = true
                coroutineScope.launch {
                  ShareCardImageExporter.saveCardToDevice(context, payload, config)
                  isExporting = false
                }
              }
            },
            icon = Icons.Default.Download,
            modifier = Modifier
              .weight(1f)
              .testTag("btn_save_card_image"),
            enabled = !isExporting
          )

          PrimaryButton(
            text = stringResource(R.string.share_card_action_share),
            onClick = {
              if (!isExporting) {
                isExporting = true
                coroutineScope.launch {
                  ShareCardImageExporter.shareCard(context, payload, config)
                  isExporting = false
                }
              }
            },
            icon = Icons.Default.Share,
            modifier = Modifier
              .weight(1f)
              .testTag("btn_share_card_image"),
            enabled = !isExporting
          )
        }
      }
    }
  }
}

@Composable
private fun FormatChip(
  title: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = ""
) {
  val extColors = LocalCherishExtendedColors.current
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    color = if (selected) MaterialTheme.colorScheme.primary else extColors.quoteBackground,
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (selected) MaterialTheme.colorScheme.primary else extColors.cardBorder
    )
  ) {
    Box(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        ),
        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun StyleSelectorItem(
  name: String,
  style: ShareCardStyle,
  colorSwatch: Color,
  borderColor: Color,
  isSelected: Boolean,
  onSelect: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = ""
) {
  val extColors = LocalCherishExtendedColors.current
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onSelect)
      .padding(4.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(colorSwatch)
        .border(
          width = if (isSelected) 2.5.dp else 1.dp,
          color = if (isSelected) MaterialTheme.colorScheme.primary else borderColor,
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      if (isSelected) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = if (style == ShareCardStyle.MIDNIGHT) Color.White else MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = name,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
      ),
      color = if (isSelected) MaterialTheme.colorScheme.primary else extColors.textMuted
    )
  }
}

@Composable
private fun OptionToggleRow(
  title: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String = ""
) {
  val extColors = LocalCherishExtendedColors.current
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      modifier = Modifier.testTag(testTag),
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = MaterialTheme.colorScheme.primary,
        uncheckedThumbColor = extColors.textMuted,
        uncheckedTrackColor = extColors.cardBorder
      )
    )
  }
}
