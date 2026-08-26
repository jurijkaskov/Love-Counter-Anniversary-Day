package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.CherishIconButton
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.LocalCherishExtendedColors
import kotlinx.coroutines.launch

private data class OnboardingPageData(
  val badgeText: String,
  val titleResId: Int,
  val descriptionResId: Int,
  val visualContent: @Composable () -> Unit,
  val buttonTextResId: Int
)

@Composable
fun OnboardingScreen(
  onComplete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current
  val coroutineScope = rememberCoroutineScope()
  val pagerState = rememberPagerState(pageCount = { 3 })

  val pages = listOf(
    OnboardingPageData(
      badgeText = stringResource(R.string.onboarding_step1_badge, stringResource(R.string.app_name)),
      titleResId = R.string.onboarding_step1_title,
      descriptionResId = R.string.onboarding_step1_description,
      visualContent = { WelcomeHeroVisual() },
      buttonTextResId = R.string.onboarding_get_started
    ),
    OnboardingPageData(
      badgeText = stringResource(R.string.onboarding_step2_badge),
      titleResId = R.string.onboarding_step2_title,
      descriptionResId = R.string.onboarding_step2_description,
      visualContent = { MilestonesHeroVisual() },
      buttonTextResId = R.string.onboarding_next
    ),
    OnboardingPageData(
      badgeText = stringResource(R.string.onboarding_step3_badge),
      titleResId = R.string.onboarding_step3_title,
      descriptionResId = R.string.onboarding_step3_description,
      visualContent = { YourStoryHeroVisual() },
      buttonTextResId = R.string.onboarding_create_first_moment
    )
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 560.dp)
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Navigation / Header (Back & Skip actions)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("onboarding_top_bar"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Back Button (hidden on page 0)
        AnimatedVisibility(
          visible = pagerState.currentPage > 0,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          CherishIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.onboarding_back),
            onClick = {
              coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
              }
            },
            backgroundColor = extColors.rosewoodContainer.copy(alpha = 0.6f),
            tint = MaterialTheme.colorScheme.onSurface,
            testTag = "onboarding_back_button"
          )
        }
        if (pagerState.currentPage == 0) {
          Spacer(modifier = Modifier.size(44.dp))
        }

        // Skip Button (visible on page 0 and 1)
        AnimatedVisibility(
          visible = pagerState.currentPage < pages.size - 1,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          TextButton(
            onClick = onComplete,
            modifier = Modifier.testTag("onboarding_skip_button")
          ) {
            Text(
              text = stringResource(R.string.onboarding_skip),
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium
              ),
              color = extColors.textMuted
            )
          }
        }
      }

      // Horizontal Pager for Step Content
      HorizontalPager(
        state = pagerState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .testTag("onboarding_pager")
      ) { pageIndex ->
        val page = pages[pageIndex]
        OnboardingPageItem(page = page, pageIndex = pageIndex)
      }

      // Bottom Section: Page Indicator & Action Button
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 20.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Dot / Pill Page Indicator
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .padding(bottom = 20.dp)
            .testTag("onboarding_page_indicator")
        ) {
          repeat(pages.size) { index ->
            val isSelected = pagerState.currentPage == index
            val width by animateDpAsState(
              targetValue = if (isSelected) 28.dp else 8.dp,
              animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
              label = "indicator_width"
            )
            val color by animateColorAsState(
              targetValue = if (isSelected) MaterialTheme.colorScheme.primary else extColors.cardBorderSubtle,
              animationSpec = tween(durationMillis = 250),
              label = "indicator_color"
            )

            Box(
              modifier = Modifier
                .height(8.dp)
                .width(width)
                .clip(CircleShape)
                .background(color)
                .clickable {
                  coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                  }
                }
            )
          }
        }

        // Primary Action Button
        val isLastPage = pagerState.currentPage == pages.size - 1
        val currentButtonText = stringResource(pages[pagerState.currentPage].buttonTextResId)
        val buttonIcon = when (pagerState.currentPage) {
          0 -> Icons.Filled.Favorite
          1 -> Icons.AutoMirrored.Filled.ArrowForward
          else -> Icons.Outlined.Stars
        }

        PrimaryButton(
          text = currentButtonText,
          onClick = {
            if (isLastPage) {
              onComplete()
            } else {
              coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
              }
            }
          },
          icon = buttonIcon,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("onboarding_primary_button")
        )
      }
    }
  }
}

@Composable
private fun OnboardingPageItem(
  page: OnboardingPageData,
  pageIndex: Int
) {
  val extColors = LocalCherishExtendedColors.current
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 8.dp)
      .testTag("onboarding_page_$pageIndex"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Subtle Category Badge
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(extColors.rosewoodContainer)
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Text(
        text = page.badgeText,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp
        ),
        color = MaterialTheme.colorScheme.primary
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Bespoke Hero Visual
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp),
      contentAlignment = Alignment.Center
    ) {
      page.visualContent()
    }

    Spacer(modifier = Modifier.height(28.dp))

    // Display Serif Headline
    Text(
      text = stringResource(page.titleResId),
      style = MaterialTheme.typography.headlineMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        lineHeight = 34.sp
      ),
      color = MaterialTheme.colorScheme.onBackground,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Description text
    Text(
      text = stringResource(page.descriptionResId),
      style = MaterialTheme.typography.bodyMedium.copy(
        lineHeight = 22.sp
      ),
      color = extColors.textMuted,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}
