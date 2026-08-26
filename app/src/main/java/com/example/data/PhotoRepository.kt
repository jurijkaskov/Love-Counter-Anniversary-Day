package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.net.Uri
import com.example.data.models.MemoryPhotoModel
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray

class PhotoRepository(private val context: Context) {
  private val prefs = context.getSharedPreferences("cherish_photos_prefs", Context.MODE_PRIVATE)
  private val _photos = MutableStateFlow<List<MemoryPhotoModel>>(emptyList())
  val photos: StateFlow<List<MemoryPhotoModel>> = _photos.asStateFlow()

  private val photosDir: File
    get() = File(context.filesDir, "memory_photos").apply { if (!exists()) mkdirs() }

  init {
    loadPhotos()
  }

  private fun loadPhotos() {
    val jsonString = prefs.getString("photos_json", null)
    if (jsonString != null) {
      try {
        val array = JSONArray(jsonString)
        val list = mutableListOf<MemoryPhotoModel>()
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          list.add(MemoryPhotoModel.fromJson(obj))
        }
        _photos.value = list
      } catch (e: Exception) {
        _photos.value = emptyList()
      }
    } else {
      _photos.value = emptyList()
    }
  }

  private fun persistPhotos(list: List<MemoryPhotoModel>) {
    _photos.value = list
    val array = JSONArray()
    list.forEach { array.put(it.toJson()) }
    prefs.edit().putString("photos_json", array.toString()).apply()
  }

  fun savePhoto(photo: MemoryPhotoModel) {
    val current = _photos.value.toMutableList()
    val index = current.indexOfFirst { it.id == photo.id }
    if (index >= 0) {
      current[index] = photo.copy(updatedAtEpochMillis = System.currentTimeMillis())
    } else {
      current.add(0, photo)
    }
    persistPhotos(current)
  }

  fun importPhotoFromUri(
    uri: Uri,
    dateEpochDay: Long = LocalDate.now().toEpochDay(),
    caption: String = "",
    storyId: String? = null,
    journalId: String? = null
  ): MemoryPhotoModel? {
    return try {
      val fileName = "photo_${UUID.randomUUID()}.jpg"
      val destinationFile = File(photosDir, fileName)

      context.contentResolver.openInputStream(uri)?.use { inputStream ->
        FileOutputStream(destinationFile).use { outputStream ->
          inputStream.copyTo(outputStream)
        }
      }

      val newPhoto = MemoryPhotoModel(
        id = UUID.randomUUID().toString(),
        filePath = fileName,
        dateEpochDay = dateEpochDay,
        caption = caption,
        associatedStoryId = storyId,
        associatedJournalId = journalId,
        isFavorite = false,
        createdAtEpochMillis = System.currentTimeMillis()
      )

      savePhoto(newPhoto)
      newPhoto
    } catch (e: Exception) {
      null
    }
  }

  fun importMultiplePhotos(
    uris: List<Uri>,
    defaultDateEpochDay: Long = LocalDate.now().toEpochDay(),
    storyId: String? = null,
    journalId: String? = null
  ): List<MemoryPhotoModel> {
    val saved = mutableListOf<MemoryPhotoModel>()
    uris.forEach { uri ->
      importPhotoFromUri(
        uri = uri,
        dateEpochDay = defaultDateEpochDay,
        caption = "",
        storyId = storyId,
        journalId = journalId
      )?.let {
        saved.add(it)
      }
    }
    return saved
  }

  fun deletePhoto(id: String) {
    val current = _photos.value.toMutableList()
    val toDelete = current.find { it.id == id }
    if (toDelete != null) {
      // Delete internal file safely
      try {
        val file = File(photosDir, toDelete.filePath)
        if (file.exists()) {
          file.delete()
        }
      } catch (_: Exception) {}

      current.removeAll { it.id == id }
      persistPhotos(current)
    }
  }

  fun toggleFavorite(id: String) {
    val current = _photos.value.map {
      if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
    }
    persistPhotos(current)
  }

  fun getPhotosForStory(storyId: String): List<MemoryPhotoModel> {
    return _photos.value.filter { it.associatedStoryId == storyId }
  }

  fun getPhotosForJournal(journalId: String): List<MemoryPhotoModel> {
    return _photos.value.filter { it.associatedJournalId == journalId }
  }

  fun unbindStory(storyId: String) {
    val current = _photos.value.map {
      if (it.associatedStoryId == storyId) it.copy(associatedStoryId = null) else it
    }
    persistPhotos(current)
  }

  fun unbindJournal(journalId: String) {
    val current = _photos.value.map {
      if (it.associatedJournalId == journalId) it.copy(associatedJournalId = null) else it
    }
    persistPhotos(current)
  }

  fun getPhotoFile(photo: MemoryPhotoModel): File {
    return File(photosDir, photo.filePath)
  }

  fun clearAll() {
    try {
      photosDir.listFiles()?.forEach { it.delete() }
    } catch (_: Exception) {}
    prefs.edit().clear().apply()
    _photos.value = emptyList()
  }

  fun seedDefaultPhotosIfEmpty(storyRepository: StoryRepository, journalRepository: JournalRepository) {
    if (_photos.value.isNotEmpty()) return

    val stories = storyRepository.stories.value
    val journalEntries = journalRepository.entries.value

    val primaryStory = stories.firstOrNull()
    val firstJournal = journalEntries.firstOrNull()

    val seededList = mutableListOf<MemoryPhotoModel>()

    // Create 3 sample artful romantic memory photos saved directly as files
    val sampleConfigs = listOf(
      SamplePhotoConfig(
        title = "Beach Sunset Walk",
        caption = "One of the most beautiful sunsets we've ever seen together. A perfect day.",
        dateEpochDay = primaryStory?.dateEpochDay ?: LocalDate.now().minusMonths(6).toEpochDay(),
        storyId = primaryStory?.id,
        journalId = firstJournal?.id,
        isFavorite = true,
        gradientTop = Color.parseColor("#E65C40"),
        gradientMid = Color.parseColor("#E29578"),
        gradientBottom = Color.parseColor("#2E1F27"),
        sunColor = Color.parseColor("#FFF3B0")
      ),
      SamplePhotoConfig(
        title = "Starlit Evening",
        caption = "Under the quiet city stars, just talking for hours about our future dreams.",
        dateEpochDay = LocalDate.now().minusMonths(3).toEpochDay(),
        storyId = primaryStory?.id,
        journalId = null,
        isFavorite = true,
        gradientTop = Color.parseColor("#1B1A2F"),
        gradientMid = Color.parseColor("#3F2B48"),
        gradientBottom = Color.parseColor("#7A4E65"),
        sunColor = Color.parseColor("#FFD166")
      ),
      SamplePhotoConfig(
        title = "First Coffee Together",
        caption = "The morning we decided to plan our next big trip together over warm vanilla lattes.",
        dateEpochDay = LocalDate.now().minusMonths(1).toEpochDay(),
        storyId = null,
        journalId = journalEntries.getOrNull(2)?.id,
        isFavorite = false,
        gradientTop = Color.parseColor("#C89B7B"),
        gradientMid = Color.parseColor("#E6CCB2"),
        gradientBottom = Color.parseColor("#7F5539"),
        sunColor = Color.parseColor("#EDE0D4")
      )
    )

    sampleConfigs.forEach { config ->
      try {
        val fileName = "sample_${UUID.randomUUID()}.jpg"
        val destFile = File(photosDir, fileName)
        val bitmap = createArtisticMemoryBitmap(config)
        FileOutputStream(destFile).use { out ->
          bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }

        seededList.add(
          MemoryPhotoModel(
            id = UUID.randomUUID().toString(),
            filePath = fileName,
            dateEpochDay = config.dateEpochDay,
            caption = config.caption,
            associatedStoryId = config.storyId,
            associatedJournalId = config.journalId,
            isFavorite = config.isFavorite,
            tags = listOf("Cherished", "Memories"),
            createdAtEpochMillis = System.currentTimeMillis()
          )
        )
      } catch (_: Exception) {}
    }

    if (seededList.isNotEmpty()) {
      persistPhotos(seededList)
    }
  }

  private fun createArtisticMemoryBitmap(config: SamplePhotoConfig): Bitmap {
    val width = 800
    val height = 800
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Background Gradient
    val shader = LinearGradient(
      0f, 0f, 0f, height.toFloat(),
      intArrayOf(config.gradientTop, config.gradientMid, config.gradientBottom),
      floatArrayOf(0f, 0.55f, 1f),
      Shader.TileMode.CLAMP
    )
    paint.shader = shader
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

    // Sun / Moon / Ambient glow
    paint.shader = RadialGradient(
      width * 0.5f, height * 0.45f, width * 0.35f,
      intArrayOf(config.sunColor, Color.TRANSPARENT),
      floatArrayOf(0f, 1f),
      Shader.TileMode.CLAMP
    )
    canvas.drawCircle(width * 0.5f, height * 0.45f, width * 0.35f, paint)

    // Soft horizon & romantic silhouette hills/waves
    paint.shader = null
    paint.color = config.gradientBottom
    val horizonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.argb(160, 30, 20, 25)
      style = Paint.Style.FILL
    }
    canvas.drawOval(
      -width * 0.2f, height * 0.72f, width * 1.2f, height * 1.3f,
      horizonPaint
    )

    // Silhouette foreground
    val forePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.argb(220, 20, 15, 20)
      style = Paint.Style.FILL
    }
    canvas.drawOval(
      width * 0.1f, height * 0.82f, width * 1.4f, height * 1.4f,
      forePaint
    )

    return bitmap
  }

  private data class SamplePhotoConfig(
    val title: String,
    val caption: String,
    val dateEpochDay: Long,
    val storyId: String?,
    val journalId: String?,
    val isFavorite: Boolean,
    val gradientTop: Int,
    val gradientMid: Int,
    val gradientBottom: Int,
    val sunColor: Int
  )
}
