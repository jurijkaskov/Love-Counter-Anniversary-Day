package com.example.ui.screens.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalCherishExtendedColors

/**
 * Screen 1 Visual: Warm romantic halo with interlocking double hearts,
 * soft radiant sparkles, and golden glow inspired by the OuiLove branding.
 */
@Composable
fun WelcomeHeroVisual(modifier: Modifier = Modifier) {
  val extColors = LocalCherishExtendedColors.current
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_halo")

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(2800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  val shimmerAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "shimmer"
  )

  Box(
    modifier = modifier
      .size(260.dp),
    contentAlignment = Alignment.Center
  ) {
    // Outer ambient glowing rings
    Box(
      modifier = Modifier
        .size(240.dp)
        .scale(pulseScale)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              extColors.goldAccent.copy(alpha = 0.18f * shimmerAlpha),
              extColors.rosewoodContainer.copy(alpha = 0.12f),
              Color.Transparent
            )
          )
        )
    )

    // Mid subtle ring
    Box(
      modifier = Modifier
        .size(190.dp)
        .clip(CircleShape)
        .border(
          width = 1.dp,
          brush = Brush.sweepGradient(
            listOf(
              extColors.goldAccent.copy(alpha = 0.35f),
              Color.Transparent,
              extColors.goldAccent.copy(alpha = 0.45f),
              Color.Transparent
            )
          ),
          shape = CircleShape
        )
    )

    // Central card surface
    Surface(
      modifier = Modifier
        .size(140.dp)
        .shadow(
          elevation = 12.dp,
          shape = RoundedCornerShape(36.dp),
          ambientColor = extColors.goldAccent.copy(alpha = 0.2f),
          spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ),
      shape = RoundedCornerShape(36.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder)
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val center = Offset(size.width / 2f, size.height / 2f)

          // Draw decorative sparkling stars
          val starColor = extColors.goldAccent.copy(alpha = shimmerAlpha)
          drawCircle(
            color = starColor,
            radius = 3.dp.toPx(),
            center = Offset(center.x + 36.dp.toPx(), center.y - 34.dp.toPx())
          )
          drawCircle(
            color = starColor.copy(alpha = 0.6f),
            radius = 2.dp.toPx(),
            center = Offset(center.x - 38.dp.toPx(), center.y + 28.dp.toPx())
          )
        }

        // Interlocking romantic double hearts
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
              .size(52.dp)
              .rotate(-10f)
          )
          Icon(
            imageVector = Icons.Outlined.Favorite,
            contentDescription = null,
            tint = extColors.goldAccent,
            modifier = Modifier
              .size(36.dp)
              .offset(x = (-12).dp, y = 6.dp)
              .rotate(14f)
          )
        }
      }
    }
  }
}

/**
 * Screen 2 Visual: Elegant stacked milestone reminder cards previewing
 * upcoming anniversaries, celebrations, and date badges.
 */
@Composable
fun MilestonesHeroVisual(modifier: Modifier = Modifier) {
  val extColors = LocalCherishExtendedColors.current

  Box(
    modifier = modifier
      .size(280.dp, 240.dp),
    contentAlignment = Alignment.Center
  ) {
    // Back card (subtle rotated)
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .offset(y = (-24).dp)
        .rotate(-4f)
        .shadow(4.dp, RoundedCornerShape(18.dp)),
      shape = RoundedCornerShape(18.dp),
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
      border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorderSubtle)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(extColors.goldContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Flight,
            contentDescription = null,
            tint = extColors.goldAccent,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = stringResource(R.string.onboarding_visual_trip),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = stringResource(R.string.onboarding_visual_trip_date),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = extColors.textMuted
          )
        }
      }
    }

    // Mid card (subtle rotated right)
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .offset(y = (-4).dp)
        .rotate(2.5f)
        .shadow(6.dp, RoundedCornerShape(18.dp)),
      shape = RoundedCornerShape(18.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(extColors.rosewoodContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Cake,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = stringResource(R.string.onboarding_visual_first_date),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = stringResource(R.string.onboarding_visual_first_date_val),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = extColors.textMuted
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(extColors.goldAccent.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = stringResource(R.string.onboarding_visual_first_date_days),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            ),
            color = extColors.goldAccent
          )
        }
      }
    }

    // Foreground Primary Card
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .offset(y = 28.dp)
        .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, extColors.goldAccent.copy(alpha = 0.6f))
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(extColors.goldContainer)
            .border(1.dp, extColors.goldAccent.copy(alpha = 0.4f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Outlined.Celebration,
            contentDescription = null,
            tint = extColors.goldAccent,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = stringResource(R.string.onboarding_visual_wedding),
            style = MaterialTheme.typography.titleSmall.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(1.dp))
          Text(
            text = stringResource(R.string.onboarding_visual_wedding_countdown),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
            color = extColors.textMuted
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = stringResource(R.string.onboarding_visual_wedding_days),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onPrimary
          )
        }
      }
    }
  }
}

/**
 * Screen 3 Visual: Romantic connected timeline illustrating "Your Story",
 * showing memory milestones interconnected along a glowing path.
 */
@Composable
fun YourStoryHeroVisual(modifier: Modifier = Modifier) {
  val extColors = LocalCherishExtendedColors.current

  Surface(
    modifier = modifier
      .size(280.dp, 240.dp)
      .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = extColors.goldAccent.copy(alpha = 0.15f)),
    shape = RoundedCornerShape(24.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(18.dp)
    ) {
      // Background connecting dashed path
      Canvas(modifier = Modifier.fillMaxSize()) {
        val startX = 26.dp.toPx()
        val path = Path().apply {
          moveTo(startX, 24.dp.toPx())
          lineTo(startX, size.height - 24.dp.toPx())
        }
        drawPath(
          path = path,
          color = extColors.goldAccent.copy(alpha = 0.45f),
          style = Stroke(
            width = 2.dp.toPx(),
            cap = StrokeCap.Round
          )
        )
      }

      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        TimelineStepRow(
          icon = Icons.Default.Favorite,
          title = stringResource(R.string.onboarding_visual_met),
          date = stringResource(R.string.onboarding_visual_met_date),
          isHighlighted = false
        )

        TimelineStepRow(
          icon = Icons.Outlined.PhotoCamera,
          title = stringResource(R.string.onboarding_visual_journey),
          date = stringResource(R.string.onboarding_visual_journey_desc),
          isHighlighted = false
        )

        TimelineStepRow(
          icon = Icons.Outlined.Stars,
          title = stringResource(R.string.onboarding_visual_forever),
          date = stringResource(R.string.onboarding_visual_forever_desc),
          isHighlighted = true
        )
      }
    }
  }
}

@Composable
private fun TimelineStepRow(
  icon: ImageVector,
  title: String,
  date: String,
  isHighlighted: Boolean
) {
  val extColors = LocalCherishExtendedColors.current

  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(CircleShape)
        .background(
          if (isHighlighted) MaterialTheme.colorScheme.primary else extColors.rosewoodContainer
        )
        .border(
          width = 1.5.dp,
          color = if (isHighlighted) extColors.goldAccent else Color.Transparent,
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isHighlighted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(16.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
          fontFamily = if (isHighlighted) FontFamily.Serif else FontFamily.Default,
          fontWeight = FontWeight.SemiBold
        ),
        color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = date,
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
        color = extColors.textMuted
      )
    }
  }
}
