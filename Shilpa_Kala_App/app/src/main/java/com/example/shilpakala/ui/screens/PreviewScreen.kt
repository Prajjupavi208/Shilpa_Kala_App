package com.example.shilpakala.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.shilpakala.R
import com.example.shilpakala.model.ProductData

/**
 * Premium Preview Screen for artisan portfolio.
 * Refactored for a high-end luxury aesthetic with corrected UI/UX.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    imagePath: String?,
    modifier: Modifier = Modifier,
    productData: ProductData? = null,
    onShareWhatsApp: () -> Unit = {},
    onViewGallery: () -> Unit = {}
) {
    val gold = Color(0xFFD4AF37)
    val obsidian = Color(0xFF080808)
    val cardBackground = Color(0xFF111111)

    // Zoom/Pan State for the image interaction
    var scaleFactor by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val entryAlpha = remember { Animatable(0f) }
    val entryScale = remember { Animatable(0.95f) }

    LaunchedEffect(Unit) {
        entryAlpha.animateTo(1f, tween(1200, easing = EaseOutExpo))
        entryScale.animateTo(1f, tween(1200, easing = EaseOutExpo))
    }

    Scaffold(
        modifier = modifier.fillMaxSize().statusBarsPadding(), // Fix top clipping
        containerColor = obsidian,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "STUDIO PREVIEW",
                        color = Color.White,
                        letterSpacing = 5.sp,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onViewGallery) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Immersive Image Showcase Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .shadow(32.dp, RoundedCornerShape(12.dp), spotColor = gold.copy(alpha = 0.15f))
                    .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scaleFactor = (scaleFactor * zoom).coerceIn(1f, 5f)
                            if (scaleFactor > 1f) {
                                offset += pan
                            } else {
                                offset = Offset.Zero
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imagePath,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            translationX = offset.x
                            translationY = offset.y
                        },
                    contentScale = ContentScale.Crop
                )

                // 1. Premium Floating Luxury Label (Bottom-Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    PremiumFloatingLabel(productData = productData, gold = gold)
                }

                // Reset Zoom Hint
                androidx.compose.animation.AnimatedVisibility(
                    visible = scaleFactor > 1.1f,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    label = "zoomReset",
                    modifier = Modifier.align(Alignment.TopEnd).padding(14.dp)
                ) {
                    IconButton(
                        onClick = { scaleFactor = 1f; offset = Offset.Zero },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reset Zoom",
                            tint = gold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 2. HERITAGE BRANDING STAMP (Correct letter spacing and alignment)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 34.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(gold.copy(alpha = 0.3f))
                )
                Text(
                    text = "  HANDMADE IN KARNATAKA  ",
                    style = TextStyle(
                        fontSize = 10.sp,
                        letterSpacing = 6.sp, // Luxury spacing
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.3f),
                        textAlign = TextAlign.Center
                    )
                )
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(gold.copy(alpha = 0.3f))
                )
            }

            // 3. Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                PremiumActionButton(
                    text = stringResource(id = R.string.share),
                    onClick = onShareWhatsApp,
                    containerColor = gold,
                    contentColor = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                PremiumActionButton(
                    text = stringResource(id = R.string.view_gallery),
                    onClick = onViewGallery,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    border = true
                )
            }
        }
    }
}

/**
 * Compact premium floating luxury label card with glassmorphism.
 * Refined to remove placeholders and use serif typography.
 */
@Composable
fun PremiumFloatingLabel(productData: ProductData?, gold: Color) {
    if (productData == null) return

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .shadow(16.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = Color.Black.copy(alpha = 0.7f), // Glassmorphism
        border = BorderStroke(0.5.dp, gold.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Artisan Name: BY [NAME] or hide if empty
            val artisan = productData.artisanName
            if (!artisan.isNullOrBlank() && artisan.lowercase() != "artisan") {
                Text(
                    text = "BY ${artisan.uppercase()}",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = gold,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Product Name: Elegant Serif
            Text(
                text = productData.productName.ifBlank { "Masterpiece" },
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Price: Hide if 0 or null
            val price = productData.priceInr
            if (!price.isNullOrBlank() && price != "0" && price != "00") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹$price",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun PremiumActionButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    border: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "buttonScale")

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(animatedScale),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = if (border) BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)) else null,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (containerColor != Color.Transparent) 8.dp else 0.dp
        )
    ) {
        Text(
            text = text.uppercase(),
            style = TextStyle(
                fontSize = 13.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
