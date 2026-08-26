package com.example.ui.screens.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.EventCategory
import com.example.ui.theme.LocalCherishExtendedColors

@Composable
fun Step2NamesTitle(
  category: EventCategory,
  yourName: String,
  onYourNameChange: (String) -> Unit,
  partnerName: String,
  onPartnerNameChange: (String) -> Unit,
  storyTitle: String,
  onStoryTitleChange: (String) -> Unit,
  validationError: String?,
  modifier: Modifier = Modifier
) {
  val extColors = LocalCherishExtendedColors.current

  val isCoupleCategory = category in listOf(
    EventCategory.RELATIONSHIP,
    EventCategory.WEDDING,
    EventCategory.FIRST_DATE,
    EventCategory.ENGAGEMENT
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("create_step_2_container"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Badge
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(extColors.rosewoodContainer)
        .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
      Text(
        text = stringResource(R.string.create_step2_badge),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 0.5.sp
        ),
        color = MaterialTheme.colorScheme.primary
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Headline
    Text(
      text = stringResource(R.string.create_step2_headline),
      style = MaterialTheme.typography.headlineMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Subtitle
    Text(
      text = stringResource(R.string.create_step2_subhead),
      style = MaterialTheme.typography.bodyMedium,
      color = extColors.textMuted
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Input Fields
    if (isCoupleCategory) {
      // Your Name
      OutlinedTextField(
        value = yourName,
        onValueChange = onYourNameChange,
        label = { Text(stringResource(R.string.input_your_name)) },
        placeholder = { Text(stringResource(R.string.input_your_name_hint), color = extColors.textMuted) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = extColors.cardBorder,
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Words,
          imeAction = ImeAction.Next
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_your_name_field")
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Partner's Name
      OutlinedTextField(
        value = partnerName,
        onValueChange = onPartnerNameChange,
        label = { Text(stringResource(R.string.input_partner_name)) },
        placeholder = { Text(stringResource(R.string.input_partner_name_hint), color = extColors.textMuted) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Outlined.Favorite,
            contentDescription = null,
            tint = extColors.goldAccent
          )
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = extColors.cardBorder,
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Words,
          imeAction = ImeAction.Next
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_partner_name_field")
      )

      Spacer(modifier = Modifier.height(16.dp))
    }

    // Story Title
    OutlinedTextField(
      value = storyTitle,
      onValueChange = onStoryTitleChange,
      label = { Text(stringResource(R.string.input_story_title)) },
      placeholder = {
        Text(
          if (isCoupleCategory) stringResource(R.string.input_story_title_hint) else category.defaultTitle,
          color = extColors.textMuted
        )
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Outlined.Edit,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondary
        )
      },
      singleLine = true,
      shape = RoundedCornerShape(18.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = extColors.cardBorder,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface
      ),
      keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Words,
        imeAction = ImeAction.Done
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("input_story_title_field")
    )

    // Validation Error
    AnimatedVisibility(visible = validationError != null) {
      if (validationError != null) {
        Text(
          text = validationError,
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
          modifier = Modifier
            .padding(top = 8.dp)
            .testTag("validation_error_text")
        )
      }
    }
  }
}
