package com.example.data.models

enum class ShareCardType {
  TIME_TOGETHER,
  ANNIVERSARY,
  COUNTDOWN,
  MILESTONE,
  MEMORY
}

enum class ShareCardStyle {
  CLASSIC,
  MIDNIGHT,
  ROSE,
  MINIMAL
}

enum class ShareCardFormat(val aspectRatio: Float, val label: String) {
  PORTRAIT_9_16(9f / 16f, "Story (9:16)"),
  SQUARE_1_1(1f / 1f, "Post (1:1)")
}

data class ShareCardPayload(
  val cardType: ShareCardType,
  val title: String,
  val subtitle: String? = null,
  val mainHighlight: String,
  val supportingText: String? = null,
  val dateString: String? = null,
  val quoteOrNote: String? = null,
  val photoPath: String? = null,
  val initialsOrIcon: String? = null,
  val watermarkText: String = "Love Counter: Anniversary Day",
  val sourceStoryId: String? = null,
  val sourceMilestoneId: String? = null,
  val sourceJournalId: String? = null,
  val sourcePhotoId: String? = null
)

data class ShareCardConfig(
  val style: ShareCardStyle = ShareCardStyle.CLASSIC,
  val format: ShareCardFormat = ShareCardFormat.PORTRAIT_9_16,
  val customMessage: String = "",
  val showQuote: Boolean = true,
  val showBreakdown: Boolean = true,
  val showDate: Boolean = true,
  val showWatermark: Boolean = true,
  val usePhotoBackground: Boolean = true
)
