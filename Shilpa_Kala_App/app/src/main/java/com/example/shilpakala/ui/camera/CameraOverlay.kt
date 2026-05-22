package com.example.shilpakala.ui.camera

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shilpakala.R

/**
 * Premium Luxury Camera focus frame.
 * Refined for 78% width and square composition to highlight artisans products perfectly.
 */
@Composable
fun ProductGuideOverlay(
    modifier: Modifier = Modifier,
    // Updated to 78% as requested for better product highlighting
    frameWidthFraction: Float = 0.78f,
    frameAspectRatio: Float = 1.0f, // Elegant Square aspect ratio
    bracketLength: Dp = 24.dp,
    strokeWidth: Dp = 1.2.dp,
    guideBoxDash: Dp = 6.dp,
    guideBoxGap: Dp = 6.dp,
    cornerRadius: Dp = 20.dp // Pronounced premium rounded corners
) {
    val textMeasurer = rememberTextMeasurer()
    val heritageText = stringResource(id = R.string.handmade_karnataka).uppercase()

    Canvas(modifier = modifier) {
        val strokePx = strokeWidth.toPx()
        val bracketPx = bracketLength.toPx()
        val dashPx = guideBoxDash.toPx()
        val gapPx = guideBoxGap.toPx()
        val radiusPx = cornerRadius.toPx()

        // Calculate frame dimensions
        val frameWidth = size.width * frameWidthFraction
        val frameHeight = frameWidth / frameAspectRatio
        val left = (size.width - frameWidth) / 2f
        
        // Positioned for perfect center composition (Point 1 & 3)
        val top = (size.height - frameHeight) / 2.5f 
        
        val frameRect = Rect(Offset(left, top), Size(frameWidth, frameHeight))
        val frameRoundRect = RoundRect(frameRect, CornerRadius(radiusPx))

        // 1. Soft dark overlay outside focus frame (Point 2)
        // Using EvenOdd to subtract the center hole from the full screen overlay
        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            addRect(Rect(0f, 0f, size.width, size.height))
            addRoundRect(frameRoundRect)
        }
        drawPath(path = path, color = Color.Black.copy(alpha = 0.75f))

        // 2. Elegant white semi-transparent border (Point 1)
        drawRoundRect(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = frameRect.topLeft,
            size = frameRect.size,
            cornerRadius = CornerRadius(radiusPx),
            style = Stroke(
                width = strokePx,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx))
            )
        )

        // 3. Subtle Corner Focus Brackets (Point 1)
        val bracketColor = Color.White.copy(alpha = 0.5f)
        val bOffset = 4.dp.toPx()
        
        // Top Left
        drawLine(bracketColor, Offset(left - bOffset, top - bOffset), Offset(left - bOffset + bracketPx, top - bOffset), strokePx)
        drawLine(bracketColor, Offset(left - bOffset, top - bOffset), Offset(left - bOffset, top - bOffset + bracketPx), strokePx)
        
        // Top Right
        drawLine(bracketColor, Offset(left + frameWidth + bOffset, top - bOffset), Offset(left + frameWidth + bOffset - bracketPx, top - bOffset), strokePx)
        drawLine(bracketColor, Offset(left + frameWidth + bOffset, top - bOffset), Offset(left + frameWidth + bOffset, top - bOffset + bracketPx), strokePx)
        
        // Bottom Left
        drawLine(bracketColor, Offset(left - bOffset, top + frameHeight + bOffset), Offset(left - bOffset + bracketPx, top + frameHeight + bOffset), strokePx)
        drawLine(bracketColor, Offset(left - bOffset, top + frameHeight + bOffset), Offset(left - bOffset, top + frameHeight + bOffset - bracketPx), strokePx)
        
        // Bottom Right
        drawLine(bracketColor, Offset(left + frameWidth + bOffset, top + frameHeight + bOffset), Offset(left + frameWidth + bOffset - bracketPx, top + frameHeight + bOffset), strokePx)
        drawLine(bracketColor, Offset(left + frameWidth + bOffset, top + frameHeight + bOffset), Offset(left + frameWidth + bOffset, top + frameHeight + bOffset - bracketPx), strokePx)

        // 4. Heritage text (Point 2)
        val textLayout = textMeasurer.measure(
            text = AnnotatedString(heritageText),
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                fontSize = 9.sp,
                letterSpacing = 5.sp,
                color = Color.White.copy(alpha = 0.45f),
                textAlign = TextAlign.Center
            )
        )
        
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(
                x = (size.width - textLayout.size.width) / 2f,
                y = top + frameHeight + 36.dp.toPx()
            )
        )
    }
}
