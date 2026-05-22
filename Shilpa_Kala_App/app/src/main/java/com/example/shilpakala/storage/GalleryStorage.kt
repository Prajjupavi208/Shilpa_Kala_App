package com.example.shilpakala.storage

import android.content.Context
import java.io.File

object GalleryStorage {
    private const val GalleryDirName = "shilpa_kala_gallery"

    fun galleryDir(context: Context): File {
        val dir = File(context.filesDir, GalleryDirName)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun newGalleryFile(context: Context): File {
        val dir = galleryDir(context)
        return File(dir, "shilpa_kala_${System.currentTimeMillis()}.jpg")
    }

    fun listGalleryFiles(context: Context): List<File> {
        val dir = galleryDir(context)
        return dir.listFiles()
            ?.filter { it.isFile && it.extension.equals("jpg", ignoreCase = true) }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    fun deleteFile(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }
}
