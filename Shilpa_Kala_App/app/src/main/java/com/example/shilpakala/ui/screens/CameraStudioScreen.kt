package com.example.shilpakala.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.shilpakala.R
import com.example.shilpakala.ui.camera.ProductGuideOverlay
import com.example.shilpakala.utils.BitmapUtils
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Premium Artisan Camera Studio.
 * Optimized for high-end product capture with a luxury minimalist UI and orientation correction.
 */
@Composable
fun CameraStudioScreen(
    onCaptured: (rawPath: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (hasCameraPermission) {
            CameraContent(
                onCaptured = onCaptured
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.camera_permission_required),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun CameraContent(
    onCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember(context) { ContextCompat.getMainExecutor(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var imageCaptureUseCase by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    // Use cases remembered to survive recomposition
    val previewUseCase = remember { Preview.Builder().build() }
    val captureUseCase = remember { 
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Live Camera Preview (TextureView mode for stability with overlays)
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                previewUseCase,
                                captureUseCase
                            )
                            previewUseCase.surfaceProvider = this.surfaceProvider
                            imageCaptureUseCase = captureUseCase
                        } catch (e: Exception) {
                            Log.e("CameraStudio", "Binding failed", e)
                        }
                    }, mainExecutor)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Premium focused guide overlay (Square, 78% width as requested)
        ProductGuideOverlay(modifier = Modifier.fillMaxSize())

        // 3. Immersive Control Panel
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
                .navigationBarsPadding()
                .padding(bottom = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            StudioCaptureButton(
                isCapturing = isCapturing,
                onClick = {
                    if (!isCapturing && imageCaptureUseCase != null) {
                        isCapturing = true
                        takeRawPhoto(
                            context = context,
                            imageCapture = imageCaptureUseCase,
                            executor = cameraExecutor,
                            onCaptured = {
                                isCapturing = false
                                onCaptured(it)
                            }
                        )
                    }
                }
            )
        }

        // Processing Overlay
        if (isCapturing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun StudioCaptureButton(
    isCapturing: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "buttonScale"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(animatedScale)
            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            .padding(6.dp)
            .clip(CircleShape)
            .background(if (isCapturing) Color.Transparent else Color.White.copy(alpha = 0.15f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isCapturing,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!isCapturing) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Capture",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

private fun takeRawPhoto(
    context: Context,
    imageCapture: ImageCapture?,
    executor: Executor,
    onCaptured: (String) -> Unit
) {
    if (imageCapture == null) return

    val photoFile = File(
        context.cacheDir,
        "studio_raw_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                // Automatically fix rotation based on EXIF before passing the path
                val correctedBitmap = BitmapUtils.decodeAndFixRotation(photoFile.absolutePath)
                if (correctedBitmap != null) {
                    try {
                        FileOutputStream(photoFile).use { out ->
                            correctedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                            out.flush()
                        }
                    } catch (e: Exception) {
                        Log.e("CameraStudio", "Failed to save corrected bitmap", e)
                    }
                }

                // UI updates must happen on main thread
                ContextCompat.getMainExecutor(context).execute {
                    onCaptured(photoFile.absolutePath)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraStudio", "Capture failed: ${exception.message}", exception)
                ContextCompat.getMainExecutor(context).execute {
                    Toast.makeText(context, "Capture Failed", Toast.LENGTH_SHORT).show()
                    onCaptured("")
                }
            }
        }
    )
}
