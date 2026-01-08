package com.finance.manager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.finance.manager.domain.model.Account
import com.finance.manager.domain.model.Category
import com.finance.manager.domain.model.TransactionType
import com.finance.manager.ui.theme.*
import com.finance.manager.ui.viewmodel.AddTransactionState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    state: AddTransactionState,
    accounts: List<Account>,
    onTypeChange: (TransactionType) -> Unit,
    onAmountChange: (String) -> Unit,
    onAccountSelect: (Long) -> Unit,
    onCategorySelect: (Long) -> Unit,
    onNoteChange: (String) -> Unit,
    onDateChange: (Date) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Нова транзакція",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )


            TabRow(
                selectedTabIndex = if (state.type == TransactionType.EXPENSE) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = state.type == TransactionType.EXPENSE,
                    onClick = { onTypeChange(TransactionType.EXPENSE) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (state.type == TransactionType.EXPENSE)
                                ExpenseColor
                            else
                                Color.Transparent
                        )
                ) {
                    Text(
                        text = "Витрата",
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = if (state.type == TransactionType.EXPENSE)
                            Color.White
                        else
                            MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (state.type == TransactionType.EXPENSE)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                    )
                }

                Tab(
                    selected = state.type == TransactionType.INCOME,
                    onClick = { onTypeChange(TransactionType.INCOME) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (state.type == TransactionType.INCOME)
                                IncomeColor
                            else
                                Color.Transparent
                        )
                ) {
                    Text(
                        text = "Дохід",
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = if (state.type == TransactionType.INCOME)
                            Color.White
                        else
                            MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (state.type == TransactionType.INCOME)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                    )
                }
            }


            OutlinedTextField(
                value = state.amount,
                onValueChange = onAmountChange,
                label = { Text("Сума") },
                leadingIcon = { Text("₴", style = MaterialTheme.typography.headlineSmall) },
                isError = state.amountError != null,
                supportingText = {
                    if (state.amountError != null) {
                        Text(state.amountError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )


            Column {
                Text(
                    text = "Рахунок",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (accounts.isEmpty()) {
                    Text(
                        text = "Немає рахунків. Спочатку створіть рахунок.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        accounts.forEach { account ->
                            AccountChip(
                                account = account,
                                isSelected = state.selectedAccountId == account.id,
                                onClick = { onAccountSelect(account.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                if (state.accountError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.accountError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }


            Column {
                Text(
                    text = "Категорія",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (state.categories.isEmpty()) {
                    Text(
                        text = "Немає категорій для цього типу",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(state.categories) { category ->
                            CategoryChip(
                                category = category,
                                isSelected = state.selectedCategoryId == category.id,
                                onClick = { onCategorySelect(category.id) }
                            )
                        }
                    }
                }

                if (state.categoryError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.categoryError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }


            OutlinedTextField(
                value = state.note,
                onValueChange = onNoteChange,
                label = { Text("Нотатка (опціонально)") },
                leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )


            OutlinedTextField(
                value = formatDate(state.date),
                onValueChange = { },
                label = { Text("Дата") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                shape = RoundedCornerShape(12.dp),

            )


            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.type == TransactionType.EXPENSE)
                        ExpenseColor
                    else
                        IncomeColor
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Зберегти",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AccountChip(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = account.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val categoryColor = parseColor(category.color)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected)
                    categoryColor.copy(alpha = 0.3f)
                else
                    categoryColor.copy(alpha = 0.1f)
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) categoryColor else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(categoryColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = category.name,
                tint = categoryColor,
                modifier = Modifier.size(20.dp)
            )
        }


        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}


private fun parseColor(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color.Gray
    }
}


private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("d MMMM yyyy", Locale("uk", "UA"))
    return formatter.format(date)
}
