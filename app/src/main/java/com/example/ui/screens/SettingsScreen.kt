package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.JournalRepository
import com.example.data.MilestoneRepository
import com.example.data.PhotoRepository
import com.example.data.PreferencesManager
import com.example.data.StoryRepository
import com.example.data.models.AccentColorStyle
import com.example.data.models.DateFormatOption
import com.example.data.models.FirstDayOfWeekOption
import com.example.data.models.StoryModel
import com.example.data.models.ThemeMode
import com.example.ui.components.ReminderTimePickerDialog
import com.example.ui.components.NotificationPermissionDialog
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.core.app.NotificationManagerCompat
import android.os.Build
import com.example.ui.components.AccentColorSelectionRow
import com.example.ui.components.CherishCard
import com.example.ui.components.DateFormatDialog
import com.example.ui.components.ExportDataDialog
import com.example.ui.components.FirstDayOfWeekDialog
import com.example.ui.components.PreferenceRow
import com.example.ui.components.ResetConfirmationDialog
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SectionTitle
import com.example.ui.components.ThemePreviewCard
import com.example.ui.components.ThemeSelectionRow
import com.example.ui.components.ToggleRow
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  preferencesManager: PreferencesManager,
  storyRepository: StoryRepository,
  milestoneRepository: MilestoneRepository,
  journalRepository: JournalRepository? = null,
  photoRepository: PhotoRepository? = null,
  primaryStory: StoryModel? = null,
  onCreateStory: () -> Unit = {},
  onReplayOnboarding: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  val userSettings by preferencesManager.settings.collectAsState()
  val stories by storyRepository.stories.collectAsState()
  val milestones by milestoneRepository.milestones.collectAsState()
  val tasks by milestoneRepository.tasks.collectAsState()
  val resolvedJournalRepo = remember(journalRepository) {
    journalRepository ?: JournalRepository(context)
  }
  val resolvedPhotoRepo = remember(photoRepository) {
    photoRepository ?: PhotoRepository(context)
  }
  val journalEntries by resolvedJournalRepo.entries.collectAsState()

  var showDateFormatDialog by remember { mutableStateOf(false) }
  var showFirstDayOfWeekDialog by remember { mutableStateOf(false) }
  var showExportDialog by remember { mutableStateOf(false) }
  var showResetDialog by remember { mutableStateOf(false) }
  var showPrivacyInfoDialog by remember { mutableStateOf(false) }
  var showReminderTimePicker by remember { mutableStateOf(false) }
  var showPermissionDialog by remember { mutableStateOf(false) }

  val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
    contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      preferencesManager.setRemindersEnabled(true)
      storyRepository.syncAllAlarms()
      Toast.makeText(context, "Notifications enabled with love", Toast.LENGTH_SHORT).show()
    } else {
      Toast.makeText(context, "Notification permission was not granted", Toast.LENGTH_SHORT).show()
    }
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
        .padding(horizontal = 20.dp)
        .testTag("settings_lazy_column"),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Screen Title
      item {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = stringResource(R.string.settings_title),
          style = MaterialTheme.typography.displaySmall.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      // Profile Hero Card
      item {
        ProfileHeroCard(
          primaryStory = primaryStory,
          onManageStory = onCreateStory
        )
      }

      // 1. APPEARANCE SECTION
      item {
        SectionTitle(
          title = "Appearance",
          testTag = "settings_appearance_header"
        )
      }

      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Theme Mode Selector Cards
          Text(
            text = "App Theme",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          ThemeSelectionRow(
            selectedThemeMode = userSettings.themeMode,
            onSelectThemeMode = { preferencesManager.setThemeMode(it) }
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Accent Palette Selector
          Text(
            text = "Color Style & Accent",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          AccentColorSelectionRow(
            selectedAccent = userSettings.accentColorStyle,
            onSelectAccent = { preferencesManager.setAccentColorStyle(it) }
          )

          // Live Theme & Accent Preview
          ThemePreviewCard(
            themeMode = userSettings.themeMode,
            accentStyle = userSettings.accentColorStyle
          )
        }
      }

      // 2. PREFERENCES SECTION
      item {
        SectionTitle(
          title = stringResource(R.string.settings_section_preferences),
          testTag = "settings_preferences_header"
        )
      }

      item {
        CherishCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp)
        ) {
          // Date Format
          PreferenceRow(
            title = stringResource(R.string.settings_date_format_title),
            subtitle = "Display format for dates & countdowns",
            valueText = userSettings.dateFormat.example,
            icon = Icons.Default.DateRange,
            onClick = { showDateFormatDialog = true },
            testTag = "pref_date_format"
          )

          HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp),
            color = extColors.cardBorderSubtle
          )

          // First Day of Week
          PreferenceRow(
            title = "First Day of the Week",
            subtitle = "Starting day for calendars",
            valueText = userSettings.firstDayOfWeek.title,
            icon = Icons.Default.CalendarMonth,
            onClick = { showFirstDayOfWeekDialog = true },
            testTag = "pref_first_day_of_week"
          )

          HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp),
            color = extColors.cardBorderSubtle
          )

          // Haptic Feedback
          ToggleRow(
            title = stringResource(R.string.settings_haptics_title),
            subtitle = stringResource(R.string.settings_haptics_subtitle),
            icon = Icons.Default.Vibration,
            checked = userSettings.hapticFeedbackEnabled,
            onCheckedChange = { preferencesManager.setHapticFeedbackEnabled(it) },
            testTag = "toggle_haptics"
          )

          HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp),
            color = extColors.cardBorderSubtle
          )

          // Reduced Motion / Animations
          ToggleRow(
            title = "Reduce Animations",
            subtitle = "Minimize decorative motion and transitions",
            icon = Icons.Outlined.Animation,
            checked = userSettings.reducedAnimations,
            onCheckedChange = { preferencesManager.setReducedAnimations(it) },
            testTag = "toggle_reduced_animations"
          )
        }
      }

      // 3. SMART REMINDERS SECTION
      item {
        SectionTitle(
          title = stringResource(R.string.reminder_global_section_title),
          testTag = "settings_reminders_header"
        )
      }

      item {
        val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

        CherishCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp)
        ) {
          // Master Enable Reminders Toggle
          ToggleRow(
            title = stringResource(R.string.reminder_global_enable_title),
            subtitle = stringResource(R.string.reminder_global_enable_sub),
            icon = Icons.Outlined.NotificationsActive,
            checked = userSettings.remindersEnabled,
            onCheckedChange = { enabled ->
              if (enabled && !notificationsEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                  showPermissionDialog = true
                } else {
                  preferencesManager.setRemindersEnabled(true)
                  storyRepository.syncAllAlarms()
                }
              } else {
                preferencesManager.setRemindersEnabled(enabled)
                storyRepository.syncAllAlarms()
              }
            },
            testTag = "toggle_global_reminders"
          )

          if (userSettings.remindersEnabled) {
            HorizontalDivider(
              modifier = Modifier.padding(horizontal = 14.dp),
              color = extColors.cardBorderSubtle
            )

            // Default Notification Time Row
            PreferenceRow(
              title = stringResource(R.string.reminder_global_time_title),
              subtitle = "Default time for moment notifications",
              valueText = userSettings.formattedDefaultTime,
              icon = Icons.Outlined.Schedule,
              onClick = { showReminderTimePicker = true },
              testTag = "pref_default_reminder_time"
            )

            HorizontalDivider(
              modifier = Modifier.padding(horizontal = 14.dp),
              color = extColors.cardBorderSubtle
            )

            // Smart Suggestions Toggle
            ToggleRow(
              title = stringResource(R.string.reminder_global_suggestions_title),
              subtitle = stringResource(R.string.reminder_global_suggestions_sub),
              icon = Icons.Outlined.AutoAwesome,
              checked = userSettings.smartSuggestionsEnabled,
              onCheckedChange = { preferencesManager.setSmartSuggestionsEnabled(it) },
              testTag = "toggle_smart_suggestions"
            )
          }

          if (!notificationsEnabled && userSettings.remindersEnabled) {
            HorizontalDivider(
              modifier = Modifier.padding(horizontal = 14.dp),
              color = extColors.cardBorderSubtle
            )

            // Permission Warning Banner Row
            PreferenceRow(
              title = stringResource(R.string.reminder_permission_status_disabled),
              subtitle = "Tap to grant notification permission in system settings",
              valueText = "Enable",
              icon = Icons.Default.Info,
              onClick = {
                val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                  putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
              },
              testTag = "pref_permission_warning"
            )
          }
        }
      }

      // 4. HOME SCREEN WIDGETS SECTION
      item {
        SectionTitle(
          title = stringResource(R.string.widget_settings_card_title),
          testTag = "settings_widgets_header"
        )
      }

      item {
        CherishCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(extColors.goldContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Outlined.AutoAwesome,
                  contentDescription = null,
                  tint = extColors.goldAccent,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Glance at Your Moments",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                  ),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = stringResource(R.string.widget_settings_card_desc),
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorderSubtle),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(10.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "Countdown",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "3x2 / 4x2",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorderSubtle),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(10.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "Next Moment",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "3x2 / 4x2",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorderSubtle),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(10.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "Minimal Days",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "2x1 / 1x1",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            SecondaryButton(
              text = "Preview & Configure Widgets",
              icon = Icons.Outlined.AutoAwesome,
              onClick = {
                context.startActivity(Intent(context, com.example.ui.widget.WidgetConfigurationActivity::class.java))
              },
              modifier = Modifier.fillMaxWidth(),
              testTag = "btn_open_widget_config_preview"
            )
          }
        }
      }

      // 5. DATA & PRIVACY SECTION
      item {
        SectionTitle(
          title = stringResource(R.string.settings_section_data),
          testTag = "settings_data_header"
        )
      }

      item {
        // Reassuring local storage info banner
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(extColors.rosewoodContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Stored Locally & Privately",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Your memories, countdowns, and love notes are safely preserved only on your personal device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      item {
        CherishCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp)
        ) {
          // Export Data
          PreferenceRow(
            title = stringResource(R.string.settings_backup_title),
            subtitle = stringResource(R.string.settings_backup_subtitle),
            icon = Icons.Outlined.Backup,
            onClick = { showExportDialog = true },
            testTag = "pref_backup"
          )

          HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp),
            color = extColors.cardBorderSubtle
          )

          // Reset Application Data
          PreferenceRow(
            title = "Reset Application Data",
            subtitle = "Clear all stories, moments, and milestones",
            icon = Icons.Outlined.DeleteOutline,
            onClick = { showResetDialog = true },
            testTag = "pref_reset_data"
          )
        }
      }

      // 4. ABOUT SECTION
      item {
        SectionTitle(
          title = stringResource(R.string.settings_section_about),
          testTag = "settings_about_header"
        )
      }

      item {
        CherishCard(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp)
        ) {
          // Replay Onboarding
          PreferenceRow(
            title = stringResource(R.string.settings_replay_onboarding),
            subtitle = stringResource(R.string.settings_replay_onboarding_subtitle),
            icon = Icons.Outlined.AutoStories,
            onClick = onReplayOnboarding,
            testTag = "pref_replay_onboarding"
          )

          HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp),
            color = extColors.cardBorderSubtle
          )

          // Send Feedback
          /*PreferenceRow(
            title = stringResource(R.string.settings_send_feedback),
            subtitle = "Share thoughts, ideas, or suggestions",
            icon = Icons.Default.Feedback,
            onClick = {
              val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@lovecounter.app")
                putExtra(Intent.EXTRA_SUBJECT, "Love Counter App Feedback")
              }
              try {
                context.startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
              } catch (_: Exception) {
                Toast.makeText(context, "Feedback email: support@lovecounter.app", Toast.LENGTH_LONG).show()
              }
            },
            testTag = "pref_feedback"
          )

          HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp),
            color = extColors.cardBorderSubtle
          )*/

          // Privacy Policy
          PreferenceRow(
            title = stringResource(R.string.settings_privacy_policy),
            subtitle = "How your relationship data is protected",
            icon = Icons.Outlined.PrivacyTip,
            onClick = { showPrivacyInfoDialog = true },
            testTag = "pref_privacy"
          )

          HorizontalDivider(
            modifier = Modifier.padding(horizontal = 14.dp),
            color = extColors.cardBorderSubtle
          )

          // Version Info
          PreferenceRow(
            title = stringResource(R.string.settings_version_title),
            valueText = stringResource(R.string.settings_version_value),
            icon = Icons.Default.Info,
            showChevron = false,
            testTag = "pref_version"
          )
        }
      }

      // App Footer Tagline
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Love Counter: Anniversary Day",
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Celebrate every moment together",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  // DIALOGS
  if (showDateFormatDialog) {
    DateFormatDialog(
      selectedOption = userSettings.dateFormat,
      onSelectOption = { preferencesManager.setDateFormat(it) },
      onDismiss = { showDateFormatDialog = false }
    )
  }

  if (showFirstDayOfWeekDialog) {
    FirstDayOfWeekDialog(
      selectedOption = userSettings.firstDayOfWeek,
      onSelectOption = { preferencesManager.setFirstDayOfWeek(it) },
      onDismiss = { showFirstDayOfWeekDialog = false }
    )
  }

  if (showExportDialog) {
    ExportDataDialog(
      storiesCount = stories.size,
      milestonesCount = milestones.size,
      tasksCount = tasks.size,
      onGenerateExportJson = {
        preferencesManager.exportAllDataJson(storyRepository, milestoneRepository)
      },
      onDismiss = { showExportDialog = false }
    )
  }

  if (showResetDialog) {
    ResetConfirmationDialog(
      onConfirmReset = {
        preferencesManager.resetAllApplicationData(storyRepository, milestoneRepository)
        resolvedJournalRepo.clearAll()
        resolvedPhotoRepo.clearAll()
        Toast.makeText(context, "All data has been reset", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showResetDialog = false }
    )
  }

  if (showPrivacyInfoDialog) {
    BasicAlertDialog(
      onDismissRequest = { showPrivacyInfoDialog = false },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .testTag("privacy_info_dialog")
    ) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder),
        shadowElevation = 6.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(extColors.rosewoodContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }

            Column {
              Text(
                text = "Privacy & Data Protection",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Built with your privacy at heart",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "• All your data is stored locally on your device in secure app storage.\n\n• No accounts, passwords, or personal credentials are required to celebrate your moments.\n\n• You can export your data at any time in portable JSON format, or delete it completely whenever you choose.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(24.dp))

          SecondaryButton(
            text = stringResource(R.string.event_close),
            onClick = { showPrivacyInfoDialog = false },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }

  if (showReminderTimePicker) {
    ReminderTimePickerDialog(
      initialHour = userSettings.defaultReminderHour,
      initialMinute = userSettings.defaultReminderMinute,
      onDismiss = { showReminderTimePicker = false },
      onConfirm = { h, m ->
        preferencesManager.setDefaultReminderTime(h, m)
        showReminderTimePicker = false
        Toast.makeText(context, "Default reminder time updated", Toast.LENGTH_SHORT).show()
      }
    )
  }

  if (showPermissionDialog) {
    NotificationPermissionDialog(
      onDismiss = { showPermissionDialog = false },
      onRequestPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
      }
    )
  }
}

@Composable
private fun ProfileHeroCard(
  primaryStory: StoryModel?,
  onManageStory: () -> Unit
) {
  val extColors = LocalCherishExtendedColors.current

  CherishCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("profile_hero_card"),
    containerColor = MaterialTheme.colorScheme.surface,
    borderColor = extColors.cardBorder,
    contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Initials Double Circle Avatar
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(extColors.rosewoodContainer)
          .border(2.dp, extColors.goldAccent.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = primaryStory?.displayInitials ?: "♥",
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = primaryStory?.displayTitle ?: stringResource(R.string.settings_profile_names),
          style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
          ),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = if (primaryStory != null) "Together since ${primaryStory.formattedDate}" else stringResource(R.string.settings_profile_subtitle),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    SecondaryButton(
      text = if (primaryStory != null) "Customize Story & Moments" else "Create Your Story",
      onClick = onManageStory,
      icon = if (primaryStory != null) Icons.Outlined.Edit else Icons.Default.Add,
      modifier = Modifier.fillMaxWidth(),
      testTag = "btn_manage_profile"
    )
  }
}
