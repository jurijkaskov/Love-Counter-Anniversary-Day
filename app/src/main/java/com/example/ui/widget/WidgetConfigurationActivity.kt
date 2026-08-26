package com.example.ui.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PreferencesManager
import com.example.data.StoryRepository
import com.example.data.models.StoryModel
import com.example.ui.theme.CherishCardBorder
import com.example.ui.theme.CherishDarkBg
import com.example.ui.theme.CherishDarkCardBorder
import com.example.ui.theme.CherishDarkSurface
import com.example.ui.theme.CherishDarkTextMuted
import com.example.ui.theme.CherishDarkTextPrimary
import com.example.ui.theme.CherishDarkTextSecondary
import com.example.ui.theme.CherishGold
import com.example.ui.theme.CherishGoldContainer
import com.example.ui.theme.CherishIvoryBg
import com.example.ui.theme.CherishRosewood
import com.example.ui.theme.CherishRosewoodContainer
import com.example.ui.theme.CherishSurface
import com.example.ui.theme.CherishTextMuted
import com.example.ui.theme.CherishTextPrimary
import com.example.ui.theme.CherishTextSecondary
import com.example.ui.theme.CherishTheme

class WidgetConfigurationActivity : ComponentActivity() {

  private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Default result is canceled in case user presses back without saving
    setResult(Activity.RESULT_CANCELED)

    val extras = intent?.extras
    if (extras != null) {
      appWidgetId = extras.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
      )
    }

    // Resolve which widget provider this is for
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val appWidgetInfo = if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
      appWidgetManager?.getAppWidgetInfo(appWidgetId)
    } else null

    val inferredType = when (appWidgetInfo?.provider?.className) {
      NextEventWidgetProvider::class.java.name -> WidgetType.NEXT_EVENT
      MinimalDaysWidgetProvider::class.java.name -> WidgetType.MINIMAL_DAYS
      else -> WidgetType.MAIN_COUNTDOWN
    }

    setContent {
      val preferencesManager = remember { PreferencesManager(this) }
      val userSettings by preferencesManager.settings.collectAsState()

      CherishTheme(
        themeMode = userSettings.themeMode,
        accentStyle = userSettings.accentColorStyle
      ) {
        WidgetConfigurationScreen(
          appWidgetId = appWidgetId,
          initialWidgetType = inferredType,
          onFinish = {
            val resultValue = Intent().apply {
              putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(Activity.RESULT_OK, resultValue)
            finish()
          },
          onCancel = {
            finish()
          }
        )
      }
    }
  }
}

