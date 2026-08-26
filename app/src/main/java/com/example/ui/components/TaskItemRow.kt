package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.MilestoneTaskModel
import com.example.ui.theme.CherishGold
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun TaskItemRow(
  task: MilestoneTaskModel,
  onToggle: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  showChevron: Boolean = true
) {
  val extColors = LocalCherishExtendedColors.current
  val isCompleted = task.isCompleted

  val checkScale by animateFloatAsState(
    targetValue = if (isCompleted) 1f else 0f,
    animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
    label = "check_scale"
  )

  val circleBgColor by animateColorAsState(
    targetValue = if (isCompleted) {
      MaterialTheme.colorScheme.primary
    } else {
      Color.Transparent
    },
    label = "circle_bg"
  )

  val circleBorderColor by animateColorAsState(
    targetValue = if (isCompleted) {
      MaterialTheme.colorScheme.primary
    } else {
      extColors.cardBorder
    },
    label = "circle_border"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag("task_item_${task.id}"),
    color = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder.copy(alpha = 0.6f)),
    shadowElevation = 0.5.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Animated Checkbox Target
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onToggle
          )
          .testTag("task_checkbox_${task.id}"),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(circleBgColor)
            .border(
              width = 1.5.dp,
              color = circleBorderColor,
              shape = CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          if (isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Completed",
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier
                .size(15.dp)
                .scale(checkScale)
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Title & Subtitle / Due date
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = task.title,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
            fontSize = 15.sp
          ),
          color = if (isCompleted) {
            extColors.textMuted
          } else {
            MaterialTheme.colorScheme.onSurface
          },
          textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        if (task.formattedDueDate != null || task.note.isNotBlank()) {
          Row(
            modifier = Modifier.padding(top = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (task.formattedDueDate != null) {
              Text(
                text = "Due ${task.formattedDueDate}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = if (isCompleted) extColors.textMuted else CherishGold
              )
            }
            if (task.note.isNotBlank()) {
              Text(
                text = task.note,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = extColors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }

      if (showChevron) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
          contentDescription = "View Task Details",
          tint = extColors.textMuted.copy(alpha = 0.7f),
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
