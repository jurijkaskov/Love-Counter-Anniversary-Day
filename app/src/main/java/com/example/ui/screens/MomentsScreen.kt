package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.R
import com.example.data.StoryRepository
import com.example.data.models.EventCategory
import com.example.data.models.StoryModel
import com.example.ui.components.AddEditEventDialog
import com.example.ui.components.CherishIconButton
import com.example.ui.components.EventDetailsDialog
import com.example.ui.components.MilestoneItemRow
import com.example.ui.components.NextEventHighlightCard
import com.example.ui.components.PremiumEmptyState
import com.example.ui.components.SectionTitle
import com.example.ui.screens.create.getIconForStory
import com.example.ui.theme.LocalCherishExtendedColors

import androidx.compose.ui.platform.LocalContext
import com.example.data.JournalRepository
import com.example.data.MilestoneRepository
import com.example.data.PhotoRepository
import com.example.data.PreferencesManager
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.MilestoneModel
import com.example.ui.components.AddEditJournalEntryDialog
import com.example.ui.components.AddEditPhotoDialog
import com.example.ui.components.JournalEntryDetailsDialog
import com.example.ui.components.PhotoViewerDialog
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.journal.JournalTimelineScreen
import com.example.ui.screens.journal.JournalTimelineTab
import androidx.compose.material.icons.outlined.CalendarMonth

enum class MomentsTabCategory(val labelResId: Int) {
  ALL(R.string.moments_tab_all),
  TIMELINE(R.string.timeline_title),
  JOURNAL(R.string.journal_title),
  PHOTOS(R.string.photos_tab_title),
  CALENDAR(R.string.calendar_title),
  ANNIVERSARIES(R.string.moments_tab_anniversaries),
  SPECIAL(R.string.moments_tab_special),
  BIRTHDAYS(R.string.moments_tab_birthdays),
  MILESTONES(R.string.moments_tab_milestones)
}

