package com.example.shilpakala.navigation

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shilpakala.R
import com.example.shilpakala.image.StudioBrandingEngine
import com.example.shilpakala.share.ShareUtils
import com.example.shilpakala.state.StudioViewModel
import com.example.shilpakala.storage.GalleryStorage
import com.example.shilpakala.ui.camera.CameraPreviewScreen
import com.example.shilpakala.ui.screens.BrandingDetailsScreen
import com.example.shilpakala.ui.screens.GalleryScreen
import com.example.shilpakala.ui.screens.LandingScreen
import com.example.shilpakala.ui.screens.PreviewScreen
import com.example.shilpakala.utils.BitmapUtils
import java.io.FileOutputStream
import java.util.concurrent.Executors

@Composable
fun AppNavGraph(
    navController: NavHostController,
    studioViewModel: StudioViewModel
) {
    val context = LocalContext.current
    val uiExecutor = remember(context) { ContextCompat.getMainExecutor(context) }
    val backgroundExecutor = remember { Executors.newSingleThreadExecutor() }
    
    // Localization
    val errorNoPhoto = stringResource(id = R.string.error_no_photo)
    val errorDecode = stringResource(id = R.string.error_decode_failed)
    val photoSavedMsg = stringResource(id = R.string.photo_saved)

    DisposableEffect(Unit) { onDispose { backgroundExecutor.shutdown() } }

    NavHost(
        navController = navController,
        startDestination = Routes.Landing,
        enterTransition = { fadeIn(tween(500)) + slideInHorizontally(tween(500)) { it / 3 } },
        exitTransition = { fadeOut(tween(500)) },
        popEnterTransition = { fadeIn(tween(500)) },
        popExitTransition = { fadeOut(tween(500)) + slideOutHorizontally(tween(500)) { it / 3 } }
    ) {
        composable(Routes.Landing) {
            LandingScreen(
                onOpenStudio = { navController.navigate(Routes.Camera) },
                onViewGallery = { navController.navigate(Routes.Gallery) }
            )
        }

        composable(Routes.Camera) {
            CameraPreviewScreen(
                onCaptured = { rawPath ->
                    studioViewModel.updateCapturedRawPath(rawPath)
                    navController.navigate(Routes.Details)
                }
            )
        }

        composable(Routes.Details) {
            var isProcessing by remember { mutableStateOf(false) }

            BrandingDetailsScreen(
                initial = studioViewModel.productData,
                isProcessing = isProcessing,
                onSubmit = { product ->
                    studioViewModel.updateProductData { product }

                    val rawPath = studioViewModel.capturedRawPath
                    if (rawPath.isNullOrBlank()) {
                        Toast.makeText(context, errorNoPhoto, Toast.LENGTH_SHORT).show()
                        return@BrandingDetailsScreen
                    }

                    isProcessing = true
                    backgroundExecutor.execute {
                        runCatching {
                            // FIXED: Use BitmapUtils to fix rotation during decoding
                            val originalBitmap = BitmapUtils.decodeAndFixRotation(rawPath)
                                ?: error(errorDecode)

                            val branded = StudioBrandingEngine.brand(product, originalBitmap)

                            val outFile = GalleryStorage.newGalleryFile(context)
                            FileOutputStream(outFile).use { out ->
                                branded.compress(Bitmap.CompressFormat.JPEG, 95, out)
                                out.flush()
                            }

                            uiExecutor.execute {
                                studioViewModel.addProcessedImagePath(outFile.absolutePath)
                                isProcessing = false
                                Toast.makeText(context, photoSavedMsg, Toast.LENGTH_SHORT).show()
                                navController.navigate(Routes.Preview)
                            }
                        }.onFailure { err ->
                            uiExecutor.execute {
                                isProcessing = false
                                val msg = context.getString(R.string.error_processing_failed, err.message)
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            )
        }

        composable(Routes.Preview) {
            PreviewScreen(
                imagePath = studioViewModel.latestProcessedPath,
                productData = studioViewModel.productData,
                onShareWhatsApp = {
                    val path = studioViewModel.latestProcessedPath
                    if (!path.isNullOrBlank()) {
                        ShareUtils.shareToWhatsApp(context, path)
                    }
                },
                onViewGallery = { 
                    navController.navigate(Routes.Gallery) {
                        popUpTo(Routes.Landing) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.Gallery) {
            LaunchedEffect(Unit) {
                val files = GalleryStorage.listGalleryFiles(context)
                studioViewModel.updateProcessedImagePaths(files.map { it.absolutePath })
            }

            GalleryScreen(
                imagePaths = studioViewModel.processedImagePaths,
                onOpen = {
                    studioViewModel.updateLatestProcessedPath(it)
                    navController.navigate(Routes.Preview)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
