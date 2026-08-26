package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.JournalEntryModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import java.time.LocalDate

class JournalRepository(private val context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val _entries = MutableStateFlow<List<JournalEntryModel>>(emptyList())
  val entries: StateFlow<List<JournalEntryModel>> = _entries.asStateFlow()

  init {
    loadEntries()
  }

  fun hasAnyEntries(): Boolean {
    loadEntries()
    return _entries.value.isNotEmpty()
  }

  fun saveEntry(entry: JournalEntryModel) {
    val current = _entries.value.toMutableList()
    val index = current.indexOfFirst { it.id == entry.id }
    val entryToSave = entry.copy(updatedAtEpochMillis = System.currentTimeMillis())
    if (index >= 0) {
      current[index] = entryToSave
    } else {
      current.add(0, entryToSave)
    }
    persist(current)
  }

  fun deleteEntry(id: String) {
    val current = _entries.value.filterNot { it.id == id }
    persist(current)
  }

  fun toggleFavorite(id: String) {
    val current = _entries.value.map {
      if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
    }
    persist(current)
  }

  fun getEntryById(id: String): JournalEntryModel? {
    return _entries.value.find { it.id == id }
  }

  fun getEntriesForStory(storyId: String): List<JournalEntryModel> {
    return _entries.value.filter { it.associatedStoryId == storyId }
  }

  fun unbindStory(storyId: String) {
    val current = _entries.value.map {
      if (it.associatedStoryId == storyId) it.copy(associatedStoryId = null) else it
    }
    persist(current)
  }

  fun seedDefaultEntriesIfEmpty(storyRepository: StoryRepository? = null) {
    if (_entries.value.isEmpty()) {
      val today = LocalDate.now()
      val defaultStories = storyRepository?.stories?.value ?: emptyList()
      val tripStoryId = defaultStories.find { it.title.contains("Trip", ignoreCase = true) }?.id ?: "moment-trip"
      val weMetStoryId = defaultStories.find { it.title.contains("Met", ignoreCase = true) }?.id ?: "moment-we-met"
      val engagementStoryId = defaultStories.find { it.title.contains("Engagement", ignoreCase = true) }?.id ?: "moment-engagement"

      val beachSunsetEntry = JournalEntryModel(
        id = "journal-beach-sunset",
        title = "Beach Sunset",
        content = "One of the most beautiful sunsets we've ever seen together. A gentle warm breeze off the ocean, the sky turning into brilliant hues of amber, rose, and gold, and your hand in mine. A truly perfect day that I will cherish forever.",
        dateEpochDay = today.minusDays(40).toEpochDay(),
        associatedStoryId = tripStoryId,
        tags = listOf("Travel", "Sunset"),
        iconKey = "favorite",
        moodAccent = "gold",
        isFavorite = true,
        createdAtEpochMillis = System.currentTimeMillis() - 40L * 24 * 60 * 60 * 1000
      )

      val cafeConversationEntry = JournalEntryModel(
        id = "journal-first-coffee",
        title = "First Coffee & Endless Laughter",
        content = "We sat at the cozy corner table for what was supposed to be a quick one-hour coffee. Four hours later, the café was closing and neither of us wanted to leave. I knew right then that you were someone truly special.",
        dateEpochDay = today.minusDays(1250).toEpochDay(),
        associatedStoryId = weMetStoryId,
        tags = listOf("First Date", "Coffee", "Memories"),
        iconKey = "favorite_border",
        moodAccent = "rosewood",
        isFavorite = true,
        createdAtEpochMillis = System.currentTimeMillis() - 1250L * 24 * 60 * 60 * 1000
      )

      val starryProposalEntry = JournalEntryModel(
        id = "journal-starry-proposal",
        title = "Under the Starlit Sky",
        content = "Surrounded by fairy lights and soft music, you looked into my eyes and asked the question that changed our lives forever. My heart skipped a million beats before saying YES with every ounce of love in my soul.",
        dateEpochDay = today.minusDays(540).toEpochDay(),
        associatedStoryId = engagementStoryId,
        tags = listOf("Engagement", "Milestone", "Forever"),
        iconKey = "ring",
        moodAccent = "blush",
        isFavorite = true,
        createdAtEpochMillis = System.currentTimeMillis() - 540L * 24 * 60 * 60 * 1000
      )

      val initialEntries = listOf(beachSunsetEntry, starryProposalEntry, cafeConversationEntry)
      persist(initialEntries)
    }
  }

  fun resetAll() {
    prefs.edit().remove(KEY_ENTRIES).apply()
    _entries.value = emptyList()
  }

  fun clearAll() {
    resetAll()
  }

  fun getAllEntriesAsJson(): JSONArray {
    val array = JSONArray()
    _entries.value.forEach { array.put(it.toJson()) }
    return array
  }

  fun restoreEntriesFromJson(array: JSONArray) {
    val list = mutableListOf<JournalEntryModel>()
    for (i in 0 until array.length()) {
      val obj = array.getJSONObject(i)
      list.add(JournalEntryModel.fromJson(obj))
    }
    persist(list)
  }

  private fun loadEntries() {
    val jsonString = prefs.getString(KEY_ENTRIES, null)
    if (jsonString.isNullOrBlank()) {
      _entries.value = emptyList()
      return
    }

    try {
      val array = JSONArray(jsonString)
      val list = mutableListOf<JournalEntryModel>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(JournalEntryModel.fromJson(obj))
      }
      _entries.value = list
    } catch (e: Exception) {
      _entries.value = emptyList()
    }
  }

  private fun persist(list: List<JournalEntryModel>) {
    _entries.value = list
    val array = JSONArray()
    list.forEach { array.put(it.toJson()) }
    prefs.edit().putString(KEY_ENTRIES, array.toString()).apply()
  }

  companion object {
    private const val PREFS_NAME = "cherish_journal_prefs"
    private const val KEY_ENTRIES = "saved_journal_entries"
  }
}
