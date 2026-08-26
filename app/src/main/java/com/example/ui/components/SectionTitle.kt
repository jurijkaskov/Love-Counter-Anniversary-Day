package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun SectionTitle(
  title: String,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null,
  titleColor: Color = MaterialTheme.colorScheme.onBackground,
  testTag: String = "section_title"
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = titleColor,
        fontWeight = FontWeight.Medium
      )
      if (subtitle != null) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = LocalCherishExtendedColors.current.textMuted
        )
      }
    }
    if (actionText != null && onActionClick != null) {
      Text(
        text = actionText,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.SemiBold
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .clickable(onClick = onActionClick)
          .padding(vertical = 4.dp, horizontal = 8.dp)
          .testTag("${testTag}_action")
      )
    }
  }
}
