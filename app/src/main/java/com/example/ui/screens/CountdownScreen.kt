package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Share
import com.example.R
import com.example.ui.share.ShareCardDialog
import com.example.ui.share.ShareCardPayloadFactory
import com.example.data.models.StoryModel
import com.example.ui.components.CherishCard
import com.example.ui.components.CherishIconButton
import com.example.ui.components.MilestoneItemRow
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SectionTitle
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.LocalDate

private data class CountdownMilestone(
  val id: String,
  val title: String,
  val date: String,
  val countdownDays: String,
  val icon: ImageVector,
  val isFavorite: Boolean = false
)

@Composable
fun CountdownScreen(
  primaryStory: StoryModel? = null,
  onCreateStoryClick: () -> Unit = {},
  onNavigateToMoments: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  var showShareCardDialog by remember { mutableStateOf(false) }

  var sampleMilestones by remember {
    mutableStateOf(
      listOf(
        CountdownMilestone(
          id = "1",
          title = "Wedding Anniversary",
          date = "August 12, 2027",
          countdownDays = "In 156 days",
          icon = Icons.Outlined.Celebration,
          isFavorite = true
        ),
        CountdownMilestone(
          id = "2",
          title = "First Date Anniversary",
          date = "February 14, 2027",
          countdownDays = "In 342 days",
          icon = Icons.Default.Favorite,
          isFavorite = true
        ),
        CountdownMilestone(
          id = "3",
          title = "Her Birthday",
          date = "November 3, 2026",
          countdownDays = "In 56 days",
          icon = Icons.Default.Cake,
          isFavorite = false
        ),
        CountdownMilestone(
          id = "4",
          title = "Trip to Amalfi Coast",
          date = "December 20, 2026",
          countdownDays = "In 103 days",
          icon = Icons.Default.Flight,
          isFavorite = false
        )
      )
    )
  }

  // Animation trigger for smooth dashboard reveal
  var isVisible by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    isVisible = true
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
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(10.dp))
        HeaderBar(
          primaryStory = primaryStory,
          onAddClick = onCreateStoryClick,
          onShareClick = { showShareCardDialog = true }
        )
      }

      item {
        HeroCountdownCard(
          primaryStory = primaryStory,
          onCreateStoryClick = onCreateStoryClick,
          onShareClick = { showShareCardDialog = true }
        )
      }

      if (primaryStory != null) {
        // Detailed Time Statistics Card (Days, Weeks, Months, Hours)
        item {
          TimeTogetherStatsCard(primaryStory = primaryStory)
        }

        // Dynamic Next Important Moment / Anniversary
        item {
          NextSpecialDateCard(
            primaryStory = primaryStory,
            onAddMoment = onCreateStoryClick
          )
        }
      }

      // Meaningful Daily Message / Romantic Affirmation
      item {
        RomanticQuoteCard(
          quoteText = primaryStory?.note?.ifBlank { null }
        )
      }

      // Upcoming Milestones Section
      item {
        Spacer(modifier = Modifier.height(2.dp))
        SectionTitle(
          title = stringResource(R.string.countdown_upcoming_milestones_title),
          subtitle = stringResource(R.string.countdown_upcoming_subtitle),
          actionText = stringResource(R.string.btn_view_all),
          onActionClick = onNavigateToMoments,
          testTag = "countdown_milestones_header"
        )
      }

      items(sampleMilestones, key = { it.id }) { milestone ->
        MilestoneItemRow(
          title = milestone.title,
          dateFormatted = milestone.date,
          badgeText = milestone.countdownDays,
          icon = milestone.icon,
          iconBackground = if (milestone.isFavorite) extColors.rosewoodContainer else extColors.goldContainer,
          iconTint = if (milestone.isFavorite) MaterialTheme.colorScheme.primary else extColors.goldAccent,
          isFavorite = milestone.isFavorite,
          onFavoriteClick = {
            sampleMilestones = sampleMilestones.map {
              if (it.id == milestone.id) it.copy(isFavorite = !it.isFavorite) else it
            }
          },
          testTag = "milestone_item_${milestone.id}"
        )
      }

      // Quick-Access Action Shortcuts
      item {
        QuickActionsBar(
          onViewMoments = onNavigateToMoments,
          onAddMoment = onCreateStoryClick
        )
      }

      item {
        Spacer(modifier = Modifier.height(84.dp))
      }
    }

    if (showShareCardDialog && primaryStory != null) {
      ShareCardDialog(
        payload = ShareCardPayloadFactory.fromStory(primaryStory),
        onDismiss = { showShareCardDialog = false }
      )
    }
  }
}

