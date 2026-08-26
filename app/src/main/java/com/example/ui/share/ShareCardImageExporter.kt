package com.example.ui.share

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.models.ShareCardConfig
import com.example.data.models.ShareCardFormat
import com.example.data.models.ShareCardPayload
import com.example.data.models.ShareCardStyle
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ShareCardImageExporter {

  /**
   * Generates a high-resolution Bitmap for the given payload and configuration.
   */
  fun generateCardBitmap(
    context: Context,
    payload: ShareCardPayload,
    config: ShareCardConfig
  ): Bitmap {
    val width = 1080
    val height = when (config.format) {
      ShareCardFormat.PORTRAIT_9_16 -> 1920
      ShareCardFormat.SQUARE_1_1 -> 1080
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawShareCard(canvas, width, height, payload, config)
    return bitmap
  }

  private fun drawShareCard(
    canvas: Canvas,
    width: Int,
    height: Int,
    payload: ShareCardPayload,
    config: ShareCardConfig
  ) {
    val isSquare = config.format == ShareCardFormat.SQUARE_1_1

    // 1. Color Palette based on selected style
    val bgColor: Int
    val bgGradientEnd: Int
    val surfaceColor: Int
    val primaryColor: Int
    val accentColor: Int
    val textPrimary: Int
    val textSecondary: Int
    val cardBorderColor: Int
    val quoteBgColor: Int

    when (config.style) {
      ShareCardStyle.CLASSIC -> {
        bgColor = Color.parseColor("#FBF8F4")
        bgGradientEnd = Color.parseColor("#F5EDE2")
        surfaceColor = Color.parseColor("#FFFFFF")
        primaryColor = Color.parseColor("#B8644A") // Rosewood
        accentColor = Color.parseColor("#C99252") // Gold
        textPrimary = Color.parseColor("#28211D")
        textSecondary = Color.parseColor("#756A63")
        cardBorderColor = Color.parseColor("#EFE6DB")
        quoteBgColor = Color.parseColor("#FCEEEA")
      }
      ShareCardStyle.MIDNIGHT -> {
        bgColor = Color.parseColor("#14100E")
        bgGradientEnd = Color.parseColor("#221A16")
        surfaceColor = Color.parseColor("#1E1815")
        primaryColor = Color.parseColor("#E4AD70") // Champagne Gold
        accentColor = Color.parseColor("#DE8C75") // Soft Rose
        textPrimary = Color.parseColor("#F6EFE9")
        textSecondary = Color.parseColor("#B3A79F")
        cardBorderColor = Color.parseColor("#3B312B")
        quoteBgColor = Color.parseColor("#2A201B")
      }
      ShareCardStyle.ROSE -> {
        bgColor = Color.parseColor("#FFF0EC")
        bgGradientEnd = Color.parseColor("#FCE0D8")
        surfaceColor = Color.parseColor("#FFFFFF")
        primaryColor = Color.parseColor("#B8644A")
        accentColor = Color.parseColor("#E89A89")
        textPrimary = Color.parseColor("#2D1D18")
        textSecondary = Color.parseColor("#805C52")
        cardBorderColor = Color.parseColor("#F3D2C9")
        quoteBgColor = Color.parseColor("#FFF5F2")
      }
      ShareCardStyle.MINIMAL -> {
        bgColor = Color.parseColor("#FAFAFA")
        bgGradientEnd = Color.parseColor("#F0F0F2")
        surfaceColor = Color.parseColor("#FFFFFF")
        primaryColor = Color.parseColor("#222222")
        accentColor = Color.parseColor("#888888")
        textPrimary = Color.parseColor("#1A1A1A")
        textSecondary = Color.parseColor("#666666")
        cardBorderColor = Color.parseColor("#E5E5EA")
        quoteBgColor = Color.parseColor("#F2F2F7")
      }
    }

    // 2. Draw canvas background
    val bgPaint = Paint().apply {
      isAntiAlias = true
      shader = LinearGradient(
        0f, 0f, 0f, height.toFloat(),
        bgColor, bgGradientEnd,
        Shader.TileMode.CLAMP
      )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Draw background photo if enabled
    if (config.usePhotoBackground && !payload.photoPath.isNullOrBlank()) {
      try {
        val file = File(payload.photoPath)
        if (file.exists()) {
          val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.ARGB_8888
          }
          val photoBm = BitmapFactory.decodeFile(file.absolutePath, options)
          if (photoBm != null) {
            val photoPaint = Paint(Paint.FILTER_BITMAP_FLAG)
            // Scale and crop photo to fit canvas
            val photoRect = calculateCenterCropRect(photoBm.width, photoBm.height, width, height)
            canvas.drawBitmap(photoBm, photoRect.first, photoRect.second, photoPaint)

            // Scrim overlay so card text pops
            val scrimPaint = Paint().apply {
              isAntiAlias = true
              val scrimTop = if (config.style == ShareCardStyle.MIDNIGHT) Color.argb(180, 20, 16, 14) else Color.argb(190, 251, 248, 244)
              val scrimBottom = if (config.style == ShareCardStyle.MIDNIGHT) Color.argb(230, 20, 16, 14) else Color.argb(240, 251, 248, 244)
              shader = LinearGradient(0f, 0f, 0f, height.toFloat(), scrimTop, scrimBottom, Shader.TileMode.CLAMP)
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
            photoBm.recycle()
          }
        }
      } catch (e: Exception) {
        // Fallback gracefully to standard canvas background
      }
    }

    // 3. Card Frame Insets
    val marginH = if (isSquare) 56f else 64f
    val marginV = if (isSquare) 56f else 96f
    val cardRect = RectF(marginH, marginV, width - marginH, height - marginV)
    val cornerRadius = 48f

    // Inner Card Shadow & Background
    val cardBgPaint = Paint().apply {
      isAntiAlias = true
      color = surfaceColor
      if (config.usePhotoBackground && !payload.photoPath.isNullOrBlank()) {
        alpha = if (config.style == ShareCardStyle.MIDNIGHT) 220 else 240
      }
    }
    canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, cardBgPaint)

    // Inner Card Border
    val borderPaint = Paint().apply {
      isAntiAlias = true
      style = Paint.Style.STROKE
      strokeWidth = 3f
      color = cardBorderColor
    }
    canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, borderPaint)

    // Subtle Double Accent Inner Line
    val innerMargin = 16f
    val innerRect = RectF(
      cardRect.left + innerMargin,
      cardRect.top + innerMargin,
      cardRect.right - innerMargin,
      cardRect.bottom - innerMargin
    )
    val innerBorderPaint = Paint().apply {
      isAntiAlias = true
      style = Paint.Style.STROKE
      strokeWidth = 1.5f
      color = accentColor
      alpha = 70
    }
    canvas.drawRoundRect(innerRect, cornerRadius - 8f, cornerRadius - 8f, innerBorderPaint)

    // 4. Content Drawing Area
    var currentY = cardRect.top + (if (isSquare) 50f else 80f)
    val contentWidth = cardRect.width() - 80f
    val centerX = width / 2f

    // A. Top Badge (Heart & Tag text)
    val badgeSubtitle = payload.subtitle?.uppercase() ?: "CELEBRATE EVERY MOMENT"
    val badgePaint = TextPaint().apply {
      isAntiAlias = true
      typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
      textSize = 28f
      color = primaryColor
      letterSpacing = 0.15f
      textAlign = Paint.Align.CENTER
    }

    val badgeTextWidth = badgePaint.measureText(badgeSubtitle)
    val badgePadH = 36f
    val badgePadV = 16f
    val badgeRect = RectF(
      centerX - (badgeTextWidth / 2f) - badgePadH,
      currentY,
      centerX + (badgeTextWidth / 2f) + badgePadH,
      currentY + 54f
    )

    val badgeBgPaint = Paint().apply {
      isAntiAlias = true
      color = quoteBgColor
    }
    canvas.drawRoundRect(badgeRect, 27f, 27f, badgeBgPaint)

    val badgeBorderPaint = Paint().apply {
      isAntiAlias = true
      style = Paint.Style.STROKE
      strokeWidth = 2f
      color = accentColor
      alpha = 90
    }
    canvas.drawRoundRect(badgeRect, 27f, 27f, badgeBorderPaint)

    canvas.drawText(
      badgeSubtitle,
      centerX,
      badgeRect.centerY() + 10f,
      badgePaint
    )

    currentY += if (isSquare) 90f else 130f

    // B. Main Story / Couple Title
    val titlePaint = TextPaint().apply {
      isAntiAlias = true
      typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
      textSize = if (isSquare) 56f else 68f
      color = textPrimary
      textAlign = Paint.Align.CENTER
    }

    val titleText = payload.title
    canvas.drawText(titleText, centerX, currentY, titlePaint)

    currentY += if (isSquare) 60f else 80f

    // Small Gold Heart Emblem / Divider
    drawHeartIcon(canvas, centerX, currentY, 20f, accentColor)
    currentY += if (isSquare) 50f else 80f

    // C. Big Highlight Metric (e.g. "1,253 Days" or "5 Years")
    val highlightPaint = TextPaint().apply {
      isAntiAlias = true
      typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
      textSize = if (isSquare) 100f else 130f
      color = primaryColor
      textAlign = Paint.Align.CENTER
    }
    canvas.drawText(payload.mainHighlight, centerX, currentY, highlightPaint)

    currentY += if (isSquare) 60f else 80f

    // D. Time Breakdown / Supporting Text (if enabled)
    if (config.showBreakdown && !payload.supportingText.isNullOrBlank()) {
      val supportPaint = TextPaint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        textSize = if (isSquare) 34f else 40f
        color = textSecondary
        textAlign = Paint.Align.CENTER
      }
      canvas.drawText(payload.supportingText, centerX, currentY, supportPaint)
      currentY += if (isSquare) 45f else 60f
    }

    // E. Date String (if enabled)
    if (config.showDate && !payload.dateString.isNullOrBlank()) {
      val datePaint = TextPaint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        textSize = 30f
        color = accentColor
        textAlign = Paint.Align.CENTER
      }
      canvas.drawText(payload.dateString, centerX, currentY, datePaint)
      currentY += if (isSquare) 40f else 60f
    }

    // F. Romantic Love Note or Custom Message (if enabled)
    val displayQuote = if (config.customMessage.isNotBlank()) {
      config.customMessage
    } else if (config.showQuote && !payload.quoteOrNote.isNullOrBlank()) {
      payload.quoteOrNote
    } else null

    if (!displayQuote.isNullOrBlank()) {
      val quoteBoxWidth = contentWidth - 40f
      val quotePaint = TextPaint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        textSize = if (isSquare) 30f else 36f
        color = textPrimary
      }

      val formattedQuote = "“$displayQuote”"
      val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        StaticLayout.Builder.obtain(formattedQuote, 0, formattedQuote.length, quotePaint, quoteBoxWidth.toInt())
          .setAlignment(Layout.Alignment.ALIGN_CENTER)
          .setLineSpacing(8f, 1f)
          .build()
      } else {
        @Suppress("DEPRECATION")
        StaticLayout(formattedQuote, quotePaint, quoteBoxWidth.toInt(), Layout.Alignment.ALIGN_CENTER, 1f, 8f, true)
      }

      val quoteHeight = staticLayout.height.toFloat()
      val quoteRect = RectF(
        centerX - (quoteBoxWidth / 2f) - 24f,
        currentY,
        centerX + (quoteBoxWidth / 2f) + 24f,
        currentY + quoteHeight + 40f
      )

      val quoteContainerPaint = Paint().apply {
        isAntiAlias = true
        color = quoteBgColor
      }
      canvas.drawRoundRect(quoteRect, 24f, 24f, quoteContainerPaint)

      canvas.save()
      canvas.translate(centerX - (quoteBoxWidth / 2f), currentY + 20f)
      staticLayout.draw(canvas)
      canvas.restore()
    }

    // G. Watermark & Branding at Bottom
    if (config.showWatermark) {
      val watermarkY = cardRect.bottom - 44f
      val watermarkPaint = TextPaint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        textSize = 26f
        color = accentColor
        letterSpacing = 0.18f
        textAlign = Paint.Align.CENTER
      }
      canvas.drawText(payload.watermarkText, centerX, watermarkY, watermarkPaint)

      // Tiny heart above watermark
      drawHeartIcon(canvas, centerX, watermarkY - 34f, 10f, accentColor)
    }
  }

  private fun drawHeartIcon(canvas: Canvas, cx: Float, cy: Float, size: Float, color: Int) {
    val heartPaint = Paint().apply {
      isAntiAlias = true
      this.color = color
      style = Paint.Style.FILL
    }
    val path = Path()
    val s = size / 2f
    path.moveTo(cx, cy + s)
    path.cubicTo(cx - s * 1.5f, cy - s * 0.8f, cx - s * 1.5f, cy - s * 2f, cx, cy - s * 0.8f)
    path.cubicTo(cx + s * 1.5f, cy - s * 2f, cx + s * 1.5f, cy - s * 0.8f, cx, cy + s)
    path.close()
    canvas.drawPath(path, heartPaint)
  }

  private fun calculateCenterCropRect(
    srcWidth: Int,
    srcHeight: Int,
    dstWidth: Int,
    dstHeight: Int
  ): Pair<android.graphics.Rect, RectF> {
    val srcAspect = srcWidth.toFloat() / srcHeight.toFloat()
    val dstAspect = dstWidth.toFloat() / dstHeight.toFloat()

    val cropW: Int
    val cropH: Int
    if (srcAspect > dstAspect) {
      cropH = srcHeight
      cropW = (srcHeight * dstAspect).toInt()
    } else {
      cropW = srcWidth
      cropH = (srcWidth / dstAspect).toInt()
    }

    val cropLeft = (srcWidth - cropW) / 2
    val cropTop = (srcHeight - cropH) / 2
    val srcRect = android.graphics.Rect(cropLeft, cropTop, cropLeft + cropW, cropTop + cropH)
    val dstRect = RectF(0f, 0f, dstWidth.toFloat(), dstHeight.toFloat())

    return Pair(srcRect, dstRect)
  }

  /**
   * Shares the generated card image using the system sharing intent via FileProvider.
   */
  suspend fun shareCard(
    context: Context,
    payload: ShareCardPayload,
    config: ShareCardConfig
  ): Boolean = withContext(Dispatchers.IO) {
    try {
      val bitmap = generateCardBitmap(context, payload, config)
      val shareDir = File(context.cacheDir, "share_cards")
      if (!shareDir.exists()) {
        shareDir.mkdirs()
      }

      val imageFile = File(shareDir, "cherish_card_${System.currentTimeMillis()}.png")
      FileOutputStream(imageFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
      }

      val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
      )

      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(Intent.EXTRA_SUBJECT, payload.title)
        putExtra(Intent.EXTRA_TEXT, "${payload.title} • Cherish")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      withContext(Dispatchers.Main) {
        context.startActivity(Intent.createChooser(shareIntent, "Share Memory Card"))
      }
      true
    } catch (e: Exception) {
      e.printStackTrace()
      withContext(Dispatchers.Main) {
        Toast.makeText(context, R.string.share_card_share_error, Toast.LENGTH_SHORT).show()
      }
      false
    }
  }

  /**
   * Saves the generated card image to the device's public Photos / Pictures directory.
   */
  suspend fun saveCardToDevice(
    context: Context,
    payload: ShareCardPayload,
    config: ShareCardConfig
  ): Boolean = withContext(Dispatchers.IO) {
    try {
      val bitmap = generateCardBitmap(context, payload, config)
      val filename = "Cherish_${System.currentTimeMillis()}.png"

      var outputStream: OutputStream? = null
      var imageUri: Uri? = null

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
          put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
          put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Cherish")
          put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (imageUri != null) {
          outputStream = resolver.openOutputStream(imageUri)
        }

        if (outputStream != null) {
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
          outputStream.close()

          contentValues.clear()
          contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
          imageUri?.let { resolver.update(it, contentValues, null, null) }
        }
      } else {
        @Suppress("DEPRECATION")
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val cherishDir = File(picturesDir, "Cherish")
        if (!cherishDir.exists()) cherishDir.mkdirs()

        val destFile = File(cherishDir, filename)
        outputStream = FileOutputStream(destFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()

        val values = ContentValues().apply {
          @Suppress("DEPRECATION")
          put(MediaStore.Images.Media.DATA, destFile.absolutePath)
          put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
      }

      withContext(Dispatchers.Main) {
        Toast.makeText(context, R.string.share_card_saved_success, Toast.LENGTH_SHORT).show()
      }
      true
    } catch (e: Exception) {
      e.printStackTrace()
      withContext(Dispatchers.Main) {
        Toast.makeText(context, R.string.share_card_saved_error, Toast.LENGTH_SHORT).show()
      }
      false
    }
  }
}
