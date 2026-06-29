package com.example.nightingalehospitalapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A modifier that applies a generic shimmering effect.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val startOffsetX = transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAnimation"
    )

    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val colorAccent = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                color,
                colorAccent,
                color
            ),
            start = Offset(startOffsetX.value, 0f),
            end = Offset(startOffsetX.value + 500f, 500f)
        )
    )
}

/**
 * A placeholder layout that mimics a Doctor Profile Card but with a shimmer effect.
 */
@Composable
fun DoctorCardShimmer() {
    NightingaleElevatedCard {
        // Title placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(24.dp)
                .shimmerEffect()
                .background(Color.Gray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Subtitle placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(16.dp)
                .shimmerEffect()
                .background(Color.Gray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Content lines
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(16.dp)
                .shimmerEffect()
                .background(Color.Gray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(16.dp)
                .shimmerEffect()
                .background(Color.Gray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Button placeholders
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shimmerEffect()
                    .background(Color.Gray, RoundedCornerShape(20.dp))
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shimmerEffect()
                    .background(Color.Gray, RoundedCornerShape(20.dp))
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shimmerEffect()
                    .background(Color.Gray, RoundedCornerShape(20.dp))
            )
        }
    }
}

/**
 * A generic placeholder layout that mimics a standard list card with a shimmer effect.
 */
@Composable
fun NightingaleListShimmer() {
    NightingaleElevatedCard {
        // Title placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(24.dp)
                .shimmerEffect()
                .background(Color.Gray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(12.dp))
        // Content lines
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(16.dp)
                .shimmerEffect()
                .background(Color.Gray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(16.dp)
                .shimmerEffect()
                .background(Color.Gray, RoundedCornerShape(4.dp))
        )
    }
}