/**
 * 1. Personal Header Bar
 */
@Composable
private fun HeaderBar(
  primaryStory: StoryModel?,
  onAddClick: () -> Unit,
  onShareClick: () -> Unit = {}
) {
  val extColors = LocalCherishExtendedColors.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("countdown_header_bar"),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(extColors.rosewoodContainer)
          .border(1.5.dp, extColors.goldAccent.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = primaryStory?.displayInitials ?: "♥",
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          ),
          color = MaterialTheme.colorScheme.primary
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = primaryStory?.displayTitle ?: stringResource(R.string.app_name),
          style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          ),
          color = MaterialTheme.colorScheme.onBackground,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = if (primaryStory != null) {
            if (primaryStory.isPastDate) "Since ${primaryStory.formattedDate}" else "Coming ${primaryStory.formattedDate}"
          } else {
            stringResource(R.string.app_tagline)
          },
          style = MaterialTheme.typography.bodySmall,
          color = extColors.textMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      if (primaryStory != null) {
        CherishIconButton(
          icon = Icons.Outlined.Share,
          contentDescription = "Share memory card",
          onClick = onShareClick,
          testTag = "countdown_header_share_btn"
        )
        Spacer(modifier = Modifier.width(6.dp))
      }

      // Add / Create Story Button
      CherishIconButton(
        icon = Icons.Filled.Add,
        contentDescription = "Add milestone or story",
        onClick = onAddClick,
        testTag = "countdown_add_moment_btn"
      )

      Spacer(modifier = Modifier.width(6.dp))

      // Couple initials / status badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(extColors.goldContainer)
          .border(1.dp, extColors.goldAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
          .clickable { onAddClick() }
          .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = primaryStory?.displayInitials ?: "♥",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = extColors.goldAccent
        )
      }
    }
  }
}

/**
 * 2. Main Countdown Hero Card
 */
