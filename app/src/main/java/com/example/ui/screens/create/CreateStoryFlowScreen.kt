package com.example.ui.screens.create

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.StoryRepository
import com.example.data.models.EventCategory
import com.example.data.models.StoryModel
import com.example.ui.components.CherishIconButton
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.LocalCherishExtendedColors
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun CreateStoryFlowScreen(
  storyRepository: StoryRepository,
  onStoryCreated: () -> Unit,
  onCancel: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val extColors = LocalCherishExtendedColors.current
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  var currentStep by remember { mutableIntStateOf(1) }
  val totalSteps = 5

  // Form State
  var selectedCategory by remember { mutableStateOf(EventCategory.RELATIONSHIP) }
  var yourName by remember { mutableStateOf("") }
  var partnerName by remember { mutableStateOf("") }
  var storyTitle by remember { mutableStateOf("") }
  var userEditedTitle by remember { mutableStateOf(false) }
  var selectedDate by remember { mutableStateOf(LocalDate.now().minusYears(1)) }
  var personalNote by remember { mutableStateOf("") }
  var selectedSymbolKey by remember { mutableStateOf("favorite") }
  var selectedThemeAccent by remember { mutableStateOf("rosewood") }
  var validationError by remember { mutableStateOf<String?>(null) }

  // Synchronize category change with default symbol and title
  fun onCategoryChanged(newCat: EventCategory) {
    selectedCategory = newCat
    val matched = AvailableCategoryOptions.find { it.category == newCat }
    if (matched != null) {
      selectedSymbolKey = matched.defaultIconKey
    }
    if (!userEditedTitle) {
      storyTitle = if (yourName.isNotBlank() && partnerName.isNotBlank()) {
        "$yourName & $partnerName"
      } else {
        newCat.defaultTitle
      }
    }
  }

  fun onYourNameUpdated(name: String) {
    yourName = name
    validationError = null
    if (!userEditedTitle) {
      storyTitle = if (name.isNotBlank() && partnerName.isNotBlank()) {
        "$name & $partnerName"
      } else if (name.isNotBlank()) {
        name
      } else {
        selectedCategory.defaultTitle
      }
    }
  }

  fun onPartnerNameUpdated(name: String) {
    partnerName = name
    validationError = null
    if (!userEditedTitle) {
      storyTitle = if (yourName.isNotBlank() && name.isNotBlank()) {
        "$yourName & $name"
      } else if (name.isNotBlank()) {
        name
      } else {
        selectedCategory.defaultTitle
      }
    }
  }

  fun onStoryTitleUpdated(title: String) {
    storyTitle = title
    userEditedTitle = title.isNotBlank()
    validationError = null
  }

  fun handleNextStep() {
    if (currentStep == 2) {
      val isCoupleCategory = selectedCategory in listOf(
        EventCategory.RELATIONSHIP,
        EventCategory.WEDDING,
        EventCategory.FIRST_DATE,
        EventCategory.ENGAGEMENT
      )
      if (isCoupleCategory && yourName.isBlank() && partnerName.isBlank() && storyTitle.isBlank()) {
        validationError = context.getString(R.string.create_step2_error_required)
        return
      }
    }

    if (currentStep < totalSteps) {
      currentStep++
      scope.launch {
        listState.animateScrollToItem(0)
      }
    } else {
      // Step 5 Confirmation -> Save and complete!
      val storyToSave = StoryModel(
        category = selectedCategory,
        yourName = yourName.trim(),
        partnerName = partnerName.trim(),
        title = storyTitle.trim().ifEmpty {
          if (yourName.isNotBlank() && partnerName.isNotBlank()) "$yourName & $partnerName" else selectedCategory.defaultTitle
        },
        dateEpochDay = selectedDate.toEpochDay(),
        note = personalNote.trim().ifEmpty { "Every day with you is my favorite day." },
        iconKey = selectedSymbolKey,
        themeAccent = selectedThemeAccent,
        isPrimary = true,
        reminderConfig = com.example.data.models.ReminderConfig.defaultForCategory(selectedCategory)
      )
      storyRepository.saveStory(storyToSave)
      onStoryCreated()
    }
  }

  fun handlePrevStep() {
    if (currentStep > 1) {
      currentStep--
      scope.launch {
        listState.animateScrollToItem(0)
      }
    } else {
      onCancel?.invoke()
    }
  }

  Surface(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding(),
    color = MaterialTheme.colorScheme.background
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 600.dp)
      ) {
        // Top App Bar with Step Indicator and Back Button
        TopFlowBar(
          currentStep = currentStep,
          totalSteps = totalSteps,
          onBackClick = { handlePrevStep() },
          onCancelClick = onCancel
        )

        // Progress Bar
        LinearProgressIndicator(
          progress = { currentStep.toFloat() / totalSteps.toFloat() },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .testTag("create_flow_progress_bar"),
          color = MaterialTheme.colorScheme.primary,
          trackColor = extColors.cardBorder
        )

        // Step Content inside scrollable list
        LazyColumn(
          state = listState,
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          item {
            Spacer(modifier = Modifier.height(20.dp))
          }

          item {
            AnimatedContent(
              targetState = currentStep,
              transitionSpec = {
                if (targetState > initialState) {
                  (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width } + fadeOut()
                  )
                } else {
                  (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> width } + fadeOut()
                  )
                }
              },
              label = "step_transition"
            ) { step ->
              when (step) {
                1 -> Step1CategorySelection(
                  selectedCategory = selectedCategory,
                  onSelectCategory = { onCategoryChanged(it) }
                )
                2 -> Step2NamesTitle(
                  category = selectedCategory,
                  yourName = yourName,
                  onYourNameChange = { onYourNameUpdated(it) },
                  partnerName = partnerName,
                  onPartnerNameChange = { onPartnerNameUpdated(it) },
                  storyTitle = storyTitle,
                  onStoryTitleChange = { onStoryTitleUpdated(it) },
                  validationError = validationError
                )
                3 -> Step3DateSelection(
                  selectedDate = selectedDate,
                  onDateSelected = { selectedDate = it }
                )
                4 -> Step4PersonalTouch(
                  personalNote = personalNote,
                  onPersonalNoteChange = { personalNote = it },
                  selectedSymbolKey = selectedSymbolKey,
                  onSelectSymbolKey = { selectedSymbolKey = it },
                  selectedThemeAccent = selectedThemeAccent,
                  onSelectThemeAccent = { selectedThemeAccent = it }
                )
                5 -> Step5PreviewConfirmation(
                  category = selectedCategory,
                  yourName = yourName,
                  partnerName = partnerName,
                  storyTitle = storyTitle,
                  selectedDate = selectedDate,
                  personalNote = personalNote,
                  selectedSymbolKey = selectedSymbolKey,
                  selectedThemeAccent = selectedThemeAccent
                )
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(32.dp))
          }
        }

        // Bottom Action Bar
        BottomActionBar(
          currentStep = currentStep,
          totalSteps = totalSteps,
          onNextClick = { handleNextStep() },
          onSkipClick = if (currentStep == 4) {
            {
              currentStep = 5
              scope.launch { listState.animateScrollToItem(0) }
            }
          } else null
        )
      }
    }
  }
}

