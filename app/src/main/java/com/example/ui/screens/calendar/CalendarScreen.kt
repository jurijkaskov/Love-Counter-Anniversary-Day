package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.R
import com.example.data.MilestoneRepository
import com.example.data.PreferencesManager
import com.example.data.StoryRepository
import com.example.data.models.FirstDayOfWeekOption
import com.example.data.models.MilestoneModel
import com.example.data.models.StoryModel
import com.example.ui.components.AddEditEventDialog
import com.example.ui.components.AddEditMilestoneDialog
import com.example.ui.components.EventDetailsDialog
import com.example.ui.components.PremiumEmptyState
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
  storyRepository: StoryRepository,
  milestoneRepository: MilestoneRepository,
  preferencesManager: PreferencesManager,
  onNavigateToTimeline: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val stories by storyRepository.stories.collectAsState()
  val allMilestones by milestoneRepository.milestones.collectAsState()
  val allTasks by milestoneRepository.tasks.collectAsState()
  val userSettings by preferencesManager.settings.collectAsState()

  // Calendar State
  var visibleYearMonth by remember { mutableStateOf(YearMonth.now()) }
  var selectedDate by remember { mutableStateOf(LocalDate.now()) }

  // Dialog States
  var showMonthPicker by remember { mutableStateOf(false) }
  var showAddDialog by remember { mutableStateOf(false) }
  var addDialogInitialDateEpochDay by remember { mutableStateOf<Long?>(null) }
  var selectedStoryForDetails by remember { mutableStateOf<StoryModel?>(null) }
  var storyToEdit by remember { mutableStateOf<StoryModel?>(null) }
  var milestoneToEdit by remember { mutableStateOf<MilestoneModel?>(null) }

  // Events for selected date
  val eventsOnSelectedDate = remember(context, selectedDate, stories, allMilestones, allTasks) {
    CalendarDataHelper.getAllEventsForDate(context, selectedDate, stories, allMilestones, allTasks)
  }

  // Upcoming stories for preview (sorted by days until next occurrence)
  val upcomingStories = remember(stories) {
    stories.sortedBy { it.daysUntilNextOccurrence }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .testTag("calendar_screen"),
    contentAlignment = Alignment.TopCenter
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 600.dp)
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(bottom = 90.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Calendar Header & Month Navigation
      item {
        Spacer(modifier = Modifier.height(12.dp))
        CalendarHeader(
          yearMonth = visibleYearMonth,
          onPreviousMonth = {
            visibleYearMonth = visibleYearMonth.minusMonths(1)
          },
          onNextMonth = {
            visibleYearMonth = visibleYearMonth.plusMonths(1)
          },
          onJumpToToday = {
            val today = LocalDate.now()
            visibleYearMonth = YearMonth.from(today)
            selectedDate = today
          },
          onOpenMonthPicker = { showMonthPicker = true },
          onAddMoment = {
            addDialogInitialDateEpochDay = selectedDate.toEpochDay()
            showAddDialog = true
          },
          onToggleViewMode = onNavigateToTimeline
        )
      }

      // 2. Weekday Header (MON, TUE, WED, ...)
      item {
        CalendarWeekdayBar(
          firstDayOfWeekOption = userSettings.firstDayOfWeek
        )
      }

      // 3. Monthly Grid
      item {
        CalendarMonthGrid(
          yearMonth = visibleYearMonth,
          selectedDate = selectedDate,
          firstDayOfWeekOption = userSettings.firstDayOfWeek,
          stories = stories,
          milestones = allMilestones,
          tasks = allTasks,
          onSelectDate = { date ->
            selectedDate = date
            if (date.monthValue != visibleYearMonth.monthValue || date.year != visibleYearMonth.year) {
              visibleYearMonth = YearMonth.from(date)
            }
          }
        )
      }

      // 4. Selected Day Details Section
      item {
        Spacer(modifier = Modifier.height(8.dp))
        CalendarSelectedDaySection(
          selectedDate = selectedDate,
          events = eventsOnSelectedDate,
          onEventClick = { item ->
            when {
              item.originalStory != null -> {
                selectedStoryForDetails = item.originalStory
              }
              item.originalMilestone != null -> {
                milestoneToEdit = item.originalMilestone
              }
            }
          },
          onAddEventForDate = { date ->
            addDialogInitialDateEpochDay = date.toEpochDay()
            showAddDialog = true
          }
        )
      }

      // 5. Upcoming Moments Preview
      if (upcomingStories.isNotEmpty()) {
        item {
          Spacer(modifier = Modifier.height(12.dp))
          CalendarUpcomingPreview(
            upcomingStories = upcomingStories,
            onStoryClick = { selectedStoryForDetails = it },
            onViewAllTimeline = onNavigateToTimeline,
            onToggleFavorite = { storyRepository.toggleFavorite(it) }
          )
        }
      }
    }
  }

  // --- Dialogs ---

  // 1. Month / Year Picker Dialog
  if (showMonthPicker) {
    MonthYearPickerDialog(
      initialYearMonth = visibleYearMonth,
      onDismiss = { showMonthPicker = false },
      onSelectYearMonth = { newYearMonth ->
        visibleYearMonth = newYearMonth
        // keep day of month if valid for new month, else adjust
        val maxDay = newYearMonth.lengthOfMonth()
        val currentDay = selectedDate.dayOfMonth.coerceAtMost(maxDay)
        selectedDate = newYearMonth.atDay(currentDay)
        showMonthPicker = false
      }
    )
  }

  // 2. Add / Edit Event Dialog
  if (showAddDialog || storyToEdit != null) {
    AddEditEventDialog(
      eventToEdit = storyToEdit,
      initialDateEpochDay = addDialogInitialDateEpochDay,
      onDismiss = {
        showAddDialog = false
        storyToEdit = null
        addDialogInitialDateEpochDay = null
      },
      onSave = { updatedStory ->
        storyRepository.saveStory(updatedStory)
        showAddDialog = false
        storyToEdit = null
        addDialogInitialDateEpochDay = null
      }
    )
  }

  // 3. Event Details Dialog
  if (selectedStoryForDetails != null) {
    EventDetailsDialog(
      event = selectedStoryForDetails!!,
      onDismiss = { selectedStoryForDetails = null },
      onEdit = { story ->
        selectedStoryForDetails = null
        storyToEdit = story
      },
      onDelete = { storyId ->
        storyRepository.deleteStory(storyId)
        selectedStoryForDetails = null
      },
      onToggleFavorite = { storyId ->
        storyRepository.toggleFavorite(storyId)
      },
      onUpdateStory = { updatedStory ->
        storyRepository.saveStory(updatedStory)
      }
    )
  }

  // 4. Milestone Edit Dialog
  if (milestoneToEdit != null) {
    AddEditMilestoneDialog(
      milestoneToEdit = milestoneToEdit,
      availableStories = stories,
      onDismiss = { milestoneToEdit = null },
      onSaveMilestone = { updatedMilestone ->
        milestoneRepository.saveMilestone(updatedMilestone)
        milestoneToEdit = null
      }
    )
  }
}