@Composable
private fun HeroCountdownCard(
  primaryStory: StoryModel?,
  onCreateStoryClick: () -> Unit,
  onShareClick: () -> Unit = {}
) {
  val extColors = LocalCherishExtendedColors.current

  if (primaryStory == null) {
    // Empty state card
    CherishCard(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(12.dp, shape = RoundedCornerShape(28.dp), ambientColor = extColors.goldAccent.copy(alpha = 0.2f))
        .testTag("hero_countdown_card_empty"),
      shape = RoundedCornerShape(28.dp),
      containerColor = MaterialTheme.colorScheme.surface,
      borderColor = extColors.cardBorder,
      contentPadding = androidx.compose.foundation.layout.PaddingValues(26.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(extColors.rosewoodContainer)
            .border(1.5.dp, extColors.goldAccent.copy(alpha = 0.4f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(34.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Begin Your Story",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Set your relationship date or first anniversary to celebrate every single day together.",
          style = MaterialTheme.typography.bodyMedium,
          color = extColors.textMuted,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryButton(
          text = "Create First Story",
          onClick = onCreateStoryClick,
          icon = Icons.Filled.Add,
          testTag = "btn_create_first_story_hero"
        )
      }
    }
    return
  }

  val isPast = primaryStory.isPastDate
  val targetDays = primaryStory.totalDays.toInt()

  // Animated smooth number counter
  val animatedDays by animateIntAsState(
    targetValue = targetDays,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "hero_days_animation"
  )
  val daysDisplay = String.format("%,d", animatedDays)

  CherishCard(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(16.dp, shape = RoundedCornerShape(28.dp), ambientColor = extColors.goldAccent.copy(alpha = 0.25f))
      .testTag("hero_countdown_card"),
    shape = RoundedCornerShape(28.dp),
    containerColor = Color.Transparent,
    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              extColors.rosewoodContainer.copy(alpha = 0.95f),
              extColors.goldContainer.copy(alpha = 0.7f),
              MaterialTheme.colorScheme.surface
            )
          )
        )
        .padding(vertical = 32.dp, horizontal = 24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Tag badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .border(1.dp, extColors.goldAccent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isPast) {
                stringResource(R.string.countdown_together_for).uppercase()
              } else {
                "COUNTDOWN TO CELEBRATION"
              },
              style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.6.sp,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              ),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Big Display Number
        Row(
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = daysDisplay,
            style = MaterialTheme.typography.displayLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Normal,
              fontSize = 62.sp,
              lineHeight = 66.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = if (isPast) "days ♥" else "days to go ♥",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontFamily = FontFamily.Serif,
              fontStyle = FontStyle.Italic,
              fontWeight = FontWeight.Medium,
              fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Accurate breakdown based on exact calendar calculations
        Text(
          text = if (isPast) {
            primaryStory.formattedPeriodBreakdown
          } else {
            "Until ${primaryStory.displayTitle}"
          },
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Subtle divider line with star & date
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 16.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(1.dp)
              .background(extColors.cardBorder)
          )
          Text(
            text = " • ",
            style = MaterialTheme.typography.bodySmall,
            color = extColors.goldAccent,
            modifier = Modifier.padding(horizontal = 8.dp)
          )
          Text(
            text = if (isPast) "Since ${primaryStory.formattedDate}" else primaryStory.formattedDate,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = extColors.textMuted
          )
          Text(
            text = " • ",
            style = MaterialTheme.typography.bodySmall,
            color = extColors.goldAccent,
            modifier = Modifier.padding(horizontal = 8.dp)
          )
          Box(
            modifier = Modifier
              .weight(1f)
              .height(1.dp)
              .background(extColors.cardBorder)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Share Card Pill Action
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onShareClick() }
            .testTag("hero_share_card_btn"),
          shape = RoundedCornerShape(20.dp),
          color = extColors.quoteBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, extColors.goldAccent.copy(alpha = 0.45f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.Share,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isPast) stringResource(R.string.share_card_btn_share_our_time) else stringResource(R.string.share_card_btn_share_moment),
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }
}

/**
 * 3. Time Together Details / Statistics Card
 */
@Composable
private fun TimeTogetherStatsCard(primaryStory: StoryModel) {
  val extColors = LocalCherishExtendedColors.current

  CherishCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("time_together_stats_card"),
    containerColor = MaterialTheme.colorScheme.surface,
    borderColor = extColors.cardBorder,
    contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      StatColumnItem(
        value = String.format("%,d", primaryStory.totalDays),
        label = "Total Days",
        icon = Icons.Default.CalendarMonth,
        iconTint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(1f)
      )

      Box(
        modifier = Modifier
          .width(1.dp)
          .height(44.dp)
          .background(extColors.cardBorder)
      )

      StatColumnItem(
        value = String.format("%,d", primaryStory.totalWeeks),
        label = "Total Weeks",
        icon = Icons.Default.DateRange,
        iconTint = extColors.goldAccent,
        modifier = Modifier.weight(1f)
      )

      Box(
        modifier = Modifier
          .width(1.dp)
          .height(44.dp)
          .background(extColors.cardBorder)
      )

      StatColumnItem(
        value = String.format("%,d", primaryStory.totalMonths),
        label = "Total Months",
        icon = Icons.Default.Favorite,
        iconTint = extColors.blushAccent,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun StatColumnItem(
  value: String,
  label: String,
  icon: ImageVector,
  iconTint: Color,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier.size(15.dp)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold,
          fontSize = 19.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    Spacer(modifier = Modifier.height(3.dp))

    Text(
      text = label.uppercase(),
      style = MaterialTheme.typography.labelSmall.copy(
        letterSpacing = 0.8.sp,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      ),
      color = extColors.textMuted
    )
  }
}

/**
 * 4. Next Important Moment / Milestone Card
 */
@Composable
private fun NextSpecialDateCard(
  primaryStory: StoryModel,
  onAddMoment: () -> Unit = {}
) {
  val extColors = LocalCherishExtendedColors.current
  var isFavorite by remember { mutableStateOf(true) }

  val daysRemaining = primaryStory.daysUntilNextAnniversary
  val anniversaryTitle = primaryStory.nextAnniversaryTitle
  val anniversaryDate = primaryStory.nextAnniversaryFormattedDate
  val yearProgress = primaryStory.currentYearProgress

  val animatedProgress by animateFloatAsState(
    targetValue = yearProgress,
    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
    label = "anniversary_progress"
  )

  CherishCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("next_special_date_card"),
    containerColor = MaterialTheme.colorScheme.surface,
    borderColor = extColors.cardBorder,
    contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(extColors.goldContainer)
              .border(1.dp, extColors.goldAccent.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.DateRange,
              contentDescription = null,
              tint = extColors.goldAccent,
              modifier = Modifier.size(26.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              text = stringResource(R.string.countdown_next_anniversary_label).uppercase(),
              style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.SemiBold
              ),
              color = extColors.goldAccent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = anniversaryTitle,
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = anniversaryDate,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(extColors.rosewoodContainer)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Text(
            text = if (daysRemaining == 0L) "Today! 🎉" else "In $daysRemaining days",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Progress bar towards this milestone
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Yearly Journey Progress",
            style = MaterialTheme.typography.labelSmall,
            color = extColors.textMuted
          )
          Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = extColors.goldAccent
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { animatedProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = extColors.goldAccent,
          trackColor = extColors.goldContainer.copy(alpha = 0.5f)
        )
      }
    }
  }
}