@Composable
fun MomentsScreen(
  storyRepository: StoryRepository,
  modifier: Modifier = Modifier,
  milestoneRepository: MilestoneRepository? = null,
  journalRepository: JournalRepository? = null,
  photoRepository: PhotoRepository? = null,
  preferencesManager: PreferencesManager? = null,
  initialTab: MomentsTabCategory = MomentsTabCategory.ALL,
  onAddMoment: () -> Unit = {}
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  val allStories by storyRepository.stories.collectAsState()
  val resolvedMilestoneRepo = remember(milestoneRepository) {
    milestoneRepository ?: MilestoneRepository(context, storyRepository)
  }
  val resolvedJournalRepo = remember(journalRepository) {
    journalRepository ?: JournalRepository(context)
  }
  val resolvedPhotoRepo = remember(photoRepository) {
    photoRepository ?: PhotoRepository(context)
  }
  val resolvedPrefManager = remember(preferencesManager) {
    preferencesManager ?: PreferencesManager(context)
  }

  val allMilestones by resolvedMilestoneRepo.milestones.collectAsState()
  val allJournalEntries by resolvedJournalRepo.entries.collectAsState()
  val allPhotos by resolvedPhotoRepo.photos.collectAsState()

  var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }
  var selectedEventForDetails by remember { mutableStateOf<StoryModel?>(null) }
  var eventToEdit by remember { mutableStateOf<StoryModel?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }

  // Journal Entry Dialog States
  var selectedEntryForDetails by remember { mutableStateOf<JournalEntryModel?>(null) }
  var journalEntryToEdit by remember { mutableStateOf<JournalEntryModel?>(null) }
  var showAddJournalDialog by remember { mutableStateOf(false) }
  var initialStoryIdForJournal by remember { mutableStateOf<String?>(null) }

  // Photo Dialog States
  var selectedPhotoForViewer by remember { mutableStateOf<MemoryPhotoModel?>(null) }
  var photoToEdit by remember { mutableStateOf<MemoryPhotoModel?>(null) }
  var showAddPhotoDialog by remember { mutableStateOf(false) }
  var initialStoryIdForPhoto by remember { mutableStateOf<String?>(null) }
  var initialJournalIdForPhoto by remember { mutableStateOf<String?>(null) }

  // If Timeline, Journal, or Photos tab is selected, display the full Journal & Timeline Experience
  if (selectedTab == MomentsTabCategory.TIMELINE || selectedTab == MomentsTabCategory.JOURNAL || selectedTab == MomentsTabCategory.PHOTOS) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .statusBarsPadding(),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 600.dp)
      ) {
        MomentsCategoryFilterRow(
          selectedCategory = selectedTab,
          onSelectCategory = { selectedTab = it },
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        val targetTab = when (selectedTab) {
          MomentsTabCategory.TIMELINE -> JournalTimelineTab.TIMELINE
          MomentsTabCategory.JOURNAL -> JournalTimelineTab.JOURNAL
          MomentsTabCategory.PHOTOS -> JournalTimelineTab.PHOTOS
          else -> JournalTimelineTab.TIMELINE
        }

        JournalTimelineScreen(
          stories = allStories,
          entries = allJournalEntries,
          milestones = allMilestones,
          photos = allPhotos,
          initialTab = targetTab,
          onStoryClick = { selectedEventForDetails = it },
          onEntryClick = { selectedEntryForDetails = it },
          onMilestoneClick = { selectedTab = MomentsTabCategory.MILESTONES },
          onPhotoClick = { selectedPhotoForViewer = it },
          onAddPhotoClick = {
            initialStoryIdForPhoto = null
            initialJournalIdForPhoto = null
            photoToEdit = null
            showAddPhotoDialog = true
          },
          onWriteMemoryClick = {
            initialStoryIdForJournal = null
            showAddJournalDialog = true
          },
          onToggleEntryFavorite = { resolvedJournalRepo.toggleFavorite(it) },
          onTogglePhotoFavorite = { resolvedPhotoRepo.toggleFavorite(it) },
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Detail and Edit dialogs for Journal
    selectedEntryForDetails?.let { entry ->
      val currentEntry = allJournalEntries.find { it.id == entry.id } ?: entry
      val associatedStory = allStories.find { it.id == currentEntry.associatedStoryId }
      val connectedPhotos = resolvedPhotoRepo.getPhotosForJournal(entry.id)
      JournalEntryDetailsDialog(
        entry = currentEntry,
        associatedStory = associatedStory,
        connectedPhotos = connectedPhotos,
        onPhotoClick = { photo ->
          selectedPhotoForViewer = photo
        },
        onAddPhotoClick = {
          initialStoryIdForPhoto = currentEntry.associatedStoryId
          initialJournalIdForPhoto = currentEntry.id
          photoToEdit = null
          showAddPhotoDialog = true
        },
        onDismiss = { selectedEntryForDetails = null },
        onEdit = {
          selectedEntryForDetails = null
          journalEntryToEdit = it
        },
        onDelete = { id ->
          resolvedJournalRepo.deleteEntry(id)
          resolvedPhotoRepo.unbindJournal(id)
          selectedEntryForDetails = null
        },
        onToggleFavorite = { id ->
          resolvedJournalRepo.toggleFavorite(id)
        }
      )
    }

    if (showAddJournalDialog || journalEntryToEdit != null) {
      AddEditJournalEntryDialog(
        entry = journalEntryToEdit,
        availableStories = allStories,
        initialStoryId = initialStoryIdForJournal,
        onDismiss = {
          showAddJournalDialog = false
          journalEntryToEdit = null
          initialStoryIdForJournal = null
        },
        onSave = { updatedEntry ->
          resolvedJournalRepo.saveEntry(updatedEntry)
          showAddJournalDialog = false
          journalEntryToEdit = null
          initialStoryIdForJournal = null
        }
      )
    }

    selectedEventForDetails?.let { event ->
      val currentStory = allStories.find { it.id == event.id } ?: event
      val relatedEntries = resolvedJournalRepo.getEntriesForStory(event.id)
      val relatedPhotos = resolvedPhotoRepo.getPhotosForStory(event.id)
      EventDetailsDialog(
        event = currentStory,
        onDismiss = { selectedEventForDetails = null },
        onEdit = {
          selectedEventForDetails = null
          eventToEdit = it
        },
        onDelete = { id ->
          storyRepository.deleteStory(id)
          resolvedJournalRepo.unbindStory(id)
          resolvedPhotoRepo.unbindStory(id)
          selectedEventForDetails = null
        },
        onToggleFavorite = { id ->
          storyRepository.toggleFavorite(id)
        },
        onUpdateStory = { updatedStory ->
          storyRepository.saveStory(updatedStory)
        },
        relatedEntries = relatedEntries,
        relatedPhotos = relatedPhotos,
        onAddMemory = { storyId ->
          initialStoryIdForJournal = storyId
          showAddJournalDialog = true
        },
        onAddPhoto = { storyId ->
          initialStoryIdForPhoto = storyId
          initialJournalIdForPhoto = null
          photoToEdit = null
          showAddPhotoDialog = true
        },
        onEntryClick = { entry ->
          selectedEntryForDetails = entry
        },
        onPhotoClick = { photo ->
          selectedPhotoForViewer = photo
        }
      )
    }

    // Photo Viewer and Add/Edit Dialogs
    selectedPhotoForViewer?.let { photo ->
      PhotoViewerDialog(
        photos = allPhotos,
        initialPhotoId = photo.id,
        stories = allStories,
        journalEntries = allJournalEntries,
        onDismiss = { selectedPhotoForViewer = null },
        onEditPhoto = {
          photoToEdit = it
        },
        onDeletePhoto = { id ->
          resolvedPhotoRepo.deletePhoto(id)
        },
        onToggleFavorite = { id ->
          resolvedPhotoRepo.toggleFavorite(id)
        },
        onStoryClick = { story ->
          selectedEventForDetails = story
        },
        onJournalClick = { journal ->
          selectedEntryForDetails = journal
        }
      )
    }

    if (showAddPhotoDialog || photoToEdit != null) {
      AddEditPhotoDialog(
        photoToEdit = photoToEdit,
        availableStories = allStories,
        availableJournalEntries = allJournalEntries,
        initialStoryId = initialStoryIdForPhoto,
        initialJournalId = initialJournalIdForPhoto,
        onDismiss = {
          showAddPhotoDialog = false
          photoToEdit = null
          initialStoryIdForPhoto = null
          initialJournalIdForPhoto = null
        },
        onSave = { dateEpochDay, caption, selectedUri, storyId, journalId ->
          if (photoToEdit != null) {
            val updated = photoToEdit!!.copy(
              dateEpochDay = dateEpochDay,
              caption = caption,
              associatedStoryId = storyId,
              associatedJournalId = journalId
            )
            resolvedPhotoRepo.savePhoto(updated)
          } else if (selectedUri != null) {
            resolvedPhotoRepo.importPhotoFromUri(
              uri = selectedUri,
              caption = caption,
              dateEpochDay = dateEpochDay,
              storyId = storyId,
              journalId = journalId
            )
          }
          showAddPhotoDialog = false
          photoToEdit = null
          initialStoryIdForPhoto = null
          initialJournalIdForPhoto = null
        }
      )
    }

    if (showAddDialog || eventToEdit != null) {
      AddEditEventDialog(
        eventToEdit = eventToEdit,
        onDismiss = {
          showAddDialog = false
          eventToEdit = null
        },
        onSave = { updatedStory ->
          storyRepository.saveStory(updatedStory)
          showAddDialog = false
          eventToEdit = null
        }
      )
    }

    return
  }

  // If Calendar tab is selected, display the full Calendar Experience
  if (selectedTab == MomentsTabCategory.CALENDAR) {
    CalendarScreen(
      storyRepository = storyRepository,
      milestoneRepository = resolvedMilestoneRepo,
      preferencesManager = resolvedPrefManager,
      onNavigateToTimeline = { selectedTab = MomentsTabCategory.TIMELINE },
      modifier = modifier
    )
    return
  }

  // If Milestones tab is selected, display the specialized Milestones & Tasks experience
  if (selectedTab == MomentsTabCategory.MILESTONES) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .statusBarsPadding(),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 600.dp)
      ) {
        // Sticky moments category tabs on top so user can easily switch between Moments and Milestones
        MomentsCategoryFilterRow(
          selectedCategory = selectedTab,
          onSelectCategory = { selectedTab = it },
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        MilestonesScreen(
          milestoneRepository = resolvedMilestoneRepo,
          storyRepository = storyRepository,
          modifier = Modifier.weight(1f)
        )
      }
    }
    return
  }

  // Filter events based on active tab
  val filteredStories: List<StoryModel> = remember(selectedTab, allStories) {
    when (selectedTab) {
      MomentsTabCategory.ALL, MomentsTabCategory.CALENDAR, MomentsTabCategory.TIMELINE, MomentsTabCategory.JOURNAL, MomentsTabCategory.PHOTOS -> allStories
      MomentsTabCategory.ANNIVERSARIES -> allStories.filter {
        it.category == EventCategory.WEDDING ||
          it.category == EventCategory.RELATIONSHIP ||
          it.category == EventCategory.FIRST_DATE ||
          it.category == EventCategory.ENGAGEMENT
      }
      MomentsTabCategory.SPECIAL -> allStories.filter {
        it.category == EventCategory.SPECIAL_DAY ||
          it.category == EventCategory.TRIP ||
          it.category == EventCategory.CUSTOM
      }
      MomentsTabCategory.BIRTHDAYS -> allStories.filter {
        it.category == EventCategory.BIRTHDAY
      }
      MomentsTabCategory.MILESTONES -> allStories.filter {
        !it.isPrimary
      }
    }
  }

  // Upcoming events (future date OR recurring future occurrence)
  val upcomingEvents: List<StoryModel> = remember(filteredStories) {
    filteredStories
      .filter { !it.isPastDate || it.daysUntilNextAnniversary > 0 }
      .sortedBy { it.daysUntilNextOccurrence }
  }

  // Nearest upcoming event for the highlight card
  val nearestUpcomingEvent: StoryModel? = remember(upcomingEvents) {
    upcomingEvents.firstOrNull()
  }

  // Past events (sorted reverse chronological)
  val pastEvents: List<StoryModel> = remember(filteredStories) {
    filteredStories
      .filter { it.isPastDate }
      .sortedByDescending { it.dateEpochDay }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding(),
    contentAlignment = Alignment.TopCenter
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 600.dp)
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Header
      item {
        Spacer(modifier = Modifier.height(12.dp))
        MomentsHeader(
          onAddClick = { showAddDialog = true },
          onCalendarClick = { selectedTab = MomentsTabCategory.CALENDAR }
        )
      }

      // 2. Category Filter Tabs
      item {
        MomentsCategoryFilterRow(
          selectedCategory = selectedTab,
          onSelectCategory = { selectedTab = it }
        )
      }

      // Check if completely empty
      if (filteredStories.isEmpty()) {
        item {
          Spacer(modifier = Modifier.height(32.dp))
          PremiumEmptyState(
            title = stringResource(R.string.moments_empty_title),
            description = stringResource(R.string.moments_empty_description),
            icon = Icons.Outlined.Celebration,
            actionButtonText = stringResource(R.string.moments_add_button),
            onActionClick = { showAddDialog = true },
            testTag = "moments_empty_state"
          )
        }
      } else {
        // 3. Highlight Card (Nearest Upcoming Event)
        if (nearestUpcomingEvent != null) {
          item {
            Spacer(modifier = Modifier.height(4.dp))
            NextEventHighlightCard(
              event = nearestUpcomingEvent,
              onClick = { selectedEventForDetails = nearestUpcomingEvent },
              onFavoriteClick = { storyRepository.toggleFavorite(nearestUpcomingEvent.id) },
              testTag = "moments_featured_next_card"
            )
          }
        }

        // 4. Upcoming Events Section
        if (upcomingEvents.isNotEmpty()) {
          item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionTitle(
              title = stringResource(R.string.moments_upcoming_header),
              subtitle = "${upcomingEvents.size} special dates ahead",
              testTag = "upcoming_section_header"
            )
          }

          items(upcomingEvents, key = { it.id }) { event ->
            val icon = getIconForStory(event)
            MilestoneItemRow(
              title = event.displayTitle,
              dateFormatted = event.formattedNextOccurrenceDate,
              badgeText = event.countdownBadgeText,
              icon = icon,
              iconBackground = extColors.goldContainer,
              iconTint = extColors.goldAccent,
              isFavorite = event.isFavorite,
              onFavoriteClick = { storyRepository.toggleFavorite(event.id) },
              onClick = { selectedEventForDetails = event },
              testTag = "moment_upcoming_${event.id}"
            )
          }
        }

        // 5. Past Moments Section
        if (pastEvents.isNotEmpty()) {
          item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionTitle(
              title = stringResource(R.string.moments_past_header),
              subtitle = "${pastEvents.size} cherished memories",
              testTag = "past_section_header"
            )
          }

          items(pastEvents, key = { "past_${it.id}" }) { event ->
            val icon = getIconForStory(event)
            MilestoneItemRow(
              title = event.displayTitle,
              dateFormatted = event.formattedDate,
              badgeText = event.elapsedBadgeText,
              icon = icon,
              iconBackground = extColors.rosewoodContainer,
              iconTint = MaterialTheme.colorScheme.primary,
              isFavorite = event.isFavorite,
              onFavoriteClick = { storyRepository.toggleFavorite(event.id) },
              onClick = { selectedEventForDetails = event },
              testTag = "moment_past_${event.id}"
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(96.dp))
      }
    }
  }

  // Event Details Dialog
  selectedEventForDetails?.let { event ->
    // Keep updated from repository
    val currentStory = allStories.find { it.id == event.id } ?: event
    val relatedEntries = resolvedJournalRepo.getEntriesForStory(event.id)
    val relatedPhotos = resolvedPhotoRepo.getPhotosForStory(event.id)
    EventDetailsDialog(
      event = currentStory,
      onDismiss = { selectedEventForDetails = null },
      onEdit = {
        selectedEventForDetails = null
        eventToEdit = it
      },
      onDelete = { id ->
        storyRepository.deleteStory(id)
        resolvedJournalRepo.unbindStory(id)
        resolvedPhotoRepo.unbindStory(id)
        selectedEventForDetails = null
      },
      onToggleFavorite = { id ->
        storyRepository.toggleFavorite(id)
      },
      onUpdateStory = { updatedStory ->
        storyRepository.saveStory(updatedStory)
      },
      relatedEntries = relatedEntries,
      relatedPhotos = relatedPhotos,
      onAddMemory = { storyId ->
        initialStoryIdForJournal = storyId
        showAddJournalDialog = true
      },
      onAddPhoto = { storyId ->
        initialStoryIdForPhoto = storyId
        initialJournalIdForPhoto = null
        photoToEdit = null
        showAddPhotoDialog = true
      },
      onEntryClick = { entry ->
        selectedEntryForDetails = entry
      },
      onPhotoClick = { photo ->
        selectedPhotoForViewer = photo
      }
    )
  }

  // Detail dialog for Journal if opened from related memories
  selectedEntryForDetails?.let { entry ->
    val currentEntry = allJournalEntries.find { it.id == entry.id } ?: entry
    val associatedStory = allStories.find { it.id == currentEntry.associatedStoryId }
    val connectedPhotos = resolvedPhotoRepo.getPhotosForJournal(entry.id)
    JournalEntryDetailsDialog(
      entry = currentEntry,
      associatedStory = associatedStory,
      connectedPhotos = connectedPhotos,
      onPhotoClick = { photo ->
        selectedPhotoForViewer = photo
      },
      onAddPhotoClick = {
        initialStoryIdForPhoto = currentEntry.associatedStoryId
        initialJournalIdForPhoto = currentEntry.id
        photoToEdit = null
        showAddPhotoDialog = true
      },
      onDismiss = { selectedEntryForDetails = null },
      onEdit = {
        selectedEntryForDetails = null
        journalEntryToEdit = it
      },
      onDelete = { id ->
        resolvedJournalRepo.deleteEntry(id)
        resolvedPhotoRepo.unbindJournal(id)
        selectedEntryForDetails = null
      },
      onToggleFavorite = { id ->
        resolvedJournalRepo.toggleFavorite(id)
      }
    )
  }

  if (showAddJournalDialog || journalEntryToEdit != null) {
    AddEditJournalEntryDialog(
      entry = journalEntryToEdit,
      availableStories = allStories,
      initialStoryId = initialStoryIdForJournal,
      onDismiss = {
        showAddJournalDialog = false
        journalEntryToEdit = null
        initialStoryIdForJournal = null
      },
      onSave = { updatedEntry ->
        resolvedJournalRepo.saveEntry(updatedEntry)
        showAddJournalDialog = false
        journalEntryToEdit = null
        initialStoryIdForJournal = null
      }
    )
  }

  // Photo Viewer and Add/Edit Dialogs
  selectedPhotoForViewer?.let { photo ->
    PhotoViewerDialog(
      photos = allPhotos,
      initialPhotoId = photo.id,
      stories = allStories,
      journalEntries = allJournalEntries,
      onDismiss = { selectedPhotoForViewer = null },
      onEditPhoto = {
        photoToEdit = it
      },
      onDeletePhoto = { id ->
        resolvedPhotoRepo.deletePhoto(id)
      },
      onToggleFavorite = { id ->
        resolvedPhotoRepo.toggleFavorite(id)
      },
      onStoryClick = { story ->
        selectedEventForDetails = story
      },
      onJournalClick = { journal ->
        selectedEntryForDetails = journal
      }
    )
  }

  if (showAddPhotoDialog || photoToEdit != null) {
    AddEditPhotoDialog(
      photoToEdit = photoToEdit,
      availableStories = allStories,
      availableJournalEntries = allJournalEntries,
      initialStoryId = initialStoryIdForPhoto,
      initialJournalId = initialJournalIdForPhoto,
      onDismiss = {
        showAddPhotoDialog = false
        photoToEdit = null
        initialStoryIdForPhoto = null
        initialJournalIdForPhoto = null
      },
      onSave = { dateEpochDay, caption, selectedUri, storyId, journalId ->
        if (photoToEdit != null) {
          val updated = photoToEdit!!.copy(
            dateEpochDay = dateEpochDay,
            caption = caption,
            associatedStoryId = storyId,
            associatedJournalId = journalId
          )
          resolvedPhotoRepo.savePhoto(updated)
        } else if (selectedUri != null) {
          resolvedPhotoRepo.importPhotoFromUri(
            uri = selectedUri,
            caption = caption,
            dateEpochDay = dateEpochDay,
            storyId = storyId,
            journalId = journalId
          )
        }
        showAddPhotoDialog = false
        photoToEdit = null
        initialStoryIdForPhoto = null
        initialJournalIdForPhoto = null
      }
    )
  }

  // Add / Edit Dialog
  if (showAddDialog || eventToEdit != null) {
    AddEditEventDialog(
      eventToEdit = eventToEdit,
      onDismiss = {
        showAddDialog = false
        eventToEdit = null
      },
      onSave = { updatedStory ->
        storyRepository.saveStory(updatedStory)
        showAddDialog = false
        eventToEdit = null
      }
    )
  }
}

