package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MilestoneRepository
import com.example.data.StoryRepository
import com.example.data.models.MilestoneCategory
import com.example.data.models.MilestoneModel
import com.example.data.models.MilestoneTaskModel
import com.example.data.models.MilestoneWithTasks
import com.example.ui.components.AddEditMilestoneDialog
import com.example.ui.components.AddEditTaskDialog
import com.example.ui.components.CherishCard
import com.example.ui.components.CherishIconButton
import com.example.ui.components.PremiumEmptyState
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.TaskItemRow
import com.example.ui.components.getIconForMilestoneCategory
import com.example.ui.theme.CherishGold
import com.example.ui.theme.LocalCherishExtendedColors

enum class MilestoneTabFilter(val labelResId: Int) {
  ALL(R.string.milestones_tab_all),
  WEDDING(R.string.milestones_tab_wedding),
  ANNIVERSARY(R.string.milestones_tab_anniversary),
  TRIPS(R.string.milestones_tab_trip),
  CUSTOM(R.string.milestones_tab_custom)
}

@Composable
fun MilestonesScreen(
  milestoneRepository: MilestoneRepository,
  storyRepository: StoryRepository,
  modifier: Modifier = Modifier,
  onOpenDetails: ((String) -> Unit)? = null
) {
  val extColors = LocalCherishExtendedColors.current
  val milestones by milestoneRepository.milestones.collectAsState()
  val tasks by milestoneRepository.tasks.collectAsState()
  val stories by storyRepository.stories.collectAsState()

  var selectedFilter by remember { mutableStateOf(MilestoneTabFilter.ALL) }
  var showAddMilestoneDialog by remember { mutableStateOf(false) }
  var selectedMilestoneForDetails by remember { mutableStateOf<MilestoneWithTasks?>(null) }
  var taskToEdit by remember { mutableStateOf<MilestoneTaskModel?>(null) }

  // Combine milestones with tasks
  val allMilestonesWithTasks = remember(milestones, tasks, stories) {
    milestones.map { milestone ->
      val associatedStory = milestone.associatedStoryId?.let { storyId ->
        stories.find { it.id == storyId }
      }
      val mTasks = tasks.filter { it.milestoneId == milestone.id }
      MilestoneWithTasks(
        milestone = milestone,
        tasks = mTasks,
        associatedStory = associatedStory
      )
    }
  }

  // Filtered by selected tab
  val filteredMilestones = remember(selectedFilter, allMilestonesWithTasks) {
    when (selectedFilter) {
      MilestoneTabFilter.ALL -> allMilestonesWithTasks
      MilestoneTabFilter.WEDDING -> allMilestonesWithTasks.filter { it.milestone.category == MilestoneCategory.WEDDING }
      MilestoneTabFilter.ANNIVERSARY -> allMilestonesWithTasks.filter { it.milestone.category == MilestoneCategory.ANNIVERSARY }
      MilestoneTabFilter.TRIPS -> allMilestonesWithTasks.filter { it.milestone.category == MilestoneCategory.TRIP }
      MilestoneTabFilter.CUSTOM -> allMilestonesWithTasks.filter { it.milestone.category == MilestoneCategory.CUSTOM }
    }
  }

  // Active vs Completed
  val activeMilestones = remember(filteredMilestones) {
    filteredMilestones.filter { !it.isFullyCompleted }
  }

  val completedMilestones = remember(filteredMilestones) {
    filteredMilestones.filter { it.isFullyCompleted }
  }

  // Total Progress Stats across active + completed
  val totalTasks = remember(filteredMilestones) {
    filteredMilestones.sumOf { it.totalTasksCount }
  }
  val totalCompletedTasks = remember(filteredMilestones) {
    filteredMilestones.sumOf { it.completedTasksCount }
  }
  val overallProgressRatio = remember(totalTasks, totalCompletedTasks) {
    if (totalTasks == 0) 0f else (totalCompletedTasks.toFloat() / totalTasks.toFloat()).coerceIn(0f, 1f)
  }
  val overallProgressPercent = remember(overallProgressRatio) {
    (overallProgressRatio * 100).toInt()
  }

  val animatedOverallProgress by animateFloatAsState(
    targetValue = overallProgressRatio,
    label = "overall_progress"
  )

  // If a milestone is selected for details, display the full details screen
  if (selectedMilestoneForDetails != null) {
    val currentSelected = allMilestonesWithTasks.find { it.milestone.id == selectedMilestoneForDetails?.milestone?.id }
      ?: selectedMilestoneForDetails!!

    MilestoneDetailsScreen(
      milestoneWithTasks = currentSelected,
      availableStories = stories,
      onBack = { selectedMilestoneForDetails = null },
      onSaveMilestone = { milestoneRepository.saveMilestone(it) },
      onDeleteMilestone = {
        milestoneRepository.deleteMilestone(it)
        selectedMilestoneForDetails = null
      },
      onSaveTask = { milestoneRepository.saveTask(it) },
      onToggleTask = { milestoneRepository.toggleTask(it) },
      onDeleteTask = { milestoneRepository.deleteTask(it) }
    )
    return
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
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("milestones_header"),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = stringResource(R.string.milestones_main_title),
              style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = stringResource(R.string.milestones_main_subtitle),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          if (filteredMilestones.isNotEmpty()) {
            CherishIconButton(
              icon = Icons.Default.Add,
              contentDescription = "Create Milestone",
              onClick = { showAddMilestoneDialog = true },
              testTag = "add_milestone_header_button"
            )
          }
        }
      }

      // 2. Journey Category Filter Tabs
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .testTag("milestone_filter_tabs"),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MilestoneTabFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { selectedFilter = filter }
                .testTag("filter_tab_${filter.name.lowercase()}"),
              color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
              shape = RoundedCornerShape(20.dp),
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder)
            ) {
              Text(
                text = stringResource(filter.labelResId),
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                  fontSize = 13.sp
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              )
            }
          }
        }
      }

      // Empty state
      if (filteredMilestones.isEmpty()) {
        item {
          Spacer(modifier = Modifier.height(20.dp))
          PremiumEmptyState(
            title = stringResource(R.string.milestones_empty_headline),
            description = stringResource(R.string.milestones_empty_subheadline),
            icon = Icons.Outlined.Celebration,
            actionButtonText = stringResource(R.string.milestones_empty_cta),
            onActionClick = { showAddMilestoneDialog = true },
            modifier = Modifier.fillMaxWidth(),
            testTag = "milestones_empty_state"
          )
        }
      } else {
        // 3. Overall Progress Card (matches reference mockup: Overall Progress 18%, 2 of 11 tasks completed)
        if (totalTasks > 0) {
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("overall_progress_card"),
              shape = RoundedCornerShape(22.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder.copy(alpha = 0.8f))
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(20.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = stringResource(R.string.milestones_overall_progress),
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "$overallProgressPercent%",
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontFamily = FontFamily.Serif,
                      fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                  progress = { animatedOverallProgress },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                  color = MaterialTheme.colorScheme.primary,
                  trackColor = extColors.cardBorder.copy(alpha = 0.4f),
                  strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                  text = stringResource(
                    R.string.milestones_in_progress_summary,
                    totalCompletedTasks,
                    totalTasks
                  ),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        // 4. Active Milestones & Preparations (Grouped by milestone / timeframe)
        activeMilestones.forEach { milestoneWithTasks ->
          val milestone = milestoneWithTasks.milestone

          // Milestone Header / Timeframe Group
          item(key = "header_${milestone.id}") {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedMilestoneForDetails = milestoneWithTasks }
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(extColors.rosewoodContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = getIconForMilestoneCategory(milestone.category),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                }

                Column {
                  Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontFamily = FontFamily.Serif,
                      fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  if (milestone.formattedTargetDate != null) {
                    Text(
                      text = "${milestone.timeframeLabel.uppercase()} • ${milestone.formattedTargetDate}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.SemiBold
                      ),
                      color = CherishGold
                    )
                  }
                }
              }

              // Progress badge
              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
              ) {
                Text(
                  text = "${milestoneWithTasks.completedTasksCount}/${milestoneWithTasks.totalTasksCount}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                  ),
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          // Milestone Tasks
          items(milestoneWithTasks.sortedTasks, key = { it.id }) { task ->
            TaskItemRow(
              task = task,
              onToggle = { milestoneRepository.toggleTask(task.id) },
              onClick = { selectedMilestoneForDetails = milestoneWithTasks }
            )
          }
        }

        // 5. Completed Milestones Section
        if (completedMilestones.isNotEmpty()) {
          item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = CherishGold,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = stringResource(R.string.milestones_completed_section),
                style = MaterialTheme.typography.titleSmall.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          items(completedMilestones, key = { "completed_${it.milestone.id}" }) { item ->
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { selectedMilestoneForDetails = item }
                .testTag("completed_milestone_${item.milestone.id}"),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
              shape = RoundedCornerShape(18.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder.copy(alpha = 0.5f))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(CircleShape)
                      .background(CherishGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = CherishGold,
                      modifier = Modifier.size(20.dp)
                    )
                  }
                  Column {
                    Text(
                      text = item.milestone.title,
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                      ),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = stringResource(R.string.milestones_all_preparations_completed, item.totalTasksCount),
                      style = MaterialTheme.typography.bodySmall,
                      color = extColors.textMuted
                    )
                  }
                }

                Text(
                  text = stringResource(R.string.btn_view),
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }

        // 6. Bottom Add Milestone Action Button
        item {
          Spacer(modifier = Modifier.height(8.dp))
          PrimaryButton(
            text = stringResource(R.string.milestones_add_button),
            onClick = { showAddMilestoneDialog = true },
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Add,
            testTag = "add_milestone_bottom_button"
          )
          Spacer(modifier = Modifier.height(28.dp))
        }
      }
    }
  }

  // Create Milestone Dialog
  if (showAddMilestoneDialog) {
    AddEditMilestoneDialog(
      milestoneToEdit = null,
      availableStories = stories,
      onDismiss = { showAddMilestoneDialog = false },
      onSaveMilestone = { newMilestone ->
        milestoneRepository.saveMilestone(newMilestone)
        showAddMilestoneDialog = false
      }
    )
  }

  // Edit Milestone Dialog
  if (selectedMilestoneForDetails != null) {
    AddEditMilestoneDialog(
      milestoneToEdit = selectedMilestoneForDetails?.milestone,
      availableStories = stories,
      onDismiss = { selectedMilestoneForDetails = null },
      onSaveMilestone = { updatedMilestone ->
        milestoneRepository.saveMilestone(updatedMilestone)
        selectedMilestoneForDetails = null
      },
      onDeleteMilestone = { milestoneId ->
        milestoneRepository.deleteMilestone(milestoneId)
        selectedMilestoneForDetails = null
      }
    )
  }
}
