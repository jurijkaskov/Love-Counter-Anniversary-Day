package com.example.ui.screens.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.EventCategory
import com.example.data.models.StoryModel
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Composable
fun Step5PreviewConfirmation(
  category: EventCategory,
  yourName: String,
  partnerName: String,
  storyTitle: String,
  selectedDate: LocalDate,
  personalNote: String,
  selectedSymbolKey: String,
  selectedThemeAccent: String,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current

  val today = LocalDate.now()
  val isPast = !selectedDate.isAfter(today)
  val daysDiff = abs(ChronoUnit.DAYS.between(selectedDate, today))

  val displayTitle = when {
    storyTitle.isNotBlank() -> storyTitle
    yourName.isNotBlank() && partnerName.isNotBlank() -> "$yourName & $partnerName"
    yourName.isNotBlank() -> yourName
    partnerName.isNotBlank() -> partnerName
    else -> category.defaultTitle
  }

  val displayInitials = run {
    val f = yourName.trim().firstOrNull()?.uppercaseChar()
    val s = partnerName.trim().firstOrNull()?.uppercaseChar()
    if (f != null && s != null) "$f & $s" else if (f != null) "$f" else if (s != null) "$s" else "♥"
  }

  val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
  val symbolIcon = getIconForSymbolKey(selectedSymbolKey)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("create_step_5_container"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Badge
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(extColors.goldContainer)
        .border(1.dp, extColors.goldAccent.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Text(
        text = stringResource(R.string.create_step5_badge),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        ),
        color = extColors.goldAccent
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Headline
    Text(
      text = stringResource(R.string.create_step5_headline),
      style = MaterialTheme.typography.headlineMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Subtitle
    Text(
      text = stringResource(R.string.create_step5_subhead),
      style = MaterialTheme.typography.bodyMedium,
      color = extColors.textMuted
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Preview Card (Styled beautifully like the OuiLove reference card)
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(16.dp, RoundedCornerShape(28.dp), ambientColor = extColors.goldAccent.copy(alpha = 0.25f))
        .clip(RoundedCornerShape(28.dp))
        .testTag("confirmation_preview_card"),
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface,
      border = BorderStroke(1.5.dp, extColors.goldAccent.copy(alpha = 0.4f))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            brush = Brush.verticalGradient(
              colors = listOf(
                extColors.rosewoodContainer.copy(alpha = 0.7f),
                extColors.goldContainer.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.surface
              )
            )
          )
          .padding(24.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top bar with Initials & Category Badge
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = category.defaultTitle.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(extColors.goldContainer)
                .border(1.dp, extColors.goldAccent.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = displayInitials,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = extColors.goldAccent
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          val color1 = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.035f)
          val color2 = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.045f)
          // Floating Symbol Icon
          Box(
            modifier = Modifier
              .size(56.dp)
              .drawBehind {
                val shadowOffset = 6.dp.toPx()
                val radius = size.minDimension / 2f

                // Мягкий внешний круглый ореол
                drawCircle(
                  color = color1,
                  radius = radius + shadowOffset * 0.8f,
                  center = Offset(
                    size.width / 2f,
                    size.height / 2f + shadowOffset * 0.35f
                  )
                )

                // Более близкая мягкая тень
                drawCircle(
                  color = color2,
                  radius = radius + shadowOffset * 0.35f,
                  center = Offset(
                    size.width / 2f,
                    size.height / 2f + shadowOffset * 0.2f
                  )
                )
              }
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface)
              .border(
                1.5.dp,
                extColors.goldAccent.copy(alpha = 0.5f),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = symbolIcon,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp)
            )
          }
          /*Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface)
              .shadow(6.dp, CircleShape)
              .border(1.5.dp, extColors.goldAccent.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = symbolIcon,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(28.dp)
            )
          }*/

          Spacer(modifier = Modifier.height(14.dp))

          // Story Title
          Text(
            text = displayTitle,
            style = MaterialTheme.typography.headlineSmall.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Big Counter
          Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = if (daysDiff == 0L && isPast) "0" else String.format("%,d", daysDiff),
              style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 44.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isPast) stringResource(R.string.create_step5_days_together) else stringResource(R.string.create_step5_days_to_go),
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic
              ),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(bottom = 6.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Date tag
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = extColors.goldAccent,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = formattedDate,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
              color = extColors.textMuted
            )
          }

          // Personal Note / Quote if provided
          if (personalNote.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp))
                .padding(14.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "“$personalNote”",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontStyle = FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }
    }
  }
}
