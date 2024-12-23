package com.example.dreamsync.screens.internal.hikes.inHike

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class HikeStage {
    WAITING_FOR_OTHERS,
    ENTERING_LAYER,
    IN_LAYER,
    LEAVING_LAYER,
    HIKE_COMPLETE
}

const val CIRCLE_SIZE = 300
const val DEFAULT_TIME_LEFT = 10

@Composable
fun MultiStageScreen() {
    var stage by remember { mutableStateOf(HikeStage.WAITING_FOR_OTHERS) }
    var progress by remember { mutableStateOf(0f) }
    var timerValue by remember { mutableStateOf(DEFAULT_TIME_LEFT) }

    // Start the countdown whenever the stage changes
    LaunchedEffect(stage) {
        startCountdown(DEFAULT_TIME_LEFT) { timeLeft ->
            timerValue = timeLeft
            progress = 1f - timeLeft / DEFAULT_TIME_LEFT.toFloat()

            if (timeLeft == 0) {
                stage = when (stage) {
                    HikeStage.WAITING_FOR_OTHERS -> HikeStage.ENTERING_LAYER
                    HikeStage.ENTERING_LAYER -> HikeStage.IN_LAYER
                    HikeStage.IN_LAYER -> HikeStage.LEAVING_LAYER
                    HikeStage.LEAVING_LAYER -> HikeStage.HIKE_COMPLETE
                    HikeStage.HIKE_COMPLETE -> HikeStage.WAITING_FOR_OTHERS // Loop back to waiting for others
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (stage) {
            HikeStage.WAITING_FOR_OTHERS -> CircularTimerWithInnerContent(
                progress = progress,
                currentTime = timerValue,
                label = "Waiting for others to join...",
                information = "2/3 members have joined"
            )
            HikeStage.ENTERING_LAYER -> CircularTimerWithInnerContent(
                progress = progress,
                currentTime = timerValue,
                label = "Entering Layer"
            )
            HikeStage.IN_LAYER -> CircularTimerWithInnerContent(
                progress = progress,
                currentTime = timerValue,
                label = "In Layer"
            )
            HikeStage.LEAVING_LAYER -> CircularTimerWithInnerContent(
                progress = progress,
                currentTime = timerValue,
                label = "Leaving Layer"
            )
            HikeStage.HIKE_COMPLETE -> CircularTimerWithInnerContent(
                progress = progress,
                currentTime = timerValue,
                label = "Hike Complete",
                isButtonVisible = true,
                buttonLabel = "Finish Hike",
                onClickButton = {
                    stage = HikeStage.WAITING_FOR_OTHERS
                    timerValue = DEFAULT_TIME_LEFT
                    progress = 0f
                }
            )
        }
    }
}

@Composable
fun CircularTimerWithInnerContent(
    progress: Float,
    currentTime: Int,
    label: String,
    information : String = "",
    isButtonVisible: Boolean = false,
    buttonLabel : String = "Continue",
    onClickButton: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        CircularTimer(progress = progress, modifier = Modifier.size(CIRCLE_SIZE.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Text(
                text = "${currentTime}s",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
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
                    onClick = { onClickButton },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39CCCC)),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        buttonLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
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

suspend fun startCountdown(initialTime: Int, onTick: (Int) -> Unit) {
    var timeLeft = initialTime
    while (timeLeft > 0) {
        delay(1000L)
        timeLeft--
        onTick(timeLeft)
    }
}

@Preview
@Composable
fun HikeStagesScreenPreview() {
    MultiStageScreen()
}