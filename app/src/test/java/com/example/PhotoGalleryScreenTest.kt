package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.JournalRepository
import com.example.data.MilestoneRepository
import com.example.data.PhotoRepository
import com.example.data.PreferencesManager
import com.example.data.StoryRepository
import com.example.data.models.MemoryPhotoModel
import com.example.ui.screens.journal.PhotoGalleryView
import com.example.ui.theme.CherishTheme
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PhotoGalleryScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var photoRepository: PhotoRepository
  private lateinit var storyRepository: StoryRepository
  private lateinit var journalRepository: JournalRepository

  @Before
  fun setUp() {
    val context: Context = ApplicationProvider.getApplicationContext()
    photoRepository = PhotoRepository(context)
    storyRepository = StoryRepository(context)
    journalRepository = JournalRepository(context)
    photoRepository.clearAll()
    storyRepository.resetAll()
    journalRepository.clearAll()
  }

  @Test
  fun `photo repository saves, updates, and deletes photo metadata`() {
    val photo = MemoryPhotoModel(
      id = "test-photo-1",
      filePath = "sample.jpg",
      dateEpochDay = LocalDate.now().toEpochDay(),
      caption = "Sunset at the beach",
      associatedStoryId = null,
      associatedJournalId = null,
      isFavorite = false
    )

    photoRepository.savePhoto(photo)
    assertEquals(1, photoRepository.photos.value.size)
    assertEquals("Sunset at the beach", photoRepository.photos.value[0].caption)

    // Toggle favorite
    photoRepository.toggleFavorite("test-photo-1")
    assertTrue(photoRepository.photos.value[0].isFavorite)

    // Delete photo
    photoRepository.deletePhoto("test-photo-1")
    assertEquals(0, photoRepository.photos.value.size)
  }

  @Test
  fun `photo gallery renders empty state when no photos are saved`() {
    composeTestRule.setContent {
      CherishTheme {
        PhotoGalleryView(
          photos = emptyList(),
          onPhotoClick = {},
          onAddPhotoClick = {},
          onToggleFavorite = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("photo_gallery_view").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_all_photos").assertIsDisplayed()
    composeTestRule.onNodeWithTag("filter_fav_photos").assertIsDisplayed()
  }

  @Test
  fun `photo gallery renders photos in grid and handles filter selection`() {
    val photos = listOf(
      MemoryPhotoModel(
        id = "photo-1",
        filePath = "p1.jpg",
        dateEpochDay = LocalDate.now().toEpochDay(),
        caption = "Our trip to Kyoto",
        isFavorite = true
      ),
      MemoryPhotoModel(
        id = "photo-2",
        filePath = "p2.jpg",
        dateEpochDay = LocalDate.now().minusDays(10).toEpochDay(),
        caption = "Coffee date",
        isFavorite = false
      )
    )

    composeTestRule.setContent {
      CherishTheme {
        PhotoGalleryView(
          photos = photos,
          onPhotoClick = {},
          onAddPhotoClick = {},
          onToggleFavorite = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("photo_gallery_view").assertIsDisplayed()
    composeTestRule.onNodeWithTag("memory_photo_card_photo-1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("memory_photo_card_photo-2").assertIsDisplayed()

    // Switch to favorites
    composeTestRule.onNodeWithTag("filter_fav_photos").performClick()
    composeTestRule.onNodeWithTag("memory_photo_card_photo-1").assertIsDisplayed()
  }
}