@Composable
private fun TopFlowBar(
  currentStep: Int,
  totalSteps: Int,
  onBackClick: () -> Unit,
  onCancelClick: (() -> Unit)? = null
) {
  val extColors = LocalCherishExtendedColors.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("create_flow_top_bar"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    CherishIconButton(
      icon = Icons.AutoMirrored.Filled.ArrowBack,
      contentDescription = "Back",
      onClick = onBackClick,
      testTag = "create_flow_back_button"
    )

    // Step Indicator Text
    Text(
      text = stringResource(R.string.create_story_step_indicator, currentStep, totalSteps),
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      ),
      color = extColors.textMuted
    )

    if (onCancelClick != null) {
      CherishIconButton(
        icon = Icons.Default.Close,
        contentDescription = "Close",
        onClick = onCancelClick,
        testTag = "create_flow_cancel_button"
      )
    } else {
      Spacer(modifier = Modifier.size(44.dp))
    }
  }
}

@Composable
private fun BottomActionBar(
  currentStep: Int,
  totalSteps: Int,
  onNextClick: () -> Unit,
  onSkipClick: (() -> Unit)? = null
) {
  val extColors = LocalCherishExtendedColors.current

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("create_flow_bottom_bar"),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp,
    shadowElevation = 8.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      PrimaryButton(
        text = if (currentStep == totalSteps) {
          stringResource(R.string.create_story_finish)
        } else {
          stringResource(R.string.create_story_next)
        },
        onClick = onNextClick,
        modifier = Modifier.fillMaxWidth(),
        icon = if (currentStep == totalSteps) Icons.Filled.Favorite else Icons.AutoMirrored.Filled.ArrowForward,
        testTag = "create_flow_next_button"
      )

      if (onSkipClick != null) {
        Spacer(modifier = Modifier.height(6.dp))
        TextButton(
          onClick = onSkipClick,
          modifier = Modifier.testTag("create_flow_skip_button")
        ) {
          Text(
            text = stringResource(R.string.create_story_skip_optional),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = extColors.textMuted
          )
        }
      }
    }
  }
}
