package com.example.data.models

import android.content.Context
import com.example.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class MilestoneCategory(
  val id: String,
  val titleResId: Int,
  val defaultIconKey: String
) {
  WEDDING("wedding", R.string.milestones_tab_wedding, "ring"),
  ANNIVERSARY("anniversary", R.string.milestones_tab_anniversary, "favorite"),
  TRIP("trip", R.string.milestones_tab_trip, "flight"),
  CUSTOM("custom", R.string.milestones_tab_custom, "star");

  companion object {
    fun fromId(id: String): MilestoneCategory {
      return entries.find { it.id.equals(id, ignoreCase = true) } ?: CUSTOM
    }
  }
}

data class MilestoneModel(
  val id: String,
  val title: String,
  val category: MilestoneCategory = MilestoneCategory.CUSTOM,
  val description: String = "",
  val targetDateEpochDay: Long? = null,
  val associatedStoryId: String? = null,
  val iconKey: String = "celebration",
  val createdAtEpochMillis: Long = System.currentTimeMillis()
) {
  val targetDate: LocalDate?
    get() = targetDateEpochDay?.let { LocalDate.ofEpochDay(it) }

  val formattedTargetDate: String?
    get() = targetDate?.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

  val daysUntilTargetDate: Long?
    get() = targetDate?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }

  fun getTimeframeLabel(context: Context): String {
    val days = daysUntilTargetDate ?: return context.getString(R.string.timeframe_upcoming)
    return when {
      days < 0 -> context.getString(R.string.timeframe_passed)
      days == 0L -> context.getString(R.string.timeframe_today)
      days in 1..30 -> context.getString(R.string.timeframe_days_to_go, days.toInt())
      else -> context.getString(R.string.timeframe_months_to_go, (days / 30).toInt().coerceAtLeast(1))
    }
  }

  @Deprecated("Use getTimeframeLabel(context)", ReplaceWith("getTimeframeLabel(context)"))
  val timeframeLabel: String
    get() {
      val days = daysUntilTargetDate ?: return "Upcoming"
      return when {
        days < 0 -> "Passed"
        days == 0L -> "Today"
        days in 1..30 -> "${days}d to go"
        days in 31..89 -> "${days / 30} Months to go"
        days in 90..120 -> "3 Months to go"
        days in 121..210 -> "6 Months to go"
        days in 211..390 -> "12 Months to go"
        else -> "${days / 30} Months to go"
      }
    }
}

data class MilestoneTaskModel(
  val id: String,
  val milestoneId: String,
  val title: String,
  val isCompleted: Boolean = false,
  val dueDateEpochDay: Long? = null,
  val note: String = "",
  val orderIndex: Int = 0,
  val createdAtEpochMillis: Long = System.currentTimeMillis(),
  val completedAtEpochMillis: Long? = null
) {
  val dueDate: LocalDate?
    get() = dueDateEpochDay?.let { LocalDate.ofEpochDay(it) }

  val formattedDueDate: String?
    get() = dueDate?.format(DateTimeFormatter.ofPattern("MMM d"))
}

data class MilestoneWithTasks(
  val milestone: MilestoneModel,
  val tasks: List<MilestoneTaskModel> = emptyList(),
  val associatedStory: StoryModel? = null
) {
  val completedTasksCount: Int
    get() = tasks.count { it.isCompleted }

  val totalTasksCount: Int
    get() = tasks.size

  val progress: Float
    get() = if (totalTasksCount == 0) 0f else (completedTasksCount.toFloat() / totalTasksCount.toFloat()).coerceIn(0f, 1f)

  val progressPercent: Int
    get() = (progress * 100).toInt()

  val isFullyCompleted: Boolean
    get() = totalTasksCount > 0 && completedTasksCount == totalTasksCount

  val sortedTasks: List<MilestoneTaskModel>
    get() = tasks.sortedWith(
      compareBy<MilestoneTaskModel> { it.isCompleted }
        .thenBy { it.orderIndex }
        .thenBy { it.createdAtEpochMillis }
    )
}
