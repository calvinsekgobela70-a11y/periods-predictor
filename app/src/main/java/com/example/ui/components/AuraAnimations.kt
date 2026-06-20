package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Pastel soothing colors for floating background orbs
val SoftLavender = Color(0xFFF5F3FF)
val SoftBlushPink = Color(0xFFFFE4E6)
val SoftMint = Color(0xFFD3F9E9)
val SoftPeach = Color(0xFFFFE5D9)
val SoftBluePastel = Color(0xFFE0E7FF)

@Composable
fun FloatingPastelOrbs(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Background Orbs")

    // Dynamic state animations for coordinate offsets and sizes
    val animOffset1X by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb 1 X"
    )
    val animOffset1Y by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb 1 Y"
    )

    val animOffset2X by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb 2 X"
    )
    val animOffset2Y by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb 2 Y"
    )

    val animOffset3X by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb 3 X"
    )
    val animOffset3Y by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb 3 Y"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Render first soothing orb (Blush Pink)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SoftBlushPink.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(width * animOffset1X, height * animOffset1Y),
                    radius = width * 0.45f
                ),
                center = Offset(width * animOffset1X, height * animOffset1Y),
                radius = width * 0.45f
            )

            // Render second soothing orb (Lavender)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SoftLavender.copy(alpha = 0.40f), Color.Transparent),
                    center = Offset(width * animOffset2X, height * animOffset2Y),
                    radius = width * 0.5f
                ),
                center = Offset(width * animOffset2X, height * animOffset2Y),
                radius = width * 0.5f
            )

            // Render third soothing orb (Mint)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SoftMint.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(width * animOffset3X, height * animOffset3Y),
                    radius = width * 0.38f
                ),
                center = Offset(width * animOffset3X, height * animOffset3Y),
                radius = width * 0.38f
            )
        }
    }
}

@Composable
fun rememberBreathingScale(
    durationMillis: Int = 4000,
    minScale: Float = 0.94f,
    maxScale: Float = 1.06f
): State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "Breathing Animation")
    return infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale Factor"
    )
}
