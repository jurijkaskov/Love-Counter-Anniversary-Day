package com.example.data.models

import android.content.Context
import com.example.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

data class JournalEntryModel(
  val id: String = UUID.randomUUID().toString(),
  val title: String = "",
  val content: String = "",
  val dateEpochDay: Long = LocalDate.now().toEpochDay(),
  val associatedStoryId: String? = null,
  val tags: List<String> = emptyList(),
  val iconKey: String = "favorite",
  val moodAccent: String = "rosewood",
  val photoUri: String? = null,
  val isFavorite: Boolean = false,
  val createdAtEpochMillis: Long = System.currentTimeMillis(),
  val updatedAtEpochMillis: Long = System.currentTimeMillis()
) {
  val localDate: LocalDate
    get() = LocalDate.ofEpochDay(dateEpochDay)

  val formattedDate: String
    get() {
      val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
      return localDate.format(formatter)
    }

  fun getDisplayTitle(context: Context): String = if (title.isNotBlank()) title else context.getString(R.string.model_untitled_memory)

  val displayTitle: String
    get() = title.ifBlank { "" }

  val previewSnippet: String
    get() {
      val trimmed = content.trim()
      if (trimmed.isEmpty()) return ""
      val lines = trimmed.lines()
      val firstLine = lines.firstOrNull() ?: ""
      return if (firstLine.length > 110) {
        firstLine.take(107) + "..."
      } else if (lines.size > 1) {
        firstLine + "..."
      } else {
        firstLine
      }
    }

  val isPastDate: Boolean
    get() = !localDate.isAfter(LocalDate.now())

  fun toJson(): JSONObject {
    val json = JSONObject()
    json.put("id", id)
    json.put("title", title)
    json.put("content", content)
    json.put("dateEpochDay", dateEpochDay)
    if (associatedStoryId != null) {
      json.put("associatedStoryId", associatedStoryId)
    }
    val tagsArray = JSONArray()
    tags.forEach { tagsArray.put(it) }
    json.put("tags", tagsArray)
    json.put("iconKey", iconKey)
    json.put("moodAccent", moodAccent)
    if (photoUri != null) {
      json.put("photoUri", photoUri)
    }
    json.put("isFavorite", isFavorite)
    json.put("createdAtEpochMillis", createdAtEpochMillis)
    json.put("updatedAtEpochMillis", updatedAtEpochMillis)
    return json
  }

  companion object {
    fun fromJson(json: JSONObject): JournalEntryModel {
      val tagsList = mutableListOf<String>()
      val tagsArray = json.optJSONArray("tags")
      if (tagsArray != null) {
        for (i in 0 until tagsArray.length()) {
          tagsList.add(tagsArray.getString(i))
        }
      }

      return JournalEntryModel(
        id = json.optString("id", UUID.randomUUID().toString()),
        title = json.optString("title", ""),
        content = json.optString("content", ""),
        dateEpochDay = json.optLong("dateEpochDay", LocalDate.now().toEpochDay()),
        associatedStoryId = if (json.has("associatedStoryId") && !json.isNull("associatedStoryId")) {
          json.optString("associatedStoryId")
        } else null,
        tags = tagsList,
        iconKey = json.optString("iconKey", "favorite"),
        moodAccent = json.optString("moodAccent", "rosewood"),
        photoUri = if (json.has("photoUri") && !json.isNull("photoUri")) {
          json.optString("photoUri")
        } else null,
        isFavorite = json.optBoolean("isFavorite", false),
        createdAtEpochMillis = json.optLong("createdAtEpochMillis", System.currentTimeMillis()),
        updatedAtEpochMillis = json.optLong("updatedAtEpochMillis", System.currentTimeMillis())
      )
    }
  }
}
