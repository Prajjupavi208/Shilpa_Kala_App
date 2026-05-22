package com.example.shilpakala.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shilpakala.R

// Premium Gold Palette
private val LuxuryGold = Color(0xFFD4AF37)
private val DeepBlack = Color(0xFF080808)
private val RichObsidian = Color(0xFF121212)

@Composable
fun LandingScreen(
    onOpenStudio: () -> Unit,
    onViewGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val entryAlpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        entryAlpha.animateTo(1f, tween(1500, easing = EaseOutExpo))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // 1. Premium Background Design
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DeepBlack, RichObsidian, Color.Black)
                    )
                )
        )
        
        KarnatakaDecorativePattern()
        GoldAmbientGlow()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .alpha(entryAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 2. Logo & Branding Header
            BrandingHeader()

            Spacer(modifier = Modifier.height(48.dp))

            // 3. Action Section
            ActionSection(onOpenStudio, onViewGallery)

            Spacer(modifier = Modifier.height(64.dp))

            // 4. Featured Artisan Section
            FeaturedArtisanSection()

            Spacer(modifier = Modifier.height(48.dp))

            // 5. Maker's Mark Section
            MakersMarkSection()

            Spacer(modifier = Modifier.height(48.dp))

            // 6. Gallery Preview Row
            GalleryPreviewSection()

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun BrandingHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Logo removed as requested

        Text(
            text = "SHILPA KALA",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
            fontWeight = FontWeight.ExtraLight,
            letterSpacing = 12.sp,
            fontFamily = FontFamily.Serif
        )

        Text(
            text = "ಶಿಲ್ಪ-ಕಲಾ",
            style = MaterialTheme.typography.headlineSmall,
            color = LuxuryGold,
            fontWeight = FontWeight.Normal,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .width(40.dp)
                .height(1.dp)
                .background(LuxuryGold.copy(alpha = 0.4f))
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "KARNATAKA ARTISAN STUDIO",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.5f),
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ActionSection(onOpenStudio: () -> Unit, onViewGallery: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(0.88f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LuxuryMainButton(
            text = stringResource(id = R.string.open_studio),
            onClick = onOpenStudio,
            primary = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        LuxuryMainButton(
            text = stringResource(id = R.string.view_gallery),
            onClick = onViewGallery,
            primary = false
        )
    }
}

@Composable
private fun LuxuryMainButton(text: String, onClick: () -> Unit, primary: Boolean) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "scale")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (primary) {
                    Modifier
                        .background(LuxuryGold)
                        .shadow(12.dp, spotColor = LuxuryGold, ambientColor = LuxuryGold)
                } else {
                    Modifier
                        .border(1.dp, LuxuryGold.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                }
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = if (primary) Color.Black else LuxuryGold,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp
        )
    }
}

@Composable
private fun FeaturedArtisanSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF111111))
            .border(0.5.dp, LuxuryGold.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.hero_gombe),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
            
            // "Handmade in Karnataka" Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Handmade in Karnataka",
                    color = LuxuryGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            // View in AR badge
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                color = LuxuryGold,
                shape = CircleShape
            ) {
                Text(
                    "AR",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
        
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "The Legacy of Kinnahal",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Experience the craft of traditional wooden dolls, a heritage spanning generations in Koppal.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun MakersMarkSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.artisan_work),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(1.dp, LuxuryGold.copy(alpha = 0.4f), CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(
                text = "THE MAKER'S MARK",
                style = MaterialTheme.typography.labelSmall,
                color = LuxuryGold,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Each artisan carries the weight of a thousand years, breathing life into wood and clay.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun GalleryPreviewSection() {
    val items = listOf(R.drawable.elephant, R.drawable.family_dolls, R.drawable.mask_art)
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "GALLERY HIGHLIGHTS",
            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            letterSpacing = 2.sp
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { resId ->
                Image(
                    painter = painterResource(resId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(0.5.dp, LuxuryGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun GoldAmbientGlow() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopEnd)
                .offset(x = 150.dp, y = (-100).dp)
                .blur(100.dp)
                .alpha(0.1f)
                .background(LuxuryGold, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-150).dp, y = 200.dp)
                .blur(100.dp)
                .alpha(0.08f)
                .background(LuxuryGold, CircleShape)
        )
    }
}

@Composable
private fun KarnatakaDecorativePattern() {
    Canvas(modifier = Modifier.fillMaxSize().alpha(0.04f)) {
        val stroke = 1.dp.toPx()
        val spacing = 50.dp.toPx()
        for (i in -10..20) {
            drawLine(LuxuryGold, Offset(i * spacing, 0f), Offset(i * spacing + size.height, size.height), stroke)
            drawLine(LuxuryGold, Offset(i * spacing, size.height), Offset(i * spacing + size.height, 0f), stroke)
        }
    }
}
