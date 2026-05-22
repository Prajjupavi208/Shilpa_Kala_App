package com.example.shilpakala.image

import android.graphics.*
import com.example.shilpakala.model.ProductData

/**
 * Premium Studio Branding Engine for High-Resolution Exports.
 * Generates a cinematic portfolio card matching the luxury Preview UI.
 */
object StudioBrandingEngine {

    private const val HERITAGE_TEXT = "HANDMADE IN KARNATAKA"

    fun brand(product: ProductData, source: Bitmap): Bitmap {
        // 1. Setup High-Res Canvas (4:5 Portrait Ratio)
        val targetWidth = 1080
        val targetHeight = 1440
        val output = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val w = targetWidth.toFloat()
        val h = targetHeight.toFloat()

        // 2. Background: Deep Luxury Gradient
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, h,
                intArrayOf(Color.parseColor("#080808"), Color.parseColor("#121212"), Color.BLACK),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Subtle gold ambient glow
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.5f, h * 0.4f, w * 0.9f,
                intArrayOf(Color.parseColor("#1A1400"), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
            alpha = 180
        }
        canvas.drawCircle(w * 0.5f, h * 0.4f, w * 0.9f, glowPaint)

        // 3. Immersive Product Image
        val margin = 60f
        val imageW = w - (margin * 2)
        val imageH = imageW * 1.15f // Cinematic portrait focus
        val imageRect = RectF(margin, margin * 1.2f, margin + imageW, margin * 1.2f + imageH)
        val cornerRadius = 40f

        // Draw Image Shadow (Soft gold-tinted glow)
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D4AF37")
            alpha = 35
            maskFilter = BlurMaskFilter(45f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(imageRect, cornerRadius, cornerRadius, shadowPaint)

        // Clip and Draw Image
        val imagePath = Path().apply {
            addRoundRect(imageRect, cornerRadius, cornerRadius, Path.Direction.CW)
        }
        canvas.save()
        canvas.clipPath(imagePath)
        
        // Center-crop fill logic
        val scale = maxOf(imageW / source.width, imageH / source.height)
        val drawW = source.width * scale
        val drawH = source.height * scale
        val drawL = imageRect.left + (imageW - drawW) / 2f
        val drawT = imageRect.top + (imageH - drawH) / 2f
        
        canvas.drawBitmap(source, null, RectF(drawL, drawT, drawL + drawW, drawT + drawH), Paint(Paint.FILTER_BITMAP_FLAG))
        
        // Subtle internal vignette for focus
        val vignette = RadialGradient(
            imageRect.centerX(), imageRect.centerY(), imageRect.width() * 0.8f,
            intArrayOf(Color.TRANSPARENT, Color.BLACK),
            floatArrayOf(0.6f, 1.0f), Shader.TileMode.CLAMP
        )
        canvas.drawRect(imageRect, Paint().apply { shader = vignette; alpha = 140 })

        // 4. Floating Luxury Label (Inside the image area, bottom-left)
        val goldColor = Color.parseColor("#D4AF37")
        val labelMargin = 40f
        val labelPaddingX = 40f
        val labelPaddingY = 32f
        
        val artisanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = goldColor
            textSize = 20f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            letterSpacing = 0.15f
        }
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 44f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        }
        val pricePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 50f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        // Logic to check for available data and hide empty fields
        val hasArtisan = !product.artisanName.isNullOrBlank() && product.artisanName.lowercase() != "artisan"
        val textArtisan = if (hasArtisan) "BY ${product.artisanName.uppercase()}" else ""
        
        val textTitle = if (product.productName.isNotBlank()) product.productName else "MASTERPIECE"
        
        val hasPrice = !product.priceInr.isNullOrBlank() && product.priceInr != "0" && product.priceInr != "00"
        val textPrice = if (hasPrice) "₹${product.priceInr}" else ""

        var maxTextWidth = titlePaint.measureText(textTitle)
        if (hasArtisan) maxTextWidth = maxOf(maxTextWidth, artisanPaint.measureText(textArtisan))
        if (hasPrice) maxTextWidth = maxOf(maxTextWidth, pricePaint.measureText(textPrice))
        
        val labelW = maxTextWidth + (labelPaddingX * 2f)
        
        var totalTextHeight = titlePaint.textSize
        if (hasArtisan) totalTextHeight += artisanPaint.textSize + 12f
        if (hasPrice) totalTextHeight += pricePaint.textSize + 15f
        
        val labelH = totalTextHeight + (labelPaddingY * 3f)
        
        val labelRect = RectF(
            imageRect.left + labelMargin,
            imageRect.bottom - labelMargin - labelH,
            imageRect.left + labelMargin + labelW,
            imageRect.bottom - labelMargin
        )

        // Card Glassmorphism Background
        canvas.drawRoundRect(labelRect, 28f, 28f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            alpha = 190
        })
        
        // Card Border
        canvas.drawRoundRect(labelRect, 28f, 28f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = goldColor
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            alpha = 100
        })

        // Draw Label Text dynamically
        var currentY = labelRect.top + labelPaddingY
        
        if (hasArtisan) {
            currentY += artisanPaint.textSize * 0.8f
            canvas.drawText(textArtisan, labelRect.left + labelPaddingX, currentY, artisanPaint)
            currentY += 12f
        }
        
        currentY += titlePaint.textSize
        canvas.drawText(textTitle, labelRect.left + labelPaddingX, currentY, titlePaint)
        
        if (hasPrice) {
            currentY += pricePaint.textSize + 15f
            canvas.drawText(textPrice, labelRect.left + labelPaddingX, currentY, pricePaint)
        }

        canvas.restore() // End of image clipping

        // 5. Handmade Branding Stamp (Bottom Section)
        val footerCenterY = imageRect.bottom + (h - imageRect.bottom) * 0.55f
        val stampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = goldColor
            alpha = 180
            textSize = 26f
            letterSpacing = 0.8f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(HERITAGE_TEXT, w / 2f, footerCenterY, stampPaint)
        
        // Decorative Ornament Lines
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = goldColor
            strokeWidth = 1.2f
            alpha = 70
        }
        val textW = stampPaint.measureText(HERITAGE_TEXT)
        val gap = 50f
        val lineLen = 180f
        
        // Horizontal lines with diamond accents
        val lineY = footerCenterY - 8f
        canvas.drawLine(w/2 - textW/2 - gap - lineLen, lineY, w/2 - textW/2 - gap, lineY, linePaint)
        canvas.drawLine(w/2 + textW/2 + gap, lineY, w/2 + textW/2 + gap + lineLen, lineY, linePaint)
        
        // Small diamond caps on lines
        fun drawDiamond(cx: Float, cy: Float, size: Float) {
            val p = Path().apply {
                moveTo(cx, cy - size)
                lineTo(cx + size, cy)
                lineTo(cx, cy + size)
                lineTo(cx - size, cy)
                close()
            }
            canvas.drawPath(p, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = goldColor; alpha = 90 })
        }
        
        drawDiamond(w/2 - textW/2 - gap, lineY, 6f)
        drawDiamond(w/2 + textW/2 + gap, lineY, 6f)

        // Center Ornament (Diamond style) above text
        val orSize = 14f
        val orY = footerCenterY - 65f
        val ornamentPath = Path().apply {
            moveTo(w / 2f, orY)
            lineTo(w / 2f + orSize, orY + orSize)
            lineTo(w / 2f, orY + orSize * 2)
            lineTo(w / 2f - orSize, orY + orSize)
            close()
        }
        canvas.drawPath(ornamentPath, Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            color = goldColor
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = 100
        })

        return output
    }
}