@Composable
private fun MomentsHeader(
  onAddClick: () -> Unit,
  onCalendarClick: () -> Unit
) {
  val extColors = LocalCherishExtendedColors.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("moments_header"),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = stringResource(R.string.moments_title),
        style = MaterialTheme.typography.displaySmall.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = stringResource(R.string.moments_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = extColors.textMuted
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      CherishIconButton(
        icon = Icons.Outlined.CalendarMonth,
        contentDescription = stringResource(R.string.calendar_title),
        onClick = onCalendarClick,
        backgroundColor = extColors.cardBorderSubtle,
        tint = MaterialTheme.colorScheme.onSurface,
        testTag = "moments_calendar_toggle_button"
      )

      CherishIconButton(
        icon = Icons.Default.Add,
        contentDescription = stringResource(R.string.moments_add_button),
        onClick = onAddClick,
        backgroundColor = MaterialTheme.colorScheme.primary,
        tint = MaterialTheme.colorScheme.onPrimary,
        testTag = "add_moment_top_button"
      )
    }
  }
}

@Composable
private fun MomentsCategoryFilterRow(
  selectedCategory: MomentsTabCategory,
  onSelectCategory: (MomentsTabCategory) -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current

  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .testTag("category_filter_row"),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    MomentsTabCategory.entries.forEach { category ->
      val isSelected = category == selectedCategory
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(if (isSelected) MaterialTheme.colorScheme.onBackground else extColors.cardBorderSubtle)
          .clickable { onSelectCategory(category) }
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("filter_chip_${category.name.lowercase()}"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = stringResource(category.labelResId),
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
          ),
          color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
