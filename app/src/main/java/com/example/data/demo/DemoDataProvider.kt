package com.example.data.demo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import com.example.data.JournalRepository
import com.example.data.MilestoneRepository
import com.example.data.OnboardingManager
import com.example.data.PhotoRepository
import com.example.data.StoryRepository
import com.example.data.models.EventCategory
import com.example.data.models.JournalEntryModel
import com.example.data.models.MemoryPhotoModel
import com.example.data.models.MilestoneCategory
import com.example.data.models.MilestoneModel
import com.example.data.models.MilestoneTaskModel
import com.example.data.models.ReminderConfig
import com.example.data.models.StoryModel
import com.example.ui.widget.WidgetUpdateHelper
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.UUID

/**
 * DemoDataProvider
 *
 * Populates realistic, dynamic relationship demo data when `DemoConfig.USE_DEMO_DATA = true`.
 * Keeps all demo generation isolated and cleanly removable.
 */
object DemoDataProvider {
  private const val PREFS_NAME = "demo_data_internal_prefs"
  private const val KEY_SEEDED = "key_demo_data_seeded_v1"

  fun populateIfEnabled(
    context: Context,
    storyRepository: StoryRepository,
    milestoneRepository: MilestoneRepository,
    journalRepository: JournalRepository,
    photoRepository: PhotoRepository,
    onboardingManager: OnboardingManager,
    force: Boolean = false
  ) {
    if (!DemoConfig.USE_DEMO_DATA) return

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val alreadySeeded = prefs.getBoolean(KEY_SEEDED, false)

    // Only populate on first launch when storage is empty to prevent duplicates
    if (!force && (alreadySeeded || storyRepository.hasAnyStory())) {
      return
    }

    val today = LocalDate.now()

    // 1. Mark onboarding completed so all main screens display immediately
    onboardingManager.hasCompletedOnboarding = true

    // 2. Populate Stories / Anniversaries / Counters
    val primaryStoryId = "demo-primary-story"
    val weddingStoryId = "demo-wedding-story"
    val birthdayStoryId = "demo-birthday-story"
    val firstDateStoryId = "demo-first-date-story"
    val weMetStoryId = "demo-we-met-story"
    val tripStoryId = "demo-trip-story"
    val engagementStoryId = "demo-engagement-story"
    val homeStoryId = "demo-home-story"
    val dinnerStoryId = "demo-dinner-story"
    val cabinStoryId = "demo-cabin-story"
    val voyageStoryId = "demo-voyage-story"

    val demoStories = listOf(
      StoryModel(
        id = primaryStoryId,
        category = EventCategory.RELATIONSHIP,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Eleanor & Julian",
        dateEpochDay = today.minusDays(1253).toEpochDay(),
        note = "Every day with you is my favorite day.",
        iconKey = "favorite",
        themeAccent = "rosewood",
        isPrimary = true,
        isFavorite = true,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.RELATIONSHIP)
      ),
      StoryModel(
        id = weddingStoryId,
        category = EventCategory.WEDDING,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "3rd Wedding Anniversary",
        dateEpochDay = today.plusDays(156).toEpochDay(),
        note = "Celebrating our sacred vows and lifelong devotion.",
        iconKey = "celebration",
        themeAccent = "gold",
        isPrimary = false,
        isFavorite = true,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.WEDDING)
      ),
      StoryModel(
        id = birthdayStoryId,
        category = EventCategory.BIRTHDAY,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Her Birthday",
        dateEpochDay = today.plusDays(56).toEpochDay(),
        note = "A special celebration for the most wonderful person.",
        iconKey = "cake",
        themeAccent = "rosewood",
        isPrimary = false,
        isFavorite = true,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.BIRTHDAY)
      ),
      StoryModel(
        id = dinnerStoryId,
        category = EventCategory.SPECIAL_DAY,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Candlelight Date Night",
        dateEpochDay = today.plusDays(4).toEpochDay(),
        note = "Table reserved at our favorite rooftop bistro.",
        iconKey = "dinner_dining",
        themeAccent = "blush",
        isPrimary = false,
        isFavorite = false,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.SPECIAL_DAY)
      ),
      StoryModel(
        id = cabinStoryId,
        category = EventCategory.TRIP,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Weekend Mountain Getaway",
        dateEpochDay = today.plusDays(18).toEpochDay(),
        note = "Cozy cabin retreat, morning pine air, and scenic hiking trails.",
        iconKey = "nature",
        themeAccent = "gold",
        isPrimary = false,
        isFavorite = false,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.TRIP)
      ),
      StoryModel(
        id = firstDateStoryId,
        category = EventCategory.FIRST_DATE,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "First Date",
        dateEpochDay = today.minusDays(1248).toEpochDay(),
        note = "Coffee at the cozy corner café that changed everything.",
        iconKey = "favorite_border",
        themeAccent = "blush",
        isPrimary = false,
        isFavorite = true,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.FIRST_DATE)
      ),
      StoryModel(
        id = weMetStoryId,
        category = EventCategory.FIRST_DATE,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "We Met",
        dateEpochDay = today.minusDays(1253).toEpochDay(),
        note = "The unforgettable day our paths crossed in the bookshop.",
        iconKey = "favorite",
        themeAccent = "rosewood",
        isPrimary = false,
        isFavorite = false,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.FIRST_DATE)
      ),
      StoryModel(
        id = engagementStoryId,
        category = EventCategory.ENGAGEMENT,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Engagement",
        dateEpochDay = today.minusDays(540).toEpochDay(),
        note = "A magical evening under the stars and an ecstatic YES.",
        iconKey = "ring",
        themeAccent = "blush",
        isPrimary = false,
        isFavorite = true,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.ENGAGEMENT)
      ),
      StoryModel(
        id = homeStoryId,
        category = EventCategory.SPECIAL_DAY,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Moving into Our First Home",
        dateEpochDay = today.minusDays(320).toEpochDay(),
        note = "Unpacking boxes, pizza on the floor, making a life together.",
        iconKey = "home",
        themeAccent = "rosewood",
        isPrimary = false,
        isFavorite = true,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.SPECIAL_DAY)
      ),
      StoryModel(
        id = tripStoryId,
        category = EventCategory.TRIP,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "First Trip to Paris & Amalfi",
        dateEpochDay = today.minusDays(980).toEpochDay(),
        note = "Exploring cobblestone streets, gelato at midnight, and Amalfi sunsets.",
        iconKey = "flight",
        themeAccent = "gold",
        isPrimary = false,
        isFavorite = false,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.TRIP)
      ),
      StoryModel(
        id = voyageStoryId,
        category = EventCategory.TRIP,
        yourName = "Eleanor",
        partnerName = "Julian",
        title = "Romantic Summer Voyage",
        dateEpochDay = today.plusDays(94).toEpochDay(),
        note = "Sailing along the crystal-blue Mediterranean coast.",
        iconKey = "flight",
        themeAccent = "gold",
        isPrimary = false,
        isFavorite = false,
        reminderConfig = ReminderConfig.defaultForCategory(EventCategory.TRIP)
      )
    )

    demoStories.forEach { storyRepository.saveStory(it) }

    // 3. Populate Milestones & Checklist Tasks
    val weddingMilestoneId = "demo-milestone-wedding"
    val thousandDaysMilestoneId = "demo-milestone-1000days"
    val bucketListMilestoneId = "demo-milestone-bucketlist"
    val homeMilestoneId = "demo-milestone-home"

    val weddingMilestone = MilestoneModel(
      id = weddingMilestoneId,
      title = "Wedding Preparation",
      category = MilestoneCategory.WEDDING,
      description = "Preparing for our dream ceremony and celebration with family & friends.",
      targetDateEpochDay = today.plusDays(156).toEpochDay(),
      associatedStoryId = weddingStoryId,
      iconKey = "ring",
      createdAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 30)
    )

    val weddingTasks = listOf(
      MilestoneTaskModel(
        id = "wtask-1",
        milestoneId = weddingMilestoneId,
        title = "Book the ceremony venue",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(60).toEpochDay(),
        orderIndex = 1,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 60)
      ),
      MilestoneTaskModel(
        id = "wtask-2",
        milestoneId = weddingMilestoneId,
        title = "Establish the wedding budget",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(30).toEpochDay(),
        orderIndex = 2,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 30)
      ),
      MilestoneTaskModel(
        id = "wtask-3",
        milestoneId = weddingMilestoneId,
        title = "Choose rings & wedding attire",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(15).toEpochDay(),
        orderIndex = 3,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 15)
      ),
      MilestoneTaskModel(
        id = "wtask-4",
        milestoneId = weddingMilestoneId,
        title = "Send invitations & save-the-dates",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(5).toEpochDay(),
        orderIndex = 4,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 5)
      ),
      MilestoneTaskModel(
        id = "wtask-5",
        milestoneId = weddingMilestoneId,
        title = "Finalize photography & floral arrangements",
        isCompleted = false,
        dueDateEpochDay = today.plusDays(40).toEpochDay(),
        orderIndex = 5
      ),
      MilestoneTaskModel(
        id = "wtask-6",
        milestoneId = weddingMilestoneId,
        title = "Menu tasting & wine pairing dinner",
        isCompleted = false,
        dueDateEpochDay = today.plusDays(70).toEpochDay(),
        orderIndex = 6
      ),
      MilestoneTaskModel(
        id = "wtask-7",
        milestoneId = weddingMilestoneId,
        title = "Plan honeymoon itinerary",
        isCompleted = false,
        dueDateEpochDay = today.plusDays(100).toEpochDay(),
        orderIndex = 7
      )
    )

    val thousandDaysMilestone = MilestoneModel(
      id = thousandDaysMilestoneId,
      title = "1,000 Days Milestone",
      category = MilestoneCategory.ANNIVERSARY,
      description = "Celebrating one thousand days of unconditional love and laughter.",
      targetDateEpochDay = today.minusDays(253).toEpochDay(),
      associatedStoryId = primaryStoryId,
      iconKey = "celebration",
      createdAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 260)
    )

    val thousandDaysTasks = listOf(
      MilestoneTaskModel(
        id = "ttask-1",
        milestoneId = thousandDaysMilestoneId,
        title = "Book secret anniversary dinner",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(260).toEpochDay(),
        orderIndex = 1,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 260)
      ),
      MilestoneTaskModel(
        id = "ttask-2",
        milestoneId = thousandDaysMilestoneId,
        title = "Write 1,000 days love letter",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(253).toEpochDay(),
        orderIndex = 2,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 253)
      ),
      MilestoneTaskModel(
        id = "ttask-3",
        milestoneId = thousandDaysMilestoneId,
        title = "Frame our favorite portrait",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(250).toEpochDay(),
        orderIndex = 3,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 250)
      )
    )

    val bucketListMilestone = MilestoneModel(
      id = bucketListMilestoneId,
      title = "Romance & Bucket List",
      category = MilestoneCategory.TRIP,
      description = "Cherished adventures, dream travels, and shared aspirations.",
      targetDateEpochDay = today.plusDays(94).toEpochDay(),
      associatedStoryId = voyageStoryId,
      iconKey = "flight",
      createdAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 90)
    )

    val bucketListTasks = listOf(
      MilestoneTaskModel(
        id = "btask-1",
        milestoneId = bucketListMilestoneId,
        title = "Sunrise hot air balloon ride",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(85).toEpochDay(),
        orderIndex = 1,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 85)
      ),
      MilestoneTaskModel(
        id = "btask-2",
        milestoneId = bucketListMilestoneId,
        title = "Authentic pasta cooking class in Italy",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(410).toEpochDay(),
        orderIndex = 2,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 410)
      ),
      MilestoneTaskModel(
        id = "btask-3",
        milestoneId = bucketListMilestoneId,
        title = "Stargazing cabin in the mountains",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(180).toEpochDay(),
        orderIndex = 3,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 180)
      ),
      MilestoneTaskModel(
        id = "btask-4",
        milestoneId = bucketListMilestoneId,
        title = "Sunset sailboat cruise along Amalfi coast",
        isCompleted = false,
        dueDateEpochDay = today.plusDays(94).toEpochDay(),
        orderIndex = 4
      ),
      MilestoneTaskModel(
        id = "btask-5",
        milestoneId = bucketListMilestoneId,
        title = "Northern lights winter glass igloo",
        isCompleted = false,
        dueDateEpochDay = today.plusDays(240).toEpochDay(),
        orderIndex = 5
      )
    )

    val homeMilestone = MilestoneModel(
      id = homeMilestoneId,
      title = "Our First Home Journey",
      category = MilestoneCategory.CUSTOM,
      description = "Creating our sanctuary together step by step.",
      targetDateEpochDay = today.minusDays(320).toEpochDay(),
      associatedStoryId = homeStoryId,
      iconKey = "home",
      createdAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 350)
    )

    val homeTasks = listOf(
      MilestoneTaskModel(
        id = "htask-1",
        milestoneId = homeMilestoneId,
        title = "Find our dream sunlit home",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(360).toEpochDay(),
        orderIndex = 1,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 360)
      ),
      MilestoneTaskModel(
        id = "htask-2",
        milestoneId = homeMilestoneId,
        title = "Move-in day celebration pizza",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(320).toEpochDay(),
        orderIndex = 2,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 320)
      ),
      MilestoneTaskModel(
        id = "htask-3",
        milestoneId = homeMilestoneId,
        title = "Build gallery memory photo wall",
        isCompleted = true,
        dueDateEpochDay = today.minusDays(280).toEpochDay(),
        orderIndex = 3,
        completedAtEpochMillis = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 280)
      )
    )

    milestoneRepository.saveMilestone(weddingMilestone)
    weddingTasks.forEach { milestoneRepository.saveTask(it) }

    milestoneRepository.saveMilestone(thousandDaysMilestone)
    thousandDaysTasks.forEach { milestoneRepository.saveTask(it) }

    milestoneRepository.saveMilestone(bucketListMilestone)
    bucketListTasks.forEach { milestoneRepository.saveTask(it) }

    milestoneRepository.saveMilestone(homeMilestone)
    homeTasks.forEach { milestoneRepository.saveTask(it) }

    // 4. Populate Journal & Timeline Entries
    val firstJournalId = "demo-journal-beach-sunset"
    val secondJournalId = "demo-journal-first-coffee"
    val thirdJournalId = "demo-journal-proposal"
    val fourthJournalId = "demo-journal-home-keys"
    val fifthJournalId = "demo-journal-rainy-sunday"
    val sixthJournalId = "demo-journal-surprise-picnic"

    val demoJournalEntries = listOf(
      JournalEntryModel(
        id = firstJournalId,
        title = "Beach Sunset at Golden Hour",
        content = "One of the most beautiful sunsets we've ever seen together. A gentle warm breeze off the ocean, the sky turning into brilliant hues of amber, rose, and gold, and your hand in mine. A truly perfect day that I will cherish forever.",
        dateEpochDay = today.minusDays(40).toEpochDay(),
        associatedStoryId = tripStoryId,
        tags = listOf("Travel", "Sunset", "Romance"),
        iconKey = "favorite",
        moodAccent = "gold",
        isFavorite = true,
        createdAtEpochMillis = System.currentTimeMillis() - 40L * 24 * 60 * 60 * 1000
      ),
      JournalEntryModel(
        id = secondJournalId,
        title = "First Coffee & Endless Laughter",
        content = "We sat in that cozy corner booth for over four hours. Neither of us wanted the conversation to end. I knew right then this was something truly rare and special.",
        dateEpochDay = today.minusDays(1248).toEpochDay(),
        associatedStoryId = firstDateStoryId,
        tags = listOf("First Date", "Memories"),
        iconKey = "favorite_border",
        moodAccent = "blush",
        isFavorite = false,
        createdAtEpochMillis = System.currentTimeMillis() - 1248L * 24 * 60 * 60 * 1000
      ),
      JournalEntryModel(
        id = thirdJournalId,
        title = "The Sunset Proposal",
        content = "Under the lantern-lit cliffside gazebo with the sound of waves below, Julian got down on one knee. An instant, tearful, ecstatic YES. Our next chapter officially begins.",
        dateEpochDay = today.minusDays(540).toEpochDay(),
        associatedStoryId = engagementStoryId,
        tags = listOf("Engagement", "Love Story"),
        iconKey = "ring",
        moodAccent = "rosewood",
        isFavorite = true,
        createdAtEpochMillis = System.currentTimeMillis() - 540L * 24 * 60 * 60 * 1000
      ),
      JournalEntryModel(
        id = fourthJournalId,
        title = "Our First Home Keys",
        content = "Sitting on empty boxes eating warm pizza off paper plates. Looking around our new sunlit living room, feeling endlessly grateful to build our future together.",
        dateEpochDay = today.minusDays(320).toEpochDay(),
        associatedStoryId = homeStoryId,
        tags = listOf("Home", "Milestone"),
        iconKey = "home",
        moodAccent = "gold",
        isFavorite = true,
        createdAtEpochMillis = System.currentTimeMillis() - 320L * 24 * 60 * 60 * 1000
      ),
      JournalEntryModel(
        id = fifthJournalId,
        title = "Quiet Rainy Sunday Morning",
        content = "Soft jazz playing on vinyl, fresh homemade croissants, and rain tapping gently against the window pane. Simple moments together are the richest ones.",
        dateEpochDay = today.minusDays(12).toEpochDay(),
        associatedStoryId = primaryStoryId,
        tags = listOf("Everyday", "Cozy"),
        iconKey = "favorite",
        moodAccent = "blush",
        isFavorite = false,
        createdAtEpochMillis = System.currentTimeMillis() - 12L * 24 * 60 * 60 * 1000
      ),
      JournalEntryModel(
        id = sixthJournalId,
        title = "Surprise Anniversary Picnic",
        content = "Julian organized a surprise picnic in the botanical garden with a hand-curated album of our favorite memories. My heart has never felt fuller.",
        dateEpochDay = today.minusDays(365).toEpochDay(),
        associatedStoryId = primaryStoryId,
        tags = listOf("Anniversary", "Surprise"),
        iconKey = "celebration",
        moodAccent = "rosewood",
        isFavorite = true,
        createdAtEpochMillis = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000
      )
    )

    demoJournalEntries.forEach { journalRepository.saveEntry(it) }

    // 5. Populate Realistic Memory Photos
    populateDemoPhotos(context, photoRepository, primaryStoryId, tripStoryId, firstJournalId, thirdJournalId, today)

    // 6. Sync Widgets & mark demo data as successfully seeded
    WidgetUpdateHelper.updateAllWidgets(context)
    prefs.edit().putBoolean(KEY_SEEDED, true).apply()
  }

  private fun populateDemoPhotos(
    context: Context,
    photoRepository: PhotoRepository,
    primaryStoryId: String,
    tripStoryId: String,
    firstJournalId: String,
    thirdJournalId: String,
    today: LocalDate
  ) {
    val photosDir = File(context.filesDir, "memory_photos").apply { if (!exists()) mkdirs() }

    val sampleConfigs = listOf(
      DemoPhotoConfig(
        title = "Beach Sunset Walk",
        caption = "One of the most beautiful sunsets we've ever seen together. A perfect day.",
        dateEpochDay = today.minusDays(40).toEpochDay(),
        storyId = tripStoryId,
        journalId = firstJournalId,
        isFavorite = true,
        gradientTop = Color.parseColor("#E65C40"),
        gradientMid = Color.parseColor("#E29578"),
        gradientBottom = Color.parseColor("#2E1F27"),
        sunColor = Color.parseColor("#FFF3B0")
      ),
      DemoPhotoConfig(
        title = "Starlit Evening",
        caption = "Under the quiet stars, talking for hours about our future dreams.",
        dateEpochDay = today.minusDays(540).toEpochDay(),
        storyId = primaryStoryId,
        journalId = thirdJournalId,
        isFavorite = true,
        gradientTop = Color.parseColor("#1B1A2F"),
        gradientMid = Color.parseColor("#3F2B48"),
        gradientBottom = Color.parseColor("#7A4E65"),
        sunColor = Color.parseColor("#FFD166")
      ),
      DemoPhotoConfig(
        title = "Warm Coffee Morning",
        caption = "Morning conversation over warm vanilla lattes in our favorite corner.",
        dateEpochDay = today.minusDays(1248).toEpochDay(),
        storyId = primaryStoryId,
        journalId = null,
        isFavorite = false,
        gradientTop = Color.parseColor("#C89B7B"),
        gradientMid = Color.parseColor("#E6CCB2"),
        gradientBottom = Color.parseColor("#7F5539"),
        sunColor = Color.parseColor("#EDE0D4")
      ),
      DemoPhotoConfig(
        title = "Mountain Horizon",
        caption = "Clear golden morning hiking up the scenic peak together.",
        dateEpochDay = today.minusDays(180).toEpochDay(),
        storyId = null,
        journalId = null,
        isFavorite = true,
        gradientTop = Color.parseColor("#4A6B82"),
        gradientMid = Color.parseColor("#D4A373"),
        gradientBottom = Color.parseColor("#283618"),
        sunColor = Color.parseColor("#FAEDCD")
      )
    )

    sampleConfigs.forEach { config ->
      try {
        val fileName = "demo_photo_${UUID.randomUUID()}.jpg"
        val destFile = File(photosDir, fileName)
        val bitmap = createArtisticMemoryBitmap(config)
        FileOutputStream(destFile).use { out ->
          bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }

        val photo = MemoryPhotoModel(
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
        photoRepository.savePhoto(photo)
      } catch (_: Exception) {}
    }
  }

  private fun createArtisticMemoryBitmap(config: DemoPhotoConfig): Bitmap {
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

  private data class DemoPhotoConfig(
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
