package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.data.models.EventCategory
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.ShareCardConfig
import com.example.data.models.ShareCardFormat
import com.example.data.models.ShareCardStyle
import com.example.data.models.ShareCardType
import com.example.data.models.StoryModel
import com.example.ui.share.ShareCardDialog
import com.example.ui.share.ShareCardPayloadFactory
import com.example.ui.theme.CherishTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ShareCardTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `factory correctly maps story to share card payload`() {
    val story = StoryModel(
      title = "Our Story",
      yourName = "Sophia",
      partnerName = "Liam",
      category = EventCategory.RELATIONSHIP,
      dateEpochDay = LocalDate.now().minusDays(500).toEpochDay(),
      isPrimary = true
    )

    val payload = ShareCardPayloadFactory.fromStory(story)
    assertEquals(ShareCardType.TIME_TOGETHER, payload.cardType)
    assertEquals("Our Story", payload.title)
    assertEquals("500 Days", payload.mainHighlight)
    assertEquals("S & L", payload.initialsOrIcon)
  }

  @Test
  fun `factory correctly maps journal entry to share card payload`() {
    val entry = JournalEntryModel(
      title = "Starlight Stroll",
      content = "Walking along the promenade under the moonlight.",
      dateEpochDay = LocalDate.now().toEpochDay()
    )

    val payload = ShareCardPayloadFactory.fromJournalEntry(entry)
    assertEquals(ShareCardType.MEMORY, payload.cardType)
    assertEquals("Starlight Stroll", payload.title)
    assertNotNull(payload.quoteOrNote)
  }

  @Test
  fun `factory correctly maps memory photo to share card payload`() {
    val photo = MemoryPhotoModel(
      filePath = "test_photo.jpg",
      caption = "Sunset at the pier",
      dateEpochDay = LocalDate.now().toEpochDay()
    )

    val payload = ShareCardPayloadFactory.fromPhoto(photo)
    assertEquals(ShareCardType.MEMORY, payload.cardType)
    assertEquals("test_photo.jpg", payload.photoPath)
    assertEquals("Sunset at the pier", payload.quoteOrNote)
  }

  @Test
  fun `share card dialog displays preview and customization options`() {
    val story = StoryModel(
      title = "Elena & Lucas",
      yourName = "Elena",
      partnerName = "Lucas",
      category = EventCategory.RELATIONSHIP,
      dateEpochDay = LocalDate.now().minusDays(365).toEpochDay()
    )
    val payload = ShareCardPayloadFactory.fromStory(story)

    composeTestRule.setContent {
      CherishTheme {
        ShareCardDialog(
          payload = payload,
          onDismiss = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("share_card_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("live_card_preview").assertIsDisplayed()
    composeTestRule.onNodeWithTag("btn_share_card_image").assertExists()
    composeTestRule.onNodeWithTag("btn_save_card_image").assertExists()
  }
}
