package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.theme.LocalCherishExtendedColors
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPickerDialog(
  initialYearMonth: YearMonth,
  onDismiss: () -> Unit,
  onSelectYearMonth: (YearMonth) -> Unit
) {
  val extColors = LocalCherishExtendedColors.current
  var currentYear by remember { mutableIntStateOf(initialYearMonth.year) }
  var selectedMonth by remember { mutableIntStateOf(initialYearMonth.monthValue) }

  val months = remember { Month.entries }

  BasicAlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp)
      .testTag("month_year_picker_dialog")
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.dp, extColors.cardBorder),
      shadowElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Title
        Text(
          text = stringResource(R.string.calendar_select_month_year),
          style = MaterialTheme.typography.titleLarge.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Year Selector Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { currentYear -= 1 },
            modifier = Modifier.testTag("picker_prev_year")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Previous Year",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }

          Text(
            text = currentYear.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
          )

          IconButton(
            onClick = { currentYear += 1 },
            modifier = Modifier.testTag("picker_next_year")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Next Year",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Months 3x4 Grid
        LazyVerticalGrid(
          columns = GridCells.Fixed(3),
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          itemsIndexed(months) { index, month ->
            val monthNum = index + 1
            val isSelected = monthNum == selectedMonth
            val monthName = month.getDisplayName(TextStyle.SHORT, Locale.getDefault())

            Box(
              modifier = Modifier
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                  if (isSelected) MaterialTheme.colorScheme.primary
                  else extColors.cardBorderSubtle
                )
                .clickable {
                  selectedMonth = monthNum
                }
                .testTag("picker_month_$monthNum"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = monthName,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          SecondaryButton(
            text = stringResource(R.string.btn_cancel),
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
          )

          PrimaryButton(
            text = "Select",
            onClick = {
              onSelectYearMonth(YearMonth.of(currentYear, selectedMonth))
            },
            modifier = Modifier.weight(1f),
            testTag = "picker_confirm_button"
          )
        }
      }
    }
  }
}
