package com.example.ui.screens.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.FirstDayOfWeekOption
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalendarWeekdayBar(
  firstDayOfWeekOption: FirstDayOfWeekOption,
  modifier: Modifier = Modifier
) {
  val weekdays = remember(firstDayOfWeekOption) {
    val startDay = when (firstDayOfWeekOption) {
      FirstDayOfWeekOption.MONDAY -> DayOfWeek.MONDAY
      FirstDayOfWeekOption.SUNDAY -> DayOfWeek.SUNDAY
      FirstDayOfWeekOption.SYSTEM -> WeekFields.of(Locale.getDefault()).firstDayOfWeek
    }

    val days = mutableListOf<DayOfWeek>()
    var current = startDay
    for (i in 0 until 7) {
      days.add(current)
      current = current.plus(1)
    }
    days
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 6.dp)
      .testTag("calendar_weekday_bar"),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically
  ) {
    weekdays.forEach { dayOfWeek ->
      val label = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(Locale.getDefault())
      Text(
        text = label.take(3),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.SemiBold,
          letterSpacing = 1.sp,
          fontSize = 11.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
        modifier = Modifier.weight(1f)
      )
    }
  }
}
