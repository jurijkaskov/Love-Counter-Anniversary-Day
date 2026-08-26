package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.MilestoneModel
import com.example.data.models.MilestoneTaskModel
import com.example.data.models.MilestoneWithTasks
import com.example.data.models.StoryModel
import com.example.ui.components.AddEditMilestoneDialog
import com.example.ui.components.AddEditTaskDialog
import com.example.ui.components.PrimaryButton
import com.example.ui.components.TaskItemRow
import com.example.ui.components.getIconForMilestoneCategory
import com.example.ui.share.ShareCardDialog
import com.example.ui.share.ShareCardPayloadFactory
import com.example.ui.theme.CherishGold
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun MilestoneDetailsScreen(
  milestoneWithTasks: MilestoneWithTasks,
  availableStories: List<StoryModel>,
  onBack: () -> Unit,
  onSaveMilestone: (MilestoneModel) -> Unit,
  onDeleteMilestone: (String) -> Unit,
  onSaveTask: (MilestoneTaskModel) -> Unit,
  onToggleTask: (String) -> Unit,
  onDeleteTask: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  val milestone = milestoneWithTasks.milestone
  val tasks = milestoneWithTasks.sortedTasks

  var showEditMilestoneDialog by remember { mutableStateOf(false) }
  var showShareCardDialog by remember { mutableStateOf(false) }
  var showAddTaskDialog by remember { mutableStateOf(false) }
  var taskToEdit by remember { mutableStateOf<MilestoneTaskModel?>(null) }

  val animatedProgress by animateFloatAsState(
    targetValue = milestoneWithTasks.progress,
    label = "progress_anim"
  )

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
      // 1. Navigation Top Bar
      item {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("milestone_details_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.onBackground
            )
          }

          Text(
            text = stringResource(R.string.milestone_details_header),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground
          )

          IconButton(
            onClick = { showShareCardDialog = true },
            modifier = Modifier.testTag("share_milestone_button")
          ) {
            Icon(
              imageVector = Icons.Outlined.Share,
              contentDescription = stringResource(R.string.share_card_btn_share_moment),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = { showEditMilestoneDialog = true },
            modifier = Modifier.testTag("edit_milestone_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Milestone",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 2. Hero Milestone Card with Category Icon & Description
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .testTag("milestone_hero_card"),
          color = MaterialTheme.colorScheme.surface,
          shape = RoundedCornerShape(24.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder.copy(alpha = 0.8f)),
          shadowElevation = 1.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(extColors.rosewoodContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = getIconForMilestoneCategory(milestone.category),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(22.dp)
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = milestone.title,
                  style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                  ),
                  color = MaterialTheme.colorScheme.onSurface
                )

                if (milestone.formattedTargetDate != null) {
                  Text(
                    text = "${milestone.formattedTargetDate} • ${milestone.timeframeLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CherishGold
                  )
                }
              }
            }

            if (milestone.description.isNotBlank()) {
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = milestone.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Linked Story Banner if available
            milestoneWithTasks.associatedStory?.let { story ->
              Spacer(modifier = Modifier.height(14.dp))
              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = stringResource(R.string.milestone_connected_to, story.title),
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }

      // 3. Overall Progress Card (18%, 2 of 11 tasks completed)
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_progress_card"),
          shape = RoundedCornerShape(20.dp),
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
                text = "${milestoneWithTasks.progressPercent}%",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
              progress = { animatedProgress },
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
                milestoneWithTasks.completedTasksCount,
                milestoneWithTasks.totalTasksCount
              ),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 4. Milestone Completed Celebration Banner (if 100% complete)
      if (milestoneWithTasks.isFullyCompleted) {
        item {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(20.dp))
              .testTag("milestone_celebration_banner"),
            color = extColors.goldContainer,
            border = androidx.compose.foundation.BorderStroke(1.dp, CherishGold.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(20.dp)
          ) {
            Row(
              modifier = Modifier.padding(18.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(CherishGold),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.surface,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = stringResource(R.string.milestone_celebration_headline),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                  ),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = stringResource(R.string.milestone_celebration_sub),
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // 5. Tasks Section Header with "+ Add Task"
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(R.string.milestone_tasks_header),
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )

          Text(
            text = "${milestoneWithTasks.completedTasksCount}/${milestoneWithTasks.totalTasksCount}",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Medium
            ),
            color = extColors.textMuted
          )
        }
      }

      // 6. Tasks List
      if (tasks.isEmpty()) {
        item {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = stringResource(R.string.milestone_no_tasks),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = stringResource(R.string.milestone_no_tasks_desc),
                style = MaterialTheme.typography.bodySmall,
                color = extColors.textMuted,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      } else {
        items(tasks, key = { it.id }) { task ->
          TaskItemRow(
            task = task,
            onToggle = { onToggleTask(task.id) },
            onClick = { taskToEdit = task }
          )
        }
      }

      // 7. Add Task Button
      item {
        Spacer(modifier = Modifier.height(4.dp))
        PrimaryButton(
          text = stringResource(R.string.milestone_add_task_btn),
          onClick = { showAddTaskDialog = true },
          modifier = Modifier.fillMaxWidth(),
          icon = Icons.Default.Add,
          testTag = "add_task_button"
        )
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }

  // Add Task Dialog
  if (showAddTaskDialog) {
    AddEditTaskDialog(
      milestoneId = milestone.id,
      taskToEdit = null,
      onDismiss = { showAddTaskDialog = false },
      onSaveTask = { newTask ->
        onSaveTask(newTask)
        showAddTaskDialog = false
      }
    )
  }

  // Edit Task Dialog
  if (taskToEdit != null) {
    AddEditTaskDialog(
      milestoneId = milestone.id,
      taskToEdit = taskToEdit,
      onDismiss = { taskToEdit = null },
      onSaveTask = { updatedTask ->
        onSaveTask(updatedTask)
        taskToEdit = null
      },
      onDeleteTask = { taskId ->
        onDeleteTask(taskId)
        taskToEdit = null
      }
    )
  }

  // Edit Milestone Dialog
  if (showEditMilestoneDialog) {
    AddEditMilestoneDialog(
      milestoneToEdit = milestone,
      availableStories = availableStories,
      onDismiss = { showEditMilestoneDialog = false },
      onSaveMilestone = { updatedMilestone ->
        onSaveMilestone(updatedMilestone)
        showEditMilestoneDialog = false
      },
      onDeleteMilestone = { milestoneId ->
        onDeleteMilestone(milestoneId)
        showEditMilestoneDialog = false
        onBack()
      }
    )
  }

  if (showShareCardDialog) {
    ShareCardDialog(
      payload = ShareCardPayloadFactory.fromMilestone(context, milestoneWithTasks),
      onDismiss = { showShareCardDialog = false }
    )
  }
}
