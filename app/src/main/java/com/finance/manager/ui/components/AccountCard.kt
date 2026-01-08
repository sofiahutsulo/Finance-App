package com.finance.manager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.manager.domain.model.Account
import com.finance.manager.domain.model.AccountType
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AccountCard(
    account: Account,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = getGradientColors(account.color)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getAccountIcon(account.type),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }


                    Text(
                        text = getAccountTypeName(account.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }


                Column {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formatCurrency(account.balance),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


private fun getGradientColors(colorHex: String): List<Color> {
    val baseColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color(0xFF667EEA)
    }


    return listOf(
        baseColor,
        baseColor.copy(
            red = (baseColor.red * 0.8f).coerceAtLeast(0f),
            green = (baseColor.green * 0.8f).coerceAtLeast(0f),
            blue = (baseColor.blue * 0.8f).coerceAtLeast(0f)
        )
    )
}


private fun getAccountIcon(type: AccountType) = when (type) {
    AccountType.CASH -> Icons.Default.Money
    AccountType.CARD -> Icons.Default.CreditCard
    AccountType.BANK -> Icons.Default.AccountBalanceWallet
}


private fun getAccountTypeName(type: AccountType) = when (type) {
    AccountType.CASH -> "Готівка"
    AccountType.CARD -> "Картка"
    AccountType.BANK -> "Рахунок"
}


private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
    return formatter.format(amount).replace("UAH", "₴")
}
