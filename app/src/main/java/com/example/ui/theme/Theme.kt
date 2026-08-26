package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.data.models.AccentColorStyle
import com.example.data.models.ThemeMode

data class CherishExtendedColors(
  val cardBorder: Color,
  val cardBorderSubtle: Color,
  val goldAccent: Color,
  val goldContainer: Color,
  val rosewoodContainer: Color,
  val blushAccent: Color,
  val blushContainer: Color,
  val textMuted: Color,
  val textSecondary: Color,
  val quoteBackground: Color,
  val accentHighlight: Color,
  val accentContainer: Color,
)

val LocalCherishExtendedColors = staticCompositionLocalOf {
  CherishExtendedColors(
    cardBorder = CherishCardBorder,
    cardBorderSubtle = CherishCardBorderSubtle,
    goldAccent = CherishGold,
    goldContainer = CherishGoldContainer,
    rosewoodContainer = CherishRosewoodContainer,
    blushAccent = CherishBlush,
    blushContainer = CherishBlushContainer,
    textMuted = CherishTextMuted,
    textSecondary = CherishTextSecondary,
    quoteBackground = CherishSurfaceVariant,
    accentHighlight = CherishGold,
    accentContainer = CherishGoldContainer
  )
}

fun buildCherishColorScheme(
  isDark: Boolean,
  accentStyle: AccentColorStyle = AccentColorStyle.CHAMPAGNE_GOLD
): ColorScheme {
  val primaryColor = Color(if (isDark) accentStyle.darkPrimaryHex else accentStyle.lightPrimaryHex)
  val primaryContainer = Color(if (isDark) accentStyle.darkContainerHex else accentStyle.lightContainerHex)
  val onPrimaryContainer = if (isDark) CherishDarkTextPrimary else CherishTextPrimary

  return if (isDark) {
    darkColorScheme(
      primary = primaryColor,
      onPrimary = CherishDarkBg,
      primaryContainer = primaryContainer,
      onPrimaryContainer = onPrimaryContainer,
      secondary = CherishDarkGold,
      onSecondary = CherishDarkBg,
      secondaryContainer = CherishDarkGoldContainer,
      onSecondaryContainer = CherishDarkTextPrimary,
      tertiary = primaryColor,
      onTertiary = CherishDarkBg,
      tertiaryContainer = primaryContainer,
      onTertiaryContainer = CherishDarkTextPrimary,
      background = CherishDarkBg,
      onBackground = CherishDarkTextPrimary,
      surface = CherishDarkSurface,
      onSurface = CherishDarkTextPrimary,
      surfaceVariant = CherishDarkSurfaceVariant,
      onSurfaceVariant = CherishDarkTextSecondary,
      outline = CherishDarkCardBorder,
      outlineVariant = CherishDarkDivider
    )
  } else {
    lightColorScheme(
      primary = primaryColor,
      onPrimary = Color.White,
      primaryContainer = primaryContainer,
      onPrimaryContainer = CherishRosewoodDark,
      secondary = CherishGold,
      onSecondary = Color.White,
      secondaryContainer = CherishGoldContainer,
      onSecondaryContainer = CherishRosewoodDark,
      tertiary = CherishBlush,
      onTertiary = Color.White,
      tertiaryContainer = CherishBlushContainer,
      onTertiaryContainer = CherishRosewoodDark,
      background = CherishIvoryBg,
      onBackground = CherishTextPrimary,
      surface = CherishSurface,
      onSurface = CherishTextPrimary,
      surfaceVariant = CherishSurfaceVariant,
      onSurfaceVariant = CherishTextSecondary,
      outline = CherishCardBorder,
      outlineVariant = CherishDivider
    )
  }
}

@Composable
fun CherishTheme(
  themeMode: ThemeMode = ThemeMode.SYSTEM,
  accentStyle: AccentColorStyle = AccentColorStyle.CHAMPAGNE_GOLD,
  darkTheme: Boolean = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
  },
  content: @Composable () -> Unit,
) {
  val isDark = darkTheme
  val colorScheme = buildCherishColorScheme(isDark, accentStyle)
  val accentHighlight = Color(accentStyle.goldHighlightHex)
  val accentContainer = Color(if (isDark) accentStyle.darkContainerHex else accentStyle.lightContainerHex)

  val extendedColors = if (isDark) {
    CherishExtendedColors(
      cardBorder = CherishDarkCardBorder,
      cardBorderSubtle = CherishDarkCardBorder.copy(alpha = 0.5f),
      goldAccent = CherishDarkGold,
      goldContainer = CherishDarkGoldContainer,
      rosewoodContainer = accentContainer,
      blushAccent = CherishDarkRosewood,
      blushContainer = CherishDarkRosewoodContainer,
      textMuted = CherishDarkTextMuted,
      textSecondary = CherishDarkTextSecondary,
      quoteBackground = CherishDarkSurfaceVariant,
      accentHighlight = accentHighlight,
      accentContainer = accentContainer
    )
  } else {
    CherishExtendedColors(
      cardBorder = CherishCardBorder,
      cardBorderSubtle = CherishCardBorderSubtle,
      goldAccent = CherishGold,
      goldContainer = CherishGoldContainer,
      rosewoodContainer = accentContainer,
      blushAccent = CherishBlush,
      blushContainer = CherishBlushContainer,
      textMuted = CherishTextMuted,
      textSecondary = CherishTextSecondary,
      quoteBackground = CherishSurfaceVariant,
      accentHighlight = accentHighlight,
      accentContainer = accentContainer
    )
  }

  CompositionLocalProvider(LocalCherishExtendedColors provides extendedColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