@Composable
fun WidgetConfigurationScreen(
  appWidgetId: Int,
  initialWidgetType: WidgetType,
  onFinish: () -> Unit,
  onCancel: () -> Unit
) {
  val context = LocalContext.current
  val storyRepository = remember { StoryRepository(context) }
  val widgetPreferences = remember { WidgetPreferences(context) }
  val existingConfig = remember { widgetPreferences.getConfig(appWidgetId, initialWidgetType) }

  val stories by storyRepository.stories.collectAsState()
  val primaryStory by storyRepository.primaryStory.collectAsState()

  var selectedStoryId by remember { mutableStateOf(existingConfig.targetStoryId ?: primaryStory?.id) }
  var autoNextEvent by remember { mutableStateOf(existingConfig.autoNextEvent) }
  var themePreference by remember { mutableStateOf(existingConfig.themePreference) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp,
            bottom = 12.dp,
            start = 12.dp,
            end = 16.dp
          )
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onCancel,
            modifier = Modifier.testTag("btn_widget_config_back")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          Column {
            Text(
              text = stringResource(R.string.widget_config_title),
              style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = stringResource(R.string.widget_config_subtitle),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    },
    bottomBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(
              start = 20.dp,
              end = 20.dp,
              top = 12.dp,
              bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp
            )
        ) {
          Button(
            onClick = {
              val newConfig = WidgetConfig(
                appWidgetId = appWidgetId,
                widgetType = initialWidgetType,
                targetStoryId = if (autoNextEvent && initialWidgetType == WidgetType.NEXT_EVENT) null else selectedStoryId,
                autoNextEvent = autoNextEvent,
                themePreference = themePreference
              )
              widgetPreferences.saveConfig(newConfig)
              WidgetUpdateHelper.updateAllWidgets(context)
              onFinish()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("btn_save_widget_config"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            )
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                stringResource(R.string.widget_config_apply_btn)
              } else {
                stringResource(R.string.widget_config_save_btn)
              },
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
              )
            )
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = stringResource(R.string.widget_config_preview_label).uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Live Preview Box
        WidgetLivePreview(
          widgetType = initialWidgetType,
          selectedStory = stories.find { it.id == selectedStoryId } ?: primaryStory,
          themePreference = themePreference,
          autoNextEvent = autoNextEvent,
          stories = stories
        )
      }

      // Appearance Section
      item {
        Text(
          text = stringResource(R.string.widget_config_appearance).uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
          )
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            ThemeOptionRow(
              title = stringResource(R.string.widget_config_theme_light),
              icon = Icons.Default.LightMode,
              selected = themePreference == WidgetThemePreference.LIGHT,
              onClick = { themePreference = WidgetThemePreference.LIGHT },
              testTag = "theme_option_light"
            )

            ThemeOptionRow(
              title = stringResource(R.string.widget_config_theme_dark),
              icon = Icons.Default.DarkMode,
              selected = themePreference == WidgetThemePreference.DARK,
              onClick = { themePreference = WidgetThemePreference.DARK },
              testTag = "theme_option_dark"
            )

            ThemeOptionRow(
              title = stringResource(R.string.widget_config_theme_system),
              icon = Icons.Default.SettingsBrightness,
              selected = themePreference == WidgetThemePreference.SYSTEM,
              onClick = { themePreference = WidgetThemePreference.SYSTEM },
              testTag = "theme_option_system"
            )
          }
        }
      }

      // Story / Event Selection Section
      item {
        Text(
          text = stringResource(R.string.widget_config_select_story).uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))
      }

      if (initialWidgetType == WidgetType.NEXT_EVENT) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { autoNextEvent = true }
              .testTag("option_auto_next_event"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (autoNextEvent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (autoNextEvent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = stringResource(R.string.widget_config_auto_next),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                  ),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Automatically highlights nearest upcoming moment or celebration.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              RadioButton(
                selected = autoNextEvent,
                onClick = { autoNextEvent = true },
                colors = RadioButtonDefaults.colors(
                  selectedColor = MaterialTheme.colorScheme.primary
                )
              )
            }
          }
        }
      }

      items(stories, key = { it.id }) { story ->
        val isSelected = (!autoNextEvent || initialWidgetType != WidgetType.NEXT_EVENT) && selectedStoryId == story.id

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              selectedStoryId = story.id
              autoNextEvent = false
            }
            .testTag("story_option_${story.id}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = null,
              tint = if (story.isPrimary) MaterialTheme.colorScheme.primary else CherishGold,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (story.isPrimary) {
                  "${story.displayTitle} ★"
                } else {
                  story.displayTitle
                },
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${story.formattedDate} • ${if (story.isPastDate) "${story.totalDays} days together" else "in ${story.totalDays} days"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            RadioButton(
              selected = isSelected,
              onClick = {
                selectedStoryId = story.id
                autoNextEvent = false
              },
              colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
              )
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun ThemeOptionRow(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  selected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 10.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
      ),
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.weight(1f)
    )
    RadioButton(
      selected = selected,
      onClick = onClick,
      colors = RadioButtonDefaults.colors(
        selectedColor = MaterialTheme.colorScheme.primary
      )
    )
  }
}

