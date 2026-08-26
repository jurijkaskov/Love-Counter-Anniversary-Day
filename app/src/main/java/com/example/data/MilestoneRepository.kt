package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.MilestoneCategory
import com.example.data.models.MilestoneModel
import com.example.data.models.MilestoneTaskModel
import com.example.data.models.MilestoneWithTasks
import com.example.data.models.StoryModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

class MilestoneRepository(
  context: Context,
  private val storyRepository: StoryRepository
) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val _milestones = MutableStateFlow<List<MilestoneModel>>(emptyList())
  val milestones: StateFlow<List<MilestoneModel>> = _milestones.asStateFlow()

  private val _tasks = MutableStateFlow<List<MilestoneTaskModel>>(emptyList())
  val tasks: StateFlow<List<MilestoneTaskModel>> = _tasks.asStateFlow()

  init {
    loadData()
  }

  fun getMilestonesWithTasks(): List<MilestoneWithTasks> {
    val currentMilestones = _milestones.value
    val currentTasks = _tasks.value
    val stories = storyRepository.stories.value

    return currentMilestones.map { milestone ->
      val associatedStory = milestone.associatedStoryId?.let { storyId ->
        stories.find { it.id == storyId }
      }
      val milestoneTasks = currentTasks.filter { it.milestoneId == milestone.id }
      MilestoneWithTasks(
        milestone = milestone,
        tasks = milestoneTasks,
        associatedStory = associatedStory
      )
    }
  }

  fun getMilestoneWithTasks(milestoneId: String): MilestoneWithTasks? {
    val milestone = _milestones.value.find { it.id == milestoneId } ?: return null
    val milestoneTasks = _tasks.value.filter { it.milestoneId == milestoneId }
    val story = milestone.associatedStoryId?.let { storyId ->
      storyRepository.stories.value.find { it.id == storyId }
    }
    return MilestoneWithTasks(
      milestone = milestone,
      tasks = milestoneTasks,
      associatedStory = story
    )
  }

  fun saveMilestone(milestone: MilestoneModel) {
    val current = _milestones.value.toMutableList()
    val index = current.indexOfFirst { it.id == milestone.id }
    if (index >= 0) {
      current[index] = milestone
    } else {
      current.add(0, milestone)
    }
    persistMilestones(current)
  }

  fun deleteMilestone(milestoneId: String) {
    val updatedMilestones = _milestones.value.filterNot { it.id == milestoneId }
    val updatedTasks = _tasks.value.filterNot { it.milestoneId == milestoneId }
    persistMilestones(updatedMilestones)
    persistTasks(updatedTasks)
  }

  fun saveTask(task: MilestoneTaskModel) {
    val current = _tasks.value.toMutableList()
    val index = current.indexOfFirst { it.id == task.id }
    if (index >= 0) {
      current[index] = task
    } else {
      current.add(task)
    }
    persistTasks(current)
  }

  fun toggleTask(taskId: String) {
    val current = _tasks.value.map {
      if (it.id == taskId) {
        val newStatus = !it.isCompleted
        it.copy(
          isCompleted = newStatus,
          completedAtEpochMillis = if (newStatus) System.currentTimeMillis() else null
        )
      } else it
    }
    persistTasks(current)
  }

  fun deleteTask(taskId: String) {
    val updated = _tasks.value.filterNot { it.id == taskId }
    persistTasks(updated)
  }

  fun seedDefaultMilestonesIfEmpty() {
    if (_milestones.value.isNotEmpty()) return

    val today = LocalDate.now()
    val weddingMilestoneId = "milestone-wedding-journey"
    val anniversaryMilestoneId = "milestone-anniversary-surprise"
    val tripMilestoneId = "milestone-summer-trip"

    val weddingMilestone = MilestoneModel(
      id = weddingMilestoneId,
      title = "Wedding Preparation",
      category = MilestoneCategory.WEDDING,
      description = "Preparing for our dream ceremony and celebration with family & friends.",
      targetDateEpochDay = today.plusDays(156).toEpochDay(),
      associatedStoryId = "moment-wedding",
      iconKey = "ring",
      createdAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 30)
    )

    val weddingTasks = listOf(
      MilestoneTaskModel(
        id = "wtask-1",
        milestoneId = weddingMilestoneId,
        title = "Book the venue",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(60).toEpochDay(),
        orderIndex = 1
      ),
      MilestoneTaskModel(
        id = "wtask-2",
        milestoneId = weddingMilestoneId,
        title = "Establish the budget",
        isCompleted = false,
        dueDateEpochDay = today.minusDays(30).toEpochDay(),
        orderIndex = 2
      ),
      MilestoneTaskModel(
        id = "wtask-3",
        milestoneId = weddingMilestoneId,
        title = "Choose the dress",
        isCompleted = false,
        dueDateEpochDay = today.plusDays(30).toEpochDay(),
        orderIndex = 3
      ),
      MilestoneTaskModel(
        id = "wtask-4",
        milestoneId = weddingMilestoneId,
        title = "Send save-the-dates",
        isCompleted = false,
        dueDateEpochDay = today.plusDays(45).toEpochDay(),
        orderIndex = 4
      ),
      MilestoneTaskModel(
        id = "wtask-5",
        milestoneId = weddingMilestoneId,
        title = "Confirm vendors",
        isCompleted = true,
        dueDateEpochDay = today.plusDays(60).toEpochDay(),
        orderIndex = 5
      ),
      MilestoneTaskModel(
        id = "wtask-6",
        milestoneId = weddingMilestoneId,
        title = "Plan honeymoon",
        isCompleted = false,
        dueDateEpochDay = today.plusDays(90).toEpochDay(),
        orderIndex = 6
      ),
      MilestoneTaskModel(
        id = "wtask-7",
        milestoneId = weddingMilestoneId,
        title = "Select floral arrangements",
        isCompleted = false,
        orderIndex = 7
      ),
      MilestoneTaskModel(
        id = "wtask-8",
        milestoneId = weddingMilestoneId,
        title = "Cake tasting session",
        isCompleted = false,
        orderIndex = 8
      ),
      MilestoneTaskModel(
        id = "wtask-9",
        milestoneId = weddingMilestoneId,
        title = "Finalize guest list",
        isCompleted = false,
        orderIndex = 9
      ),
      MilestoneTaskModel(
        id = "wtask-10",
        milestoneId = weddingMilestoneId,
        title = "Write personal vows",
        isCompleted = false,
        orderIndex = 10
      ),
      MilestoneTaskModel(
        id = "wtask-11",
        milestoneId = weddingMilestoneId,
        title = "Rehearsal dinner reservations",
        isCompleted = false,
        orderIndex = 11
      )
    )

    val anniversaryMilestone = MilestoneModel(
      id = anniversaryMilestoneId,
      title = "Anniversary Evening Surprise",
      category = MilestoneCategory.ANNIVERSARY,
      description = "Planning a quiet romantic dinner and personalized keepsake.",
      targetDateEpochDay = today.plusDays(24).toEpochDay(),
      associatedStoryId = "moment-first-date",
      iconKey = "favorite",
      createdAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 10)
    )

    val anniversaryTasks = listOf(
      MilestoneTaskModel(
        id = "atask-1",
        milestoneId = anniversaryMilestoneId,
        title = "Secret rooftop dinner reservation",
        isCompleted = true,
        orderIndex = 1
      ),
      MilestoneTaskModel(
        id = "atask-2",
        milestoneId = anniversaryMilestoneId,
        title = "Order custom engraved memory book",
        isCompleted = true,
        orderIndex = 2
      ),
      MilestoneTaskModel(
        id = "atask-3",
        milestoneId = anniversaryMilestoneId,
        title = "Pick up favourite flowers & handwritten card",
        isCompleted = false,
        orderIndex = 3
      )
    )

    val tripMilestone = MilestoneModel(
      id = tripMilestoneId,
      title = "Seaside Holiday Getaway",
      category = MilestoneCategory.TRIP,
      description = "Our weekend getaway to celebrate our journey.",
      targetDateEpochDay = today.minusDays(10).toEpochDay(),
      associatedStoryId = "moment-trip",
      iconKey = "flight",
      createdAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 60)
    )

    val tripTasks = listOf(
      MilestoneTaskModel(
        id = "ttask-1",
        milestoneId = tripMilestoneId,
        title = "Book sunset cruise tickets",
        isCompleted = true,
        orderIndex = 1
      ),
      MilestoneTaskModel(
        id = "ttask-2",
        milestoneId = tripMilestoneId,
        title = "Reserve boutique seaside hotel",
        isCompleted = true,
        orderIndex = 2
      ),
      MilestoneTaskModel(
        id = "ttask-3",
        milestoneId = tripMilestoneId,
        title = "Pack travel bag with surprises",
        isCompleted = true,
        orderIndex = 3
      )
    )

    persistMilestones(listOf(weddingMilestone, anniversaryMilestone, tripMilestone))
    persistTasks(weddingTasks + anniversaryTasks + tripTasks)
  }

  private fun loadData() {
    loadMilestones()
    loadTasks()
  }

  private fun loadMilestones() {
    val jsonString = prefs.getString(KEY_MILESTONES, null)
    if (jsonString.isNullOrBlank()) {
      _milestones.value = emptyList()
      return
    }
    try {
      val array = JSONArray(jsonString)
      val list = mutableListOf<MilestoneModel>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          MilestoneModel(
            id = obj.optString("id", UUID.randomUUID().toString()),
            title = obj.optString("title", ""),
            category = MilestoneCategory.fromId(obj.optString("category", "custom")),
            description = obj.optString("description", ""),
            targetDateEpochDay = if (obj.has("targetDateEpochDay") && !obj.isNull("targetDateEpochDay")) obj.optLong("targetDateEpochDay") else null,
            associatedStoryId = if (obj.has("associatedStoryId") && !obj.isNull("associatedStoryId")) obj.optString("associatedStoryId").ifBlank { null } else null,
            iconKey = obj.optString("iconKey", "celebration"),
            createdAtEpochMillis = obj.optLong("createdAtEpochMillis", System.currentTimeMillis())
          )
        )
      }
      _milestones.value = list
    } catch (e: Exception) {
      _milestones.value = emptyList()
    }
  }

  private fun loadTasks() {
    val jsonString = prefs.getString(KEY_TASKS, null)
    if (jsonString.isNullOrBlank()) {
      _tasks.value = emptyList()
      return
    }
    try {
      val array = JSONArray(jsonString)
      val list = mutableListOf<MilestoneTaskModel>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          MilestoneTaskModel(
            id = obj.optString("id", UUID.randomUUID().toString()),
            milestoneId = obj.optString("milestoneId", ""),
            title = obj.optString("title", ""),
            isCompleted = obj.optBoolean("isCompleted", false),
            dueDateEpochDay = if (obj.has("dueDateEpochDay") && !obj.isNull("dueDateEpochDay")) obj.optLong("dueDateEpochDay") else null,
            note = obj.optString("note", ""),
            orderIndex = obj.optInt("orderIndex", 0),
            createdAtEpochMillis = obj.optLong("createdAtEpochMillis", System.currentTimeMillis()),
            completedAtEpochMillis = if (obj.has("completedAtEpochMillis") && !obj.isNull("completedAtEpochMillis")) obj.optLong("completedAtEpochMillis") else null
          )
        )
      }
      _tasks.value = list
    } catch (e: Exception) {
      _tasks.value = emptyList()
    }
  }

  private fun persistMilestones(list: List<MilestoneModel>) {
    try {
      val array = JSONArray()
      for (m in list) {
        val obj = JSONObject().apply {
          put("id", m.id)
          put("title", m.title)
          put("category", m.category.id)
          put("description", m.description)
          if (m.targetDateEpochDay != null) {
            put("targetDateEpochDay", m.targetDateEpochDay)
          }
          if (m.associatedStoryId != null) {
            put("associatedStoryId", m.associatedStoryId)
          }
          put("iconKey", m.iconKey)
          put("createdAtEpochMillis", m.createdAtEpochMillis)
        }
        array.put(obj)
      }
      prefs.edit().putString(KEY_MILESTONES, array.toString()).apply()
      _milestones.value = list
    } catch (e: Exception) {
      // Ignored
    }
  }

  private fun persistTasks(list: List<MilestoneTaskModel>) {
    try {
      val array = JSONArray()
      for (t in list) {
        val obj = JSONObject().apply {
          put("id", t.id)
          put("milestoneId", t.milestoneId)
          put("title", t.title)
          put("isCompleted", t.isCompleted)
          if (t.dueDateEpochDay != null) {
            put("dueDateEpochDay", t.dueDateEpochDay)
          }
          put("note", t.note)
          put("orderIndex", t.orderIndex)
          put("createdAtEpochMillis", t.createdAtEpochMillis)
          if (t.completedAtEpochMillis != null) {
            put("completedAtEpochMillis", t.completedAtEpochMillis)
          }
        }
        array.put(obj)
      }
      prefs.edit().putString(KEY_TASKS, array.toString()).apply()
      _tasks.value = list
    } catch (e: Exception) {
      // Ignored
    }
  }

  companion object {
    private const val PREFS_NAME = "cherish_milestones_prefs"
    private const val KEY_MILESTONES = "key_cherish_milestones_json"
    private const val KEY_TASKS = "key_cherish_milestone_tasks_json"
  }
}
