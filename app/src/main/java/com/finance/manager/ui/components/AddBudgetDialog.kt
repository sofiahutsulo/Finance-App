package com.finance.manager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.finance.manager.domain.model.BudgetPeriod
import com.finance.manager.ui.viewmodel.AddBudgetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    state: AddBudgetState,
    onCategorySelect: (Long) -> Unit,
    onLimitAmountChange: (String) -> Unit,
    onPeriodChange: (BudgetPeriod) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Додати бюджет",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )


                Text(
                    text = "Категорія",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.expenseCategories) { category ->
                        CategoryChip(
                            categoryName = category.name,
                            categoryIcon = category.icon,
                            categoryColor = category.color,
                            isSelected = state.selectedCategoryId == category.id,
                            onClick = { onCategorySelect(category.id) }
                        )
                    }
                }

                if (state.categoryError != null) {
                    Text(
                        text = state.categoryError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }


                OutlinedTextField(
                    value = state.limitAmount,
                    onValueChange = onLimitAmountChange,
                    label = { Text("Ліміт суми (₴)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = state.amountError != null,
                    supportingText = state.amountError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )


                Text(
                    text = "Період",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    FilterChip(
                        selected = state.period == BudgetPeriod.MONTH,
                        onClick = { onPeriodChange(BudgetPeriod.MONTH) },
                        label = { Text("Місяць") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = state.period == BudgetPeriod.WEEK,
                        onClick = { onPeriodChange(BudgetPeriod.WEEK) },
                        label = { Text("Тиждень") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Скасувати")
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Зберегти")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryChip(
    categoryName: String,
    categoryIcon: String,
    categoryColor: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = getCategoryEmoji(categoryIcon),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    )
}


private fun getCategoryEmoji(icon: String): String {
    return when (icon) {
        "restaurant" -> "🍽️"
        "local_grocery_store" -> "🛒"
        "directions_car" -> "🚗"
        "home" -> "🏠"
        "favorite" -> "❤️"
        "school" -> "📚"
        "sports_esports" -> "🎮"
        "shopping_bag" -> "🛍️"
        "local_hospital" -> "🏥"
        "flight" -> "✈️"
        "phone_android" -> "📱"
        "checkroom" -> "👕"
        "palette" -> "🎨"
        "pets" -> "🐾"
        "savings" -> "💰"
        "card_giftcard" -> "🎁"
        "business_center" -> "💼"
        "attach_money" -> "💵"
        else -> "📦"
    }
}