@Composable
private fun WidgetLivePreview(
  widgetType: WidgetType,
  selectedStory: StoryModel?,
  themePreference: WidgetThemePreference,
  autoNextEvent: Boolean,
  stories: List<StoryModel>
) {
  val isDark = when (themePreference) {
    WidgetThemePreference.LIGHT -> false
    WidgetThemePreference.DARK -> true
    WidgetThemePreference.SYSTEM -> false
  }

  val bgColor = if (isDark) CherishDarkSurface else CherishIvoryBg
  val innerBgColor = if (isDark) CherishDarkBg else CherishSurface
  val borderColor = if (isDark) CherishDarkCardBorder else CherishCardBorder
  val textColor = if (isDark) CherishDarkTextPrimary else CherishTextPrimary
  val textMuted = if (isDark) CherishDarkTextMuted else CherishTextMuted
  val textSecondary = if (isDark) CherishDarkTextSecondary else CherishTextSecondary
  val rosewoodColor = if (isDark) CherishRosewoodContainer else CherishRosewood
  val goldColor = CherishGold

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(bgColor)
      .border(1.dp, borderColor, RoundedCornerShape(24.dp))
      .padding(18.dp)
      .testTag("widget_live_preview_card"),
    contentAlignment = Alignment.Center
  ) {
    when (widgetType) {
      WidgetType.MAIN_COUNTDOWN -> {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = null,
              tint = rosewoodColor,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = selectedStory?.displayTitle ?: "Your Love Story",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              ),
              color = textColor,
              modifier = Modifier.weight(1f)
            )
            Text(
              text = if (selectedStory?.isPastDate == true) "Since ${selectedStory.formattedDate}" else selectedStory?.formattedDate ?: "",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = textMuted
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(innerBgColor)
              .border(1.dp, borderColor, RoundedCornerShape(16.dp))
              .padding(12.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = String.format("%,d", selectedStory?.totalDays ?: 1253),
                style = MaterialTheme.typography.headlineLarge.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 38.sp
                ),
                color = rosewoodColor
              )
              Text(
                text = if (selectedStory?.isPastDate == true) "DAYS TOGETHER" else "DAYS REMAINING",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = textSecondary
              )
              Text(
                text = selectedStory?.formattedPeriodBreakdown ?: "3 years · 5 months · 18 days",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = textMuted,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = goldColor,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (selectedStory?.isPastDate == true) {
                "Next: ${selectedStory.nextAnniversaryTitle} in ${selectedStory.daysUntilNextAnniversary} days"
              } else {
                "In ${selectedStory?.totalDays ?: 0} days"
              },
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              ),
              color = goldColor
            )
          }
        }
      }

      WidgetType.NEXT_EVENT -> {
        val targetEvent = if (autoNextEvent) {
          stories.filter { !it.isPastDate }.minByOrNull { it.totalDays }
            ?: stories.minByOrNull { it.daysUntilNextOccurrence }
            ?: selectedStory
        } else {
          selectedStory
        }

        Column(
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = rosewoodColor,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "NEXT MOMENT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = rosewoodColor,
              modifier = Modifier.weight(1f)
            )
            Text(
              text = targetEvent?.formattedNextOccurrenceDate ?: "August 12, 2026",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = textMuted
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(innerBgColor)
              .border(1.dp, borderColor, RoundedCornerShape(16.dp))
              .padding(14.dp)
          ) {
            Column {
              Text(
                text = targetEvent?.displayTitle ?: "Wedding Anniversary",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 17.sp
                ),
                color = textColor
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(CherishGoldContainer)
                    .border(1.dp, CherishGold, RoundedCornerShape(100.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = targetEvent?.countdownBadgeText?.uppercase() ?: "IN 156 DAYS",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 0.5.sp
                    ),
                    color = CherishGold
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = targetEvent?.note?.ifBlank { "Celebrating our vows" } ?: "Celebrating our vows",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = textSecondary,
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      WidgetType.MINIMAL_DAYS -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.padding(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = rosewoodColor,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = String.format("%,d", selectedStory?.totalDays ?: 1253),
            style = MaterialTheme.typography.headlineLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold,
              fontSize = 36.sp
            ),
            color = rosewoodColor
          )
          Text(
            text = if (selectedStory?.isPastDate == true) "days together" else "days until anniversary",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp
            ),
            color = textSecondary
          )
        }
      }
    }
  }
}
