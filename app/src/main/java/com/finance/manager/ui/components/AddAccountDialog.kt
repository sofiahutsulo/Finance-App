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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.finance.manager.domain.model.AccountType
import com.finance.manager.ui.viewmodel.AddAccountState

@Composable
fun AddAccountDialog(
    state: AddAccountState,
    onNameChange: (String) -> Unit,
    onBalanceChange: (String) -> Unit,
    onTypeChange: (AccountType) -> Unit,
    onColorChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Новий рахунок",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )


                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text("Назва рахунку") },
                    isError = state.nameError != null,
                    supportingText = {
                        if (state.nameError != null) {
                            Text(state.nameError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )


                OutlinedTextField(
                    value = state.balance,
                    onValueChange = onBalanceChange,
                    label = { Text("Початковий баланс") },
                    leadingIcon = { Text("₴") },
                    isError = state.balanceError != null,
                    supportingText = {
                        if (state.balanceError != null) {
                            Text(state.balanceError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )


                Column {
                    Text(
                        text = "Тип рахунку",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccountType.values().forEach { type ->
                            AccountTypeChip(
                                type = type,
                                isSelected = state.type == type,
                                onClick = { onTypeChange(type) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }


                Column {
                    Text(
                        text = "Колір",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(predefinedColors) { color ->
                            ColorChip(
                                color = color,
                                isSelected = state.color == color,
                                onClick = { onColorChange(color) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Скасувати")
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Зберегти")
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountTypeChip(
    type: AccountType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeName = when (type) {
        AccountType.CASH -> "Готівка"
        AccountType.CARD -> "Картка"
        AccountType.BANK -> "Рахунок"
    }

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
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = typeName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ColorChip(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(color))
    } catch (e: Exception) {
        Color.Gray
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(parsedColor)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


private val predefinedColors = listOf(
    "#667EEA",
    "#764BA2",
    "#4ECDC4",
    "#44A08D",
    "#F093FB",
    "#F5576C",
    "#FFA751",
    "#FFE259",
    "#667EEA",
    "#5F72FF",
    "#4CAF50",
    "#FF6B6B"
)
