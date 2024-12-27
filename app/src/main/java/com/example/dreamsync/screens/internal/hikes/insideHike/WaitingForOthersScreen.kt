package com.example.dreamsync.screens.internal.hikes.insideHike

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun WaitingForOthersScreen(progress: Float, timerValue: Int) {
    CircularTimerWithInnerContent(
        progress = progress,
        currentTime = timerValue,
        label = "Waiting for others to join...",
        information = "2/3 members have joined"
    )
}


@Composable
fun CircularTimerWithInnerContent(
    progress: Float,
    currentTime: Int,
    label: String,
    information: String = "",
    isButtonVisible: Boolean = false,
    buttonLabel: String = "Continue",
    onClickButton: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        CircularTimer(progress = progress, modifier = Modifier.size(CIRCLE_SIZE.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Text(
                text = "${currentTime}s",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            if (information.isNotEmpty()) {
                Text(
                    text = information,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    color = Color.Gray
                )
            }

            if (isButtonVisible) {
                Button(
                    onClick = { onClickButton() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39CCCC)),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(buttonLabel, style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CircularTimer(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val sizeOffset = strokeWidth / 2
        val arcSize = Size(size.width - sizeOffset, size.height - sizeOffset)

        drawCircle(
            color = Color.Gray.copy(alpha = 0.2f),
            radius = size.minDimension / 2 - sizeOffset,
            style = Stroke(strokeWidth)
        )
        drawArc(
            color = Color(0xFF39CCCC),
            startAngle = -90f,
            sweepAngle = 360 * progress,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(sizeOffset, sizeOffset),
            size = arcSize
        )
    }
}