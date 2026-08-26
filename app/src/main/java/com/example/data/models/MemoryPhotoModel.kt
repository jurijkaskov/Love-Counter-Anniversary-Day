package com.example.data.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

data class MemoryPhotoModel(
  val id: String = UUID.randomUUID().toString(),
  val filePath: String = "",
  val dateEpochDay: Long = LocalDate.now().toEpochDay(),
  val caption: String = "",
  val associatedStoryId: String? = null,
  val associatedJournalId: String? = null,
  val isFavorite: Boolean = false,
  val tags: List<String> = emptyList(),
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

  val displayTitle: String
    get() = if (caption.isNotBlank()) caption else formattedDate

  fun toJson(): JSONObject {
    val json = JSONObject()
    json.put("id", id)
    json.put("filePath", filePath)
    json.put("dateEpochDay", dateEpochDay)
    if (associatedStoryId != null) {
      json.put("associatedStoryId", associatedStoryId)
    }
    if (associatedJournalId != null) {
      json.put("associatedJournalId", associatedJournalId)
    }
    json.put("isFavorite", isFavorite)
    val tagsArray = JSONArray()
    tags.forEach { tagsArray.put(it) }
    json.put("tags", tagsArray)
    json.put("caption", caption)
    json.put("createdAtEpochMillis", createdAtEpochMillis)
    json.put("updatedAtEpochMillis", updatedAtEpochMillis)
    return json
  }

  companion object {
    fun fromJson(json: JSONObject): MemoryPhotoModel {
      val tagsList = mutableListOf<String>()
      val tagsArray = json.optJSONArray("tags")
      if (tagsArray != null) {
        for (i in 0 until tagsArray.length()) {
          tagsList.add(tagsArray.getString(i))
        }
      }

      return MemoryPhotoModel(
        id = json.optString("id", UUID.randomUUID().toString()),
        filePath = json.optString("filePath", ""),
        dateEpochDay = json.optLong("dateEpochDay", LocalDate.now().toEpochDay()),
        caption = json.optString("caption", ""),
        associatedStoryId = if (json.has("associatedStoryId") && !json.isNull("associatedStoryId")) {
          json.optString("associatedStoryId")
        } else null,
        associatedJournalId = if (json.has("associatedJournalId") && !json.isNull("associatedJournalId")) {
          json.optString("associatedJournalId")
        } else null,
        isFavorite = json.optBoolean("isFavorite", false),
        tags = tagsList,
        createdAtEpochMillis = json.optLong("createdAtEpochMillis", System.currentTimeMillis()),
        updatedAtEpochMillis = json.optLong("updatedAtEpochMillis", System.currentTimeMillis())
      )
    }
  }
}
