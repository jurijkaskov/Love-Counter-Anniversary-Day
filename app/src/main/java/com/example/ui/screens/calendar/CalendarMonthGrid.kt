package com.example.ui.screens.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.models.FirstDayOfWeekOption
import com.example.data.models.MilestoneModel
import com.example.data.models.MilestoneTaskModel
import com.example.data.models.StoryModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalendarMonthGrid(
  yearMonth: YearMonth,
  selectedDate: LocalDate,
  firstDayOfWeekOption: FirstDayOfWeekOption,
  stories: List<StoryModel>,
  milestones: List<MilestoneModel>,
  tasks: List<MilestoneTaskModel>,
  onSelectDate: (LocalDate) -> Unit,
  modifier: Modifier = Modifier
) {
  val today = remember { LocalDate.now() }

  // Generate grid days
  val gridDays = remember(yearMonth, selectedDate, firstDayOfWeekOption, stories, milestones, tasks) {
    val startDayOfWeek = when (firstDayOfWeekOption) {
      FirstDayOfWeekOption.MONDAY -> DayOfWeek.MONDAY
      FirstDayOfWeekOption.SUNDAY -> DayOfWeek.SUNDAY
      FirstDayOfWeekOption.SYSTEM -> WeekFields.of(Locale.getDefault()).firstDayOfWeek
    }

    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()

    // Calculate leading offset
    val firstDayOfWeekVal = firstDayOfMonth.dayOfWeek.value // Monday=1..Sunday=7
    val startDayOfWeekVal = startDayOfWeek.value // Monday=1..Sunday=7
    val leadingDaysCount = (firstDayOfWeekVal - startDayOfWeekVal + 7) % 7

    val totalCells = when {
      leadingDaysCount + daysInMonth <= 28 -> 28
      leadingDaysCount + daysInMonth <= 35 -> 35
      else -> 42
    }

    val daysList = mutableListOf<CalendarDayUiModel>()

    // Leading days from previous month
    val prevMonth = yearMonth.minusMonths(1)
    val prevMonthLength = prevMonth.lengthOfMonth()
    for (i in (leadingDaysCount - 1) downTo 0) {
      val date = prevMonth.atDay(prevMonthLength - i)
      val events = CalendarDataHelper.getAllEventsForDate(date, stories, milestones, tasks)
      daysList.add(
        CalendarDayUiModel(
          date = date,
          isCurrentMonth = false,
          isToday = date == today,
          isSelected = date == selectedDate,
          events = events
        )
      )
    }

    // Days in current month
    for (day in 1..daysInMonth) {
      val date = yearMonth.atDay(day)
      val events = CalendarDataHelper.getAllEventsForDate(date, stories, milestones, tasks)
      daysList.add(
        CalendarDayUiModel(
          date = date,
          isCurrentMonth = true,
          isToday = date == today,
          isSelected = date == selectedDate,
          events = events
        )
      )
    }

    // Trailing days from next month
    val remainingCells = totalCells - daysList.size
    val nextMonth = yearMonth.plusMonths(1)
    for (day in 1..remainingCells) {
      val date = nextMonth.atDay(day)
      val events = CalendarDataHelper.getAllEventsForDate(date, stories, milestones, tasks)
      daysList.add(
        CalendarDayUiModel(
          date = date,
          isCurrentMonth = false,
          isToday = date == today,
          isSelected = date == selectedDate,
          events = events
        )
      )
    }

    daysList
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("calendar_month_grid"),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    // Render 7-day rows
    val rows = gridDays.chunked(7)
    rows.forEach { week ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        week.forEach { dayModel ->
          CalendarDayCell(
            dayUiModel = dayModel,
            onSelectDate = onSelectDate,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}
