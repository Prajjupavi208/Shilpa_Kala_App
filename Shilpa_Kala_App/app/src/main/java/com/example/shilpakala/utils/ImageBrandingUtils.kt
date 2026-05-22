package com.example.shilpakala.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.Bitmap.Config

/**
 * Utility to add branding elements to a captured product image.
 * Uses Android's Canvas API to draw text and graphics onto a Bitmap.
 */
object ImageBrandingUtils {

    /**
     * Adds "Handmade in Karnataka" watermark, artisan name, and price to a bitmap.
     * Returns a new branded Bitmap.
     */
    fun brandImage(
        originalBitmap: Bitmap,
        artisanName: String,
        price: String
    ): Bitmap {
        // 1) Create a mutable copy of the bitmap so we can draw on top of it.
        val brandedBitmap = originalBitmap.copy(Config.ARGB_8888, true)
        val canvas = Canvas(brandedBitmap)

        val w = brandedBitmap.width.toFloat()
        val h = brandedBitmap.height.toFloat()
        val padding = minOf(w, h) * 0.05f

        // 2) Paints for text + footer background.
        val watermarkPaint = Paint().apply {
            color = Color.WHITE
            alpha = 200 // semi-transparent white for readability
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
            textSize = minOf(w, h) * 0.05f
        }

        val footerTextPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = minOf(w, h) * 0.06f
        }

        val priceTextPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = minOf(w, h) * 0.065f
            textAlign = Paint.Align.RIGHT
        }

        // 3) White watermark text (top center)
        val watermarkText = "Handmade in Karnataka"
        val watermarkTextWidth = watermarkPaint.measureText(watermarkText)
        val watermarkX = (w - watermarkTextWidth) / 2f
        val watermarkY = padding + watermarkPaint.textSize
        canvas.drawText(watermarkText, watermarkX, watermarkY, watermarkPaint)

        // 4) Conditional Bottom brand bar
        val hasArtisan = artisanName.isNotBlank() && artisanName.lowercase() != "artisan"
        val hasPrice = price.isNotBlank() && price != "0" && price != "00"

        if (hasArtisan || hasPrice) {
            val footerHeight = minOf(w, h) * 0.18f
            val footerLeft = padding
            val footerRight = w - padding
            val footerTop = h - padding - footerHeight
            val footerBottom = h - padding

            val footerBgPaint = Paint().apply {
                color = Color.WHITE
                alpha = 25 // subtle translucent background
                isAntiAlias = true
            }
            val footerCornerRadius = footerHeight / 2f
            canvas.drawRoundRect(
                RectF(footerLeft, footerTop, footerRight, footerBottom),
                footerCornerRadius,
                footerCornerRadius,
                footerBgPaint
            )

            val textBaselineY = footerTop + footerHeight / 2f + footerTextPaint.textSize / 3f
            
            if (hasArtisan) {
                canvas.drawText(
                    artisanName.uppercase(),
                    footerLeft + padding * 0.4f,
                    textBaselineY,
                    footerTextPaint
                )
            }
            
            if (hasPrice) {
                canvas.drawText(
                    "₹$price",
                    footerRight - padding * 0.4f,
                    textBaselineY,
                    priceTextPaint
                )
            }
        }

        return brandedBitmap
    }
}
