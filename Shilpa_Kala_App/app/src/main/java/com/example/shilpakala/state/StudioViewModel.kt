package com.example.shilpakala.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.shilpakala.model.ProductData

class StudioViewModel : ViewModel() {

    var capturedRawPath: String? by mutableStateOf(null)
        private set

    var productData: ProductData by mutableStateOf(ProductData())
        private set

    var processedImagePaths: List<String> by mutableStateOf(emptyList())
        private set

    var latestProcessedPath: String? by mutableStateOf(null)
        private set

    /**
     * Renamed to avoid JVM signature clash with property's generated setter.
     */
    fun updateCapturedRawPath(path: String?) {
        capturedRawPath = path
    }

    fun updateProductData(
        update: (ProductData) -> ProductData
    ) {
        productData = update(productData)
    }

    fun addProcessedImagePath(path: String) {
        processedImagePaths = listOf(path) + processedImagePaths
        latestProcessedPath = path
    }

    /**
     * Renamed to avoid JVM signature clash.
     */
    fun updateLatestProcessedPath(path: String?) {
        latestProcessedPath = path
    }

    /**
     * Renamed to avoid JVM signature clash.
     */
    fun updateProcessedImagePaths(paths: List<String>) {
        processedImagePaths = paths
    }

    fun clearSession() {
        capturedRawPath = null
        productData = ProductData()
        latestProcessedPath = null
        processedImagePaths = emptyList()
    }
}
