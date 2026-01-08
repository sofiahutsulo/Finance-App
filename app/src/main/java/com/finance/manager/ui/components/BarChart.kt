package com.finance.manager.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.manager.ui.theme.ExpenseColor
import com.finance.manager.ui.theme.IncomeColor
import com.finance.manager.ui.viewmodel.PeriodData

@Composable
fun BarChart(
    data: List<PeriodData>,
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

    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(12.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(color = IncomeColor)
                    }
                }
                Text(
                    text = "Доходи",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))


            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(12.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(color = ExpenseColor)
                    }
                }
                Text(
                    text = "Витрати",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        val maxValue = data.maxOf { maxOf(it.income, it.expense) }

        if (maxValue == 0.0) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Немає транзакцій за цей період",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return
        }


        val barWidth = 40f
        val spaceBetweenBars = 8f
        val groupSpacing = 32f
        val totalWidth = data.size * (barWidth * 2 + spaceBetweenBars + groupSpacing)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Canvas(
                modifier = Modifier
                    .width((totalWidth / 3).dp)
                    .height(220.dp)
                    .padding(horizontal = 8.dp)
            ) {
                val chartHeight = size.height - 40f

                data.forEachIndexed { index, periodData ->
                    val xOffset = index * (barWidth * 2 + spaceBetweenBars + groupSpacing)


                    val incomeHeight = if (maxValue > 0) {
                        (periodData.income / maxValue * chartHeight).toFloat()
                    } else 0f

                    drawRoundRect(
                        color = IncomeColor,
                        topLeft = Offset(xOffset, chartHeight - incomeHeight),
                        size = Size(barWidth, incomeHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )


                    val expenseHeight = if (maxValue > 0) {
                        (periodData.expense / maxValue * chartHeight).toFloat()
                    } else 0f

                    drawRoundRect(
                        color = ExpenseColor,
                        topLeft = Offset(xOffset + barWidth + spaceBetweenBars, chartHeight - expenseHeight),
                        size = Size(barWidth, expenseHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )


                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            color = android.graphics.Color.GRAY
                            textAlign = Paint.Align.CENTER
                            textSize = 28f
                        }

                        canvas.nativeCanvas.drawText(
                            periodData.label,
                            xOffset + barWidth + spaceBetweenBars / 2,
                            size.height - 8f,
                            paint
                        )
                    }
                }
            }
        }
    }
}
