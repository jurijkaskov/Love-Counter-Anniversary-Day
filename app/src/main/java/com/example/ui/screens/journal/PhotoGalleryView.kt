package com.example.ui.screens.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.MemoryPhotoModel
import com.example.ui.components.MemoryPhotoCard
import com.example.ui.components.PremiumEmptyState
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

enum class PhotoFilterType {
  ALL,
  FAVORITES
}

@Composable
fun PhotoGalleryView(
  photos: List<MemoryPhotoModel>,
  onPhotoClick: (MemoryPhotoModel) -> Unit,
  onAddPhotoClick: () -> Unit,
  onToggleFavorite: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  var selectedFilter by remember { mutableStateOf(PhotoFilterType.ALL) }

  val filteredPhotos = remember(photos, selectedFilter) {
    when (selectedFilter) {
      PhotoFilterType.ALL -> photos.sortedByDescending { it.dateEpochDay }
      PhotoFilterType.FAVORITES -> photos.filter { it.isFavorite }.sortedByDescending { it.dateEpochDay }
    }
  }

  // Group photos by YearMonth
  val groupedPhotos = remember(filteredPhotos) {
    filteredPhotos.groupBy {
      val localDate = LocalDate.ofEpochDay(it.dateEpochDay)
      YearMonth.of(localDate.year, localDate.month)
    }.toSortedMap(compareByDescending { it })
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("photo_gallery_view")
  ) {
    // Header Row with Title, Quote, and "+ Add Photos" Button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(R.string.photos_title),
          style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
          ),
          color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = stringResource(R.string.photos_subtitle),
          style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Serif,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            color = extColors.textMuted
          )
        )
      }

      if (filteredPhotos.isNotEmpty()) {
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onAddPhotoClick)
            .testTag("btn_gallery_add_photo"),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.primary,
          shadowElevation = 2.dp
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.AddPhotoAlternate,
              contentDescription = stringResource(R.string.photos_add_btn),
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = stringResource(R.string.photos_add_btn),
              style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }
    }

    // Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      FilterChip(
        selected = selectedFilter == PhotoFilterType.ALL,
        onClick = { selectedFilter = PhotoFilterType.ALL },
        label = {
          Text(
            text = "${stringResource(R.string.photos_filter_all)} (${photos.size})",
            style = MaterialTheme.typography.labelSmall
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primary,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
          containerColor = extColors.rosewoodContainer.copy(alpha = 0.35f),
          labelColor = extColors.textSecondary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("filter_all_photos")
      )

      FilterChip(
        selected = selectedFilter == PhotoFilterType.FAVORITES,
        onClick = { selectedFilter = PhotoFilterType.FAVORITES },
        leadingIcon = {
          Icon(
            imageVector = if (selectedFilter == PhotoFilterType.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = if (selectedFilter == PhotoFilterType.FAVORITES) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
          )
        },
        label = {
          Text(
            text = "${stringResource(R.string.photos_filter_favorites)} (${photos.count { it.isFavorite }})",
            style = MaterialTheme.typography.labelSmall
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primary,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
          containerColor = extColors.rosewoodContainer.copy(alpha = 0.35f),
          labelColor = extColors.textSecondary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("filter_fav_photos")
      )
    }

    // Main Content: Grid of photos or Empty State
    if (filteredPhotos.isEmpty()) {
      PremiumEmptyState(
        title = if (selectedFilter == PhotoFilterType.FAVORITES) "No Favorite Photos Yet" else stringResource(R.string.photos_empty_title),
        description = if (selectedFilter == PhotoFilterType.FAVORITES) "Tap the heart icon on any memory photo to keep your favorites easily accessible." else stringResource(R.string.photos_empty_desc),
        icon = Icons.Outlined.PhotoLibrary,
        actionButtonText = if (selectedFilter == PhotoFilterType.FAVORITES) null else stringResource(R.string.photos_empty_action),
        onActionClick = if (selectedFilter == PhotoFilterType.FAVORITES) null else onAddPhotoClick,
        modifier = Modifier.weight(1f)
      )
    } else {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Render month by month
        groupedPhotos.forEach { (yearMonth, monthPhotos) ->
          val monthTitle = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

          // Section Header Span
          item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = monthTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = extColors.rosewoodContainer.copy(alpha = 0.5f)
              ) {
                Text(
                  text = "${monthPhotos.size}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                  ),
                  color = extColors.textMuted,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(1.dp)
                  .background(extColors.cardBorderSubtle)
              )
            }
          }

          // Items for this month
          items(
            items = monthPhotos,
            key = { it.id }
          ) { photo ->
            MemoryPhotoCard(
              photo = photo,
              onClick = { onPhotoClick(photo) },
              onToggleFavorite = { onToggleFavorite(photo.id) },
              showCaption = true,
              aspectRatioValue = 1f
            )
          }
        }
      }
    }
  }
}
