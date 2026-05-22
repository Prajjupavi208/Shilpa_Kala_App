package com.example.shilpakala.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.util.Log
import java.io.File

/**
 * Robust utility to handle Bitmap operations including EXIF-based rotation correction.
 */
object BitmapUtils {

    private const val TAG = "BitmapUtils"

    /**
     * Decodes a bitmap from a file path and automatically corrects its orientation
     * using EXIF metadata.
     */
    fun decodeAndFixRotation(path: String): Bitmap? {
        val file = File(path)
        if (!file.exists()) {
            Log.e(TAG, "File does not exist: $path")
            return null
        }

        // 1. Decode the bitmap as-is
        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        val bitmap = BitmapFactory.decodeFile(path, options) ?: return null

        // 2. Read EXIF orientation
        val orientation = try {
            val exif = ExifInterface(path)
            exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read EXIF: ${e.message}")
            ExifInterface.ORIENTATION_UNDEFINED
        }

        // 3. Map EXIF orientation to degrees
        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

        if (degrees == 0f) return bitmap

        // 4. Rotate the bitmap
        return try {
            val matrix = Matrix().apply { postRotate(degrees) }
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            rotatedBitmap
        } catch (e: Exception) {
            Log.e(TAG, "Failed to rotate bitmap: ${e.message}")
            bitmap
        }
    }
}
