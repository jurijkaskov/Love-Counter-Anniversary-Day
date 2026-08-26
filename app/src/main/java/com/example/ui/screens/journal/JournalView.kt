package com.example.ui.screens.journal

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.JournalEntryModel
import com.example.data.models.StoryModel
import com.example.ui.components.JournalEntryCard
import com.example.ui.components.PremiumEmptyState
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun JournalView(
  entries: List<JournalEntryModel>,
  stories: List<StoryModel>,
  onEntryClick: (JournalEntryModel) -> Unit,
  onToggleFavorite: (String) -> Unit,
  onWriteMemoryClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  var showFavoritesOnly by remember { mutableStateOf(false) }

  val filteredEntries = remember(entries, showFavoritesOnly) {
    if (showFavoritesOnly) {
      entries.filter { it.isFavorite }
    } else {
      entries
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("journal_view")
  ) {
    // Header & Write Action
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(R.string.journal_title),
          style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = stringResource(R.string.journal_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = extColors.textMuted
        )
      }

      PrimaryButton(
        text = stringResource(R.string.journal_add_entry_btn),
        onClick = onWriteMemoryClick,
        testTag = "journal_write_header_btn",
        icon = Icons.Outlined.Edit
      )
    }

    // Filter Chips Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(if (!showFavoritesOnly) MaterialTheme.colorScheme.primary else extColors.rosewoodContainer.copy(alpha = 0.4f))
          .border(
            width = 1.dp,
            color = if (!showFavoritesOnly) MaterialTheme.colorScheme.primary else extColors.cardBorder,
            shape = RoundedCornerShape(12.dp)
          )
          .clickable { showFavoritesOnly = false }
          .padding(horizontal = 14.dp, vertical = 7.dp)
          .testTag("journal_filter_all")
      ) {
        Text(
          text = stringResource(R.string.journal_filter_all),
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = if (!showFavoritesOnly) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp
          ),
          color = if (!showFavoritesOnly) MaterialTheme.colorScheme.onPrimary else extColors.textSecondary
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(if (showFavoritesOnly) MaterialTheme.colorScheme.primary else extColors.rosewoodContainer.copy(alpha = 0.4f))
          .border(
            width = 1.dp,
            color = if (showFavoritesOnly) MaterialTheme.colorScheme.primary else extColors.cardBorder,
            shape = RoundedCornerShape(12.dp)
          )
          .clickable { showFavoritesOnly = true }
          .padding(horizontal = 14.dp, vertical = 7.dp)
          .testTag("journal_filter_favs")
      ) {
        Text(
          text = stringResource(R.string.journal_filter_favorites),
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = if (showFavoritesOnly) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp
          ),
          color = if (showFavoritesOnly) MaterialTheme.colorScheme.onPrimary else extColors.textSecondary
        )
      }
    }

    // List of Memories or Empty State
    if (filteredEntries.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        PremiumEmptyState(
          title = stringResource(R.string.journal_empty_title),
          description = stringResource(R.string.journal_empty_description),
          actionButtonText = stringResource(R.string.journal_write_cta),
          onActionClick = onWriteMemoryClick,
          icon = Icons.Outlined.MenuBook,
          testTag = "journal_empty_state"
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(filteredEntries, key = { it.id }) { entry ->
          val associatedStory = stories.find { it.id == entry.associatedStoryId }
          JournalEntryCard(
            entry = entry,
            associatedStory = associatedStory,
            onClick = { onEntryClick(entry) },
            onToggleFavorite = { onToggleFavorite(entry.id) }
          )
        }

        item {
          Spacer(modifier = Modifier.height(80.dp))
        }
      }
    }
  }
}
