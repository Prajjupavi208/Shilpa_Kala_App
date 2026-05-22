package com.example.shilpakala.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.shilpakala.R
import com.example.shilpakala.utils.BitmapUtils
import com.example.shilpakala.utils.ImageBrandingUtils
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreviewScreen(
    onCaptured: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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
                lifecycleOwner = lifecycleOwner,
                onCaptured = onCaptured
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
    lifecycleOwner: LifecycleOwner,
    onCaptured: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mainExecutor = remember(context) { ContextCompat.getMainExecutor(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var imageCaptureUseCase by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    val captureScale = remember { Animatable(1f) }

    var artisanName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val previewUseCase = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val captureUseCase = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            previewUseCase,
                            captureUseCase
                        )
                        imageCaptureUseCase = captureUseCase
                    } catch (e: Exception) {
                        Log.e("CameraX", "Binding failed", e)
                    }
                }, mainExecutor)

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        ProductGuideOverlay(modifier = Modifier.fillMaxSize())

        // UI Controls Overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = artisanName,
                onValueChange = { artisanName = it },
                label = { Text("Artisan Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Product Price (₹)") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.4f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(84.dp)
                    .scale(captureScale.value)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                IconButton(
                    onClick = {
                        if (!isCapturing) {
                            isCapturing = true
                            takePhoto(
                                context = context,
                                imageCapture = imageCaptureUseCase,
                                cameraCallbackExecutor = cameraExecutor,
                                uiExecutor = mainExecutor,
                                artisanName = artisanName,
                                productPrice = productPrice,
                                onCaptured = {
                                    isCapturing = false
                                    onCaptured(it)
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = stringResource(id = R.string.capture_photo),
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
        
        if (isCapturing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    cameraCallbackExecutor: Executor,
    uiExecutor: Executor,
    artisanName: String,
    productPrice: String,
    onCaptured: (String) -> Unit
) {
    if (imageCapture == null) {
        uiExecutor.execute {
            Toast.makeText(context, "Camera not ready", Toast.LENGTH_SHORT).show()
        }
        return
    }

    val photoFile = File(
        context.cacheDir,
        "shilpakala_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        cameraCallbackExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // FIXED: Use BitmapUtils to fix rotation during decoding
                val bitmap = BitmapUtils.decodeAndFixRotation(photoFile.absolutePath)
                if (bitmap == null) {
                    uiExecutor.execute {
                        Toast.makeText(context, "Photo processing failed", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                // Apply branding
                val brandedBitmap = ImageBrandingUtils.brandImage(
                    originalBitmap = bitmap,
                    artisanName = artisanName.ifBlank { "Artisan" },
                    price = productPrice.ifBlank { "0" }
                )

                FileOutputStream(photoFile).use { out ->
                    brandedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                    out.flush()
                }

                uiExecutor.execute {
                    Toast.makeText(context, "Photo Saved & Branded!", Toast.LENGTH_SHORT).show()
                    onCaptured(photoFile.absolutePath)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraPreview", "Capture failed", exception)
                uiExecutor.execute {
                    Toast.makeText(context, "Capture Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
}

private suspend fun Context.awaitCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            continuation.resume(future.get())
        }, ContextCompat.getMainExecutor(this))
    }
