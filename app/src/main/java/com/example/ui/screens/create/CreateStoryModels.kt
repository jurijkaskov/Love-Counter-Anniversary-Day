package com.example.ui.screens.create

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Nightlife
import androidx.compose.material.icons.outlined.Recommend
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.R
import com.example.data.models.EventCategory
import com.example.data.models.StoryModel
import com.example.ui.theme.CherishBlush
import com.example.ui.theme.CherishGold
import com.example.ui.theme.CherishRosewood
import com.example.ui.theme.CherishTextPrimary

data class CategoryItemOption(
  val category: EventCategory,
  val titleResId: Int,
  val descResId: Int,
  val icon: ImageVector,
  val defaultIconKey: String
)

val AvailableCategoryOptions = listOf(
  CategoryItemOption(
    category = EventCategory.RELATIONSHIP,
    titleResId = R.string.cat_relationship_title,
    descResId = R.string.cat_relationship_desc,
    icon = Icons.Filled.Favorite,
    defaultIconKey = "favorite"
  ),
  CategoryItemOption(
    category = EventCategory.WEDDING,
    titleResId = R.string.cat_wedding_title,
    descResId = R.string.cat_wedding_desc,
    icon = Icons.Outlined.Celebration,
    defaultIconKey = "celebration"
  ),
  CategoryItemOption(
    category = EventCategory.FIRST_DATE,
    titleResId = R.string.cat_first_date_title,
    descResId = R.string.cat_first_date_desc,
    icon = Icons.Outlined.FavoriteBorder,
    defaultIconKey = "favorite_border"
  ),
  CategoryItemOption(
    category = EventCategory.ENGAGEMENT,
    titleResId = R.string.cat_engagement_title,
    descResId = R.string.cat_engagement_desc,
    icon = Icons.Outlined.VolunteerActivism,
    defaultIconKey = "engagement"
  ),
  CategoryItemOption(
    category = EventCategory.BIRTHDAY,
    titleResId = R.string.cat_birthday_title,
    descResId = R.string.cat_birthday_desc,
    icon = Icons.Default.Cake,
    defaultIconKey = "cake"
  ),
  CategoryItemOption(
    category = EventCategory.SPECIAL_DAY,
    titleResId = R.string.cat_special_day_title,
    descResId = R.string.cat_special_day_desc,
    icon = Icons.Default.Star,
    defaultIconKey = "star"
  ),
  CategoryItemOption(
    category = EventCategory.TRIP,
    titleResId = R.string.cat_trip_title,
    descResId = R.string.cat_trip_desc,
    icon = Icons.Default.Flight,
    defaultIconKey = "flight"
  ),
  CategoryItemOption(
    category = EventCategory.CUSTOM,
    titleResId = R.string.cat_custom_title,
    descResId = R.string.cat_custom_desc,
    icon = Icons.Default.Star,
    defaultIconKey = "star"
  )
)

data class SymbolOption(
  val key: String,
  val label: String,
  val icon: ImageVector
)

val AvailableSymbols = listOf(
  SymbolOption("favorite", "Heart", Icons.Filled.Favorite),
  SymbolOption("celebration", "Rings", Icons.Outlined.Celebration),
  SymbolOption("cake", "Birthday", Icons.Default.Cake),
  SymbolOption("flight", "Trip", Icons.Default.Flight),
  SymbolOption("star", "Star", Icons.Default.Star),
  SymbolOption("florist", "Rose", Icons.Outlined.LocalFlorist),
  SymbolOption("champagne", "Cheers", Icons.Outlined.Nightlife),
  SymbolOption("heart_outline", "Spark", Icons.Outlined.FavoriteBorder)
)

fun getIconForSymbolKey(key: String): ImageVector {
  return AvailableSymbols.find { it.key == key }?.icon ?: Icons.Filled.Favorite
}

fun getIconForCategory(category: EventCategory): ImageVector {
  return when (category) {
    EventCategory.RELATIONSHIP -> Icons.Filled.Favorite
    EventCategory.WEDDING -> Icons.Outlined.Celebration
    EventCategory.FIRST_DATE -> Icons.Outlined.FavoriteBorder
    EventCategory.ENGAGEMENT -> Icons.Outlined.VolunteerActivism
    EventCategory.BIRTHDAY -> Icons.Default.Cake
    EventCategory.TRIP -> Icons.Default.Flight
    EventCategory.SPECIAL_DAY, EventCategory.CUSTOM -> Icons.Default.Star
  }
}

fun getIconForStory(story: StoryModel): ImageVector {
  val fromSymbol = AvailableSymbols.find { it.key == story.iconKey }?.icon
  if (fromSymbol != null) return fromSymbol
  return getIconForCategory(story.category)
}

data class PaletteOption(
  val id: String,
  val name: String,
  val primaryColor: Color,
  val accentColor: Color
)

val AvailablePalettes = listOf(
  PaletteOption("rosewood", "Rosewood Sunset", CherishRosewood, CherishGold),
  PaletteOption("gold", "Champagne Gold", CherishGold, CherishRosewood),
  PaletteOption("blush", "Warm Blush", CherishBlush, CherishRosewood),
  PaletteOption("espresso", "Deep Espresso", CherishTextPrimary, CherishGold)
)
