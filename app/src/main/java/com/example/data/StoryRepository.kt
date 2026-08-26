package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.EventCategory
import com.example.data.models.ReminderConfig
import com.example.data.models.StoryModel
import com.example.notifications.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class StoryRepository(private val context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val _stories = MutableStateFlow<List<StoryModel>>(emptyList())
  val stories: StateFlow<List<StoryModel>> = _stories.asStateFlow()

  private val _primaryStory = MutableStateFlow<StoryModel?>(null)
  val primaryStory: StateFlow<StoryModel?> = _primaryStory.asStateFlow()

  init {
    loadStories()
  }

  fun hasAnyStory(): Boolean {
    loadStories()
    return _stories.value.isNotEmpty()
  }

  fun saveStory(story: StoryModel) {
    val current = _stories.value.toMutableList()
    val index = current.indexOfFirst { it.id == story.id }
    if (index >= 0) {
      current[index] = story
    } else {
      // If this is the only story or marked primary, set it primary
      if (current.isEmpty() || story.isPrimary) {
        // Clear other primaries
        for (i in current.indices) {
          current[i] = current[i].copy(isPrimary = false)
        }
      }
      current.add(0, story)
    }
    persist(current)
    // Automatically schedule/update reminders for this story
    val prefManager = PreferencesManager(context)
    ReminderScheduler.scheduleRemindersForStory(
      context = context,
      story = story,
      remindersGloballyEnabled = prefManager.settings.value.remindersEnabled
    )
  }

  fun deleteStory(id: String) {
    val current = _stories.value.filterNot { it.id == id }
    persist(current)
    ReminderScheduler.cancelAllRemindersForStory(context, id)
  }

  fun setPrimaryStory(id: String) {
    val updated = _stories.value.map { it.copy(isPrimary = (it.id == id)) }
    persist(updated)
  }

  fun toggleFavorite(id: String) {
    val current = _stories.value.map {
      if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
    }
    persist(current)
  }

  fun getStoryById(id: String): StoryModel? {
    return _stories.value.find { it.id == id }
  }

  fun syncAllAlarms() {
    ReminderScheduler.rescheduleAll(context)
  }

  fun seedDefaultStoryIfEmpty() {
    if (_stories.value.isEmpty()) {
      val today = LocalDate.now()
      val defaultStory = StoryModel(
        id = "default-story-1",
        category = EventCategory.RELATIONSHIP,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Eleanor & Julian",
        dateEpochDay = today.minusDays(1253).toEpochDay(),
        note = "Every day with you is my favorite day.",
        iconKey = "favorite",
        themeAccent = "rosewood",
        isPrimary = true,
        isFavorite = true
      )

      val weddingMoment = StoryModel(
        id = "moment-wedding",
        category = EventCategory.WEDDING,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Wedding Anniversary",
        dateEpochDay = today.plusDays(156).toEpochDay(),
        note = "Celebrating our vows and lifelong commitment.",
        iconKey = "celebration",
        themeAccent = "gold",
        isPrimary = false,
        isFavorite = true
      )

      val firstDateMoment = StoryModel(
        id = "moment-first-date",
        category = EventCategory.FIRST_DATE,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "First Date",
        dateEpochDay = today.plusDays(342).toEpochDay(),
        note = "Coffee at the cozy corner café that changed everything.",
        iconKey = "favorite_border",
        themeAccent = "blush",
        isPrimary = false,
        isFavorite = true
      )

      val birthdayMoment = StoryModel(
        id = "moment-birthday",
        category = EventCategory.BIRTHDAY,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Her Birthday",
        dateEpochDay = today.plusDays(56).toEpochDay(),
        note = "A special day for the most wonderful person.",
        iconKey = "cake",
        themeAccent = "rosewood",
        isPrimary = false,
        isFavorite = false
      )

      val weMetMoment = StoryModel(
        id = "moment-we-met",
        category = EventCategory.FIRST_DATE,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "We Met",
        dateEpochDay = today.minusDays(1253).toEpochDay(),
        note = "The unforgettable day our paths crossed.",
        iconKey = "favorite",
        themeAccent = "rosewood",
        isPrimary = false,
        isFavorite = true
      )

      val tripMoment = StoryModel(
        id = "moment-trip",
        category = EventCategory.TRIP,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "First Trip Together",
        dateEpochDay = today.minusDays(980).toEpochDay(),
        note = "Exploring the romantic streets and sunsets.",
        iconKey = "flight",
        themeAccent = "gold",
        isPrimary = false,
        isFavorite = false
      )

      val engagementMoment = StoryModel(
        id = "moment-engagement",
        category = EventCategory.ENGAGEMENT,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Engagement",
        dateEpochDay = today.minusDays(540).toEpochDay(),
        note = "A magical evening and an ecstatic YES.",
        iconKey = "ring",
        themeAccent = "blush",
        isPrimary = false,
        isFavorite = true
      )

      val initialList = listOf(
        defaultStory,
        weddingMoment,
        firstDateMoment,
        birthdayMoment,
        weMetMoment,
        tripMoment,
        engagementMoment
      )
      persist(initialList)
    }
  }

  fun resetAll() {
    val stories = _stories.value
    stories.forEach { ReminderScheduler.cancelAllRemindersForStory(context, it.id) }
    prefs.edit().remove(KEY_STORIES).apply()
    _stories.value = emptyList()
    _primaryStory.value = null
  }

  private fun loadStories() {
    val jsonString = prefs.getString(KEY_STORIES, null)
    if (jsonString.isNullOrBlank()) {
      _stories.value = emptyList()
      _primaryStory.value = null
      return
    }

    try {
      val array = JSONArray(jsonString)
      val list = mutableListOf<StoryModel>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        val category = EventCategory.fromId(obj.optString("category", "relationship"))
        val reminderObj = obj.optJSONObject("reminderConfig")
        val reminderConfig = if (reminderObj != null) {
          ReminderConfig.fromJson(reminderObj)
        } else {
          ReminderConfig.defaultForCategory(category)
        }

        list.add(
          StoryModel(
            id = obj.optString("id"),
            category = category,
            yourName = obj.optString("yourName", ""),
            partnerName = obj.optString("partnerName", ""),
            title = obj.optString("title", ""),
            dateEpochDay = obj.optLong("dateEpochDay", LocalDate.now().toEpochDay()),
            note = obj.optString("note", ""),
            iconKey = obj.optString("iconKey", "favorite"),
            themeAccent = obj.optString("themeAccent", "rosewood"),
            isPrimary = obj.optBoolean("isPrimary", false),
            isFavorite = obj.optBoolean("isFavorite", false),
            reminderConfig = reminderConfig,
            createdAtEpochMillis = obj.optLong("createdAtEpochMillis", System.currentTimeMillis())
          )
        )
      }
      _stories.value = list
      _primaryStory.value = list.firstOrNull { it.isPrimary } ?: list.firstOrNull()
    } catch (e: Exception) {
      _stories.value = emptyList()
      _primaryStory.value = null
    }
  }

  private fun persist(list: List<StoryModel>) {
    try {
      val array = JSONArray()
      for (story in list) {
        val obj = JSONObject().apply {
          put("id", story.id)
          put("category", story.category.id)
          put("yourName", story.yourName)
          put("partnerName", story.partnerName)
          put("title", story.title)
          put("dateEpochDay", story.dateEpochDay)
          put("note", story.note)
          put("iconKey", story.iconKey)
          put("themeAccent", story.themeAccent)
          put("isPrimary", story.isPrimary)
          put("isFavorite", story.isFavorite)
          put("reminderConfig", story.reminderConfig.toJson())
          put("createdAtEpochMillis", story.createdAtEpochMillis)
        }
        array.put(obj)
      }
      prefs.edit().putString(KEY_STORIES, array.toString()).apply()
      _stories.value = list
      _primaryStory.value = list.firstOrNull { it.isPrimary } ?: list.firstOrNull()
      com.example.ui.widget.WidgetUpdateHelper.updateAllWidgets(context)
    } catch (e: Exception) {
      // Ignored
    }
  }

  companion object {
    private const val PREFS_NAME = "cherish_stories_prefs"
    private const val KEY_STORIES = "key_cherish_stories_json"
  }
}
