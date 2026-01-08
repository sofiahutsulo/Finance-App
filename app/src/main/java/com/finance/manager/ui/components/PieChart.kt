package com.finance.manager.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.manager.ui.viewmodel.CategoryExpense
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PieChart(
    data: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {

        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Немає даних для відображення",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2
            val centerX = size.width / 2
            val centerY = size.height / 2

            var startAngle = -90f

            data.forEach { categoryExpense ->
                val sweepAngle = (categoryExpense.percentage / 100f) * 360f
                val color = parseColor(categoryExpense.category.color)


                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2)
                )


                if (categoryExpense.percentage > 5f) {
                    val angle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                    val textRadius = radius * 0.7
                    val textX = centerX + (textRadius * cos(angle)).toFloat()
                    val textY = centerY + (textRadius * sin(angle)).toFloat()

                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            this.color = android.graphics.Color.WHITE
                            textAlign = Paint.Align.CENTER
                            textSize = 32f
                            isFakeBoldText = true
                            setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                        }

                        canvas.nativeCanvas.drawText(
                            "${categoryExpense.percentage.toInt()}%",
                            textX,
                            textY + 12f,
                            paint
                        )
                    }
                }

                startAngle += sweepAngle
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.take(5).forEach { categoryExpense ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(2.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(color = parseColor(categoryExpense.category.color))
                        }
                    }


                    Text(
                        text = categoryExpense.category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )


                    Text(
                        text = "${categoryExpense.percentage.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


private fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color.Gray
    }
}