/**
 * 5. Meaningful Daily Romantic Quote Card
 */
@Composable
private fun RomanticQuoteCard(quoteText: String? = null) {
  val extColors = LocalCherishExtendedColors.current

  CherishCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("romantic_quote_card"),
    containerColor = extColors.quoteBackground,
    borderColor = Color.Transparent,
    contentPadding = androidx.compose.foundation.layout.PaddingValues(
      horizontal = 24.dp,
      vertical = 18.dp
    )
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = if (quoteText != null) "“$quoteText”" else stringResource(R.string.countdown_quote),
        style = MaterialTheme.typography.headlineSmall.copy(
          fontFamily = FontFamily.Serif,
          fontStyle = FontStyle.Italic,
          fontSize = 17.sp,
          lineHeight = 24.sp
        ),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      Icon(
        imageVector = Icons.Filled.Favorite,
        contentDescription = null,
        tint = extColors.blushAccent,
        modifier = Modifier.size(14.dp)
      )
    }
  }
}

/**
 * 6. Quick Action Buttons
 */
@Composable
private fun QuickActionsBar(
  onViewMoments: () -> Unit,
  onAddMoment: () -> Unit
) {
  val extColors = LocalCherishExtendedColors.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    SecondaryButton(
      text = "All Moments",
      onClick = onViewMoments,
      icon = Icons.AutoMirrored.Filled.ArrowForward,
      modifier = Modifier.weight(1f),
      testTag = "btn_quick_view_moments"
    )

    PrimaryButton(
      text = "Add Moment",
      onClick = onAddMoment,
      icon = Icons.Filled.Add,
      modifier = Modifier.weight(1f),
      testTag = "btn_quick_add_moment"
    )
  }
}
