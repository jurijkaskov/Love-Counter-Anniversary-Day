package com.example.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun CherishBottomBar(
  currentRoute: String,
  onNavigateTo: (CherishScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("bottom_nav_bar"),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp,
    shadowElevation = 8.dp,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .border(
          width = 1.dp,
          color = extColors.cardBorderSubtle,
          shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        )
        .navigationBarsPadding()
        .padding(top = 8.dp, bottom = 8.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 500.dp)
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        CherishScreen.items.forEach { screen ->
          val isSelected = currentRoute == screen.route
          BottomNavItem(
            screen = screen,
            isSelected = isSelected,
            onClick = {
              if (!isSelected) {
                onNavigateTo(screen)
              }
            },
            testTag = "nav_item_${screen.route}"
          )
        }
      }
    }
  }
}

@Composable
private fun BottomNavItem(
  screen: CherishScreen,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "bottom_nav_item"
) {
  val extColors = LocalCherishExtendedColors.current
  val interactionSource = remember { MutableInteractionSource() }

  val scale by animateFloatAsState(
    targetValue = if (isSelected) 1.04f else 1.0f,
    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
    label = "nav_scale"
  )

  val iconColor by animateColorAsState(
    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else extColors.textMuted,
    animationSpec = tween(durationMillis = 180),
    label = "nav_icon_color"
  )

  val labelColor by animateColorAsState(
    targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface else extColors.textMuted,
    animationSpec = tween(durationMillis = 180),
    label = "nav_label_color"
  )

  val pillBackground by animateColorAsState(
    targetValue = if (isSelected) extColors.rosewoodContainer.copy(alpha = 0.85f) else Color.Transparent,
    animationSpec = tween(durationMillis = 200),
    label = "nav_pill_bg"
  )

  val title = stringResource(screen.titleResId)

  Column(
    modifier = modifier
      .scale(scale)
      .widthIn(min = 72.dp)
      .heightIn(min = 52.dp)
      .clip(RoundedCornerShape(20.dp))
      .semantics {
        role = Role.Tab
        selected = isSelected
        contentDescription = "$title tab, ${if (isSelected) "selected" else "not selected"}"
      }
      .clickable(
        interactionSource = interactionSource,
        indication = ripple(bounded = false, radius = 32.dp),
        onClick = onClick
      )
      .padding(horizontal = 8.dp, vertical = 4.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(pillBackground)
        .padding(horizontal = 18.dp, vertical = 5.dp),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        painter = painterResource(
          if (isSelected) screen.selectedIconResId else screen.unselectedIconResId
        ),
        contentDescription = null,
        tint = iconColor,
        modifier = Modifier.size(22.dp)
      )
    }

    Spacer(modifier = Modifier.height(3.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 11.5.sp,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
      ),
      color = labelColor
    )
  }
}
