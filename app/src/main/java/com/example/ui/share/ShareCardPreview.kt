package com.example.ui.share

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.models.ShareCardConfig
import com.example.data.models.ShareCardFormat
import com.example.data.models.ShareCardPayload
import com.example.data.models.ShareCardStyle
import java.io.File

@Composable
fun ShareCardPreview(
  payload: ShareCardPayload,
  config: ShareCardConfig,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isSquare = config.format == ShareCardFormat.SQUARE_1_1

  // Determine colors based on style
  val (canvasBg, surfaceBg, primaryColor, accentColor, textPrimary, textSecondary, borderColor, quoteBg) = remember(config.style) {
    when (config.style) {
      ShareCardStyle.CLASSIC -> StyleColors(
        canvasBg = listOf(Color(0xFFFBF8F4), Color(0xFFF5EDE2)),
        surfaceBg = Color(0xFFFFFFFF),
        primaryColor = Color(0xFFB8644A),
        accentColor = Color(0xFFC99252),
        textPrimary = Color(0xFF28211D),
        textSecondary = Color(0xFF756A63),
        borderColor = Color(0xFFEFE6DB),
        quoteBg = Color(0xFFFCEEEA)
      )
      ShareCardStyle.MIDNIGHT -> StyleColors(
        canvasBg = listOf(Color(0xFF14100E), Color(0xFF221A16)),
        surfaceBg = Color(0xFF1E1815),
        primaryColor = Color(0xFFE4AD70),
        accentColor = Color(0xFFDE8C75),
        textPrimary = Color(0xFFF6EFE9),
        textSecondary = Color(0xFFB3A79F),
        borderColor = Color(0xFF3B312B),
        quoteBg = Color(0xFF2A201B)
      )
      ShareCardStyle.ROSE -> StyleColors(
        canvasBg = listOf(Color(0xFFFFF0EC), Color(0xFFFCE0D8)),
        surfaceBg = Color(0xFFFFFFFF),
        primaryColor = Color(0xFFB8644A),
        accentColor = Color(0xFFE89A89),
        textPrimary = Color(0xFF2D1D18),
        textSecondary = Color(0xFF805C52),
        borderColor = Color(0xFFF3D2C9),
        quoteBg = Color(0xFFFFF5F2)
      )
      ShareCardStyle.MINIMAL -> StyleColors(
        canvasBg = listOf(Color(0xFFFAFAFA), Color(0xFFF0F0F2)),
        surfaceBg = Color(0xFFFFFFFF),
        primaryColor = Color(0xFF222222),
        accentColor = Color(0xFF888888),
        textPrimary = Color(0xFF1A1A1A),
        textSecondary = Color(0xFF666666),
        borderColor = Color(0xFFE5E5EA),
        quoteBg = Color(0xFFF2F2F7)
      )
    }
  }

  Box(
    modifier = modifier
      .aspectRatio(config.format.aspectRatio)
      .clip(RoundedCornerShape(24.dp))
      .background(Brush.verticalGradient(canvasBg))
      .testTag("share_card_preview_box"),
    contentAlignment = Alignment.Center
  ) {
    // Optional background photo
    if (config.usePhotoBackground && !payload.photoPath.isNullOrBlank()) {
      val file = File(payload.photoPath)
      if (file.exists()) {
        AsyncImage(
          model = ImageRequest.Builder(context)
            .data(file)
            .crossfade(true)
            .build(),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient Scrim
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = if (config.style == ShareCardStyle.MIDNIGHT) {
                  listOf(Color(0xBB14100E), Color(0xEE14100E))
                } else {
                  listOf(Color(0xC8FBF8F4), Color(0xF0FBF8F4))
                }
              )
            )
        )
      }
    }

    // Inner Card Container
    Card(
      modifier = Modifier
        .fillMaxSize()
        .padding(if (isSquare) 12.dp else 16.dp)
        .shadow(
          elevation = 8.dp,
          shape = RoundedCornerShape(20.dp),
          ambientColor = accentColor.copy(alpha = 0.2f)
        ),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (config.usePhotoBackground && !payload.photoPath.isNullOrBlank()) {
          surfaceBg.copy(alpha = if (config.style == ShareCardStyle.MIDNIGHT) 0.88f else 0.94f)
        } else {
          surfaceBg
        }
      ),
      border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp)
          .border(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.35f),
            shape = RoundedCornerShape(14.dp)
          )
          .padding(if (isSquare) 12.dp else 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          // 1. Top Subtitle Badge
          val badgeText = payload.subtitle?.uppercase() ?: "CELEBRATE EVERY MOMENT"
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(quoteBg)
              .border(0.75.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(10.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.2.sp
                ),
                color = primaryColor
              )
            }
          }

          Spacer(modifier = Modifier.height(if (isSquare) 8.dp else 14.dp))

          // 2. Title
          Text(
            text = payload.title,
            style = MaterialTheme.typography.headlineMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold,
              fontSize = if (isSquare) 18.sp else 22.sp
            ),
            color = textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Mini Heart Divider
          Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(10.dp)
          )

          Spacer(modifier = Modifier.height(if (isSquare) 8.dp else 12.dp))

          // 3. Huge Highlight Metric
          Text(
            text = payload.mainHighlight,
            style = MaterialTheme.typography.displayLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Normal,
              fontSize = if (isSquare) 28.sp else 38.sp,
              lineHeight = if (isSquare) 32.sp else 42.sp
            ),
            color = primaryColor,
            textAlign = TextAlign.Center
          )

          // 4. Breakdown / Supporting Info
          if (config.showBreakdown && !payload.supportingText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = payload.supportingText,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = if (isSquare) 11.sp else 13.sp,
                fontWeight = FontWeight.Medium
              ),
              color = textSecondary,
              textAlign = TextAlign.Center,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }

          // 5. Date String
          if (config.showDate && !payload.dateString.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = payload.dateString,
              style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              ),
              color = accentColor,
              textAlign = TextAlign.Center
            )
          }

          // 6. Romantic Love Note / Custom Dedication
          val displayQuote = if (config.customMessage.isNotBlank()) {
            config.customMessage
          } else if (config.showQuote && !payload.quoteOrNote.isNullOrBlank()) {
            payload.quoteOrNote
          } else null

          if (!displayQuote.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(if (isSquare) 8.dp else 12.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(12.dp))
                .background(quoteBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "“$displayQuote”",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontFamily = FontFamily.Serif,
                  fontStyle = FontStyle.Italic,
                  fontSize = if (isSquare) 10.sp else 11.sp,
                  lineHeight = 15.sp
                ),
                color = textPrimary,
                textAlign = TextAlign.Center,
                maxLines = if (isSquare) 2 else 3,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          // 7. Watermark / Branding
          if (config.showWatermark) {
            Spacer(modifier = Modifier.height(if (isSquare) 8.dp else 16.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = accentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(8.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = payload.watermarkText,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Serif,
                  fontSize = 9.sp,
                  letterSpacing = 1.sp
                ),
                color = accentColor.copy(alpha = 0.8f)
              )
            }
          }
        }
      }
    }
  }
}

private data class StyleColors(
  val canvasBg: List<Color>,
  val surfaceBg: Color,
  val primaryColor: Color,
  val accentColor: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val borderColor: Color,
  val quoteBg: Color
)
