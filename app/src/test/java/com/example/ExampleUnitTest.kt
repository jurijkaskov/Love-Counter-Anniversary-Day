package com.example

import com.example.data.models.EventCategory
import com.example.data.models.StoryModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `accurate calendar period and breakdown calculations`() {
    val threeYearsAgo = LocalDate.now().minusYears(3).minusMonths(5).minusDays(18)
    val story = StoryModel(
      title = "Our Relationship",
      dateEpochDay = threeYearsAgo.toEpochDay()
    )

    assertEquals(3, story.exactYears)
    assertEquals(5, story.exactMonths)
    assertEquals(18, story.exactDays)
    assertTrue(story.formattedPeriodBreakdown.contains("3 years"))
    assertTrue(story.formattedPeriodBreakdown.contains("5 months"))
    assertTrue(story.formattedPeriodBreakdown.contains("18 days"))
    assertTrue(story.totalDays > 1000)
    assertTrue(story.totalWeeks > 150)
    assertTrue(story.totalMonths >= 41)
  }

  @Test
  fun `next anniversary calculation computes correct upcoming date and years`() {
    // Relationship started 2 years and 100 days ago
    val startDate = LocalDate.now().minusYears(2).minusDays(100)
    val story = StoryModel(
      title = "Eleanor & Julian",
      dateEpochDay = startDate.toEpochDay()
    )

    assertTrue(story.daysUntilNextAnniversary > 0)
    assertTrue(story.daysUntilNextAnniversary <= 366)
    assertEquals(3, story.nextAnniversaryYears)
    assertEquals("3rd Anniversary", story.nextAnniversaryTitle)
  }

  @Test
  fun `display initials extraction`() {
    val story = StoryModel(
      yourName = "Eleanor",
      partnerName = "Julian"
    )
    assertEquals("E & J", story.displayInitials)
    assertEquals("Eleanor & Julian", story.displayTitle)
  }
}
