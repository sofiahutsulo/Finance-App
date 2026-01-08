package com.finance.manager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finance.manager.ui.components.AddTransactionBottomSheet
import com.finance.manager.ui.components.BalanceCard
import com.finance.manager.ui.components.MonthStatsCard
import com.finance.manager.ui.components.TransactionItem
import com.finance.manager.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val addTransactionState by viewModel.addTransactionState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }


    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            viewModel.initAddTransactionForm()
        }
    }

    Scaffold(
        floatingActionButton = {

            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Додати транзакцію"
                )
            }
        }
    ) { padding ->
        if (uiState.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    BalanceCard(balance = uiState.totalBalance)
                }


                item {
                    MonthStatsCard(
                        income = uiState.monthIncome,
                        expense = uiState.monthExpense
                    )
                }


                item {
                    if (uiState.recentTransactions.isNotEmpty()) {
                        Text(
                            text = "Останні транзакції",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }


                if (uiState.recentTransactions.isEmpty()) {
                    item {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Поки немає транзакцій",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Натисніть + щоб додати першу транзакцію",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(uiState.recentTransactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,

                            categoryName = "Категорія",
                            categoryIcon = "restaurant",
                            categoryColor = "#FF6B6B"
                        )
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }


        if (showBottomSheet) {
            AddTransactionBottomSheet(
                state = addTransactionState,
                accounts = uiState.accounts,
                onTypeChange = viewModel::onTransactionTypeChange,
                onAmountChange = viewModel::onAmountChange,
                onAccountSelect = viewModel::onAccountSelect,
                onCategorySelect = viewModel::onCategorySelect,
                onNoteChange = viewModel::onNoteChange,
                onDateChange = viewModel::onDateChange,
                onSave = {
                    viewModel.saveTransaction {
                        showBottomSheet = false
                    }
                },
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}
