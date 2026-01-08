package com.finance.manager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finance.manager.domain.model.Transaction
import com.finance.manager.domain.model.TransactionType
import com.finance.manager.ui.components.TransactionItem
import com.finance.manager.ui.utils.groupByDate
import com.finance.manager.ui.viewmodel.PeriodFilter
import com.finance.manager.ui.viewmodel.TransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dismissState = rememberDismissState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Транзакції",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )


                FilterSection(
                    filters = uiState.filters,
                    categories = uiState.categories,
                    onTypeChange = viewModel::onTypeFilterChange,
                    onCategoryChange = viewModel::onCategoryFilterChange,
                    onPeriodChange = viewModel::onPeriodFilterChange,
                    onResetFilters = viewModel::resetFilters
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
        } else if (uiState.transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Транзакцій не знайдено",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Спробуйте змінити фільтри",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {

            val groupedTransactions = uiState.transactions.groupByDate { it.date }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                groupedTransactions.forEach { (dateGroup, transactions) ->

                    item {
                        Text(
                            text = dateGroup,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }


                    items(
                        items = transactions,
                        key = { it.id }
                    ) { transaction ->
                        SwipeToDeleteItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) },
                            content = {

                                val category = uiState.categories.find { it.id == transaction.categoryId }

                                TransactionItem(
                                    transaction = transaction,
                                    categoryName = category?.name ?: "Категорія",
                                    categoryIcon = category?.icon ?: "restaurant",
                                    categoryColor = category?.color ?: "#FF6B6B",
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Divider(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                        )
                    }


                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteItem(
    transaction: Transaction,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Видалити",
                    tint = Color.White
                )
            }
        },
        dismissContent = { content() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    filters: com.finance.manager.ui.viewmodel.TransactionFilters,
    categories: List<com.finance.manager.domain.model.Category>,
    onTypeChange: (TransactionType?) -> Unit,
    onCategoryChange: (Long?) -> Unit,
    onPeriodChange: (PeriodFilter) -> Unit,
    onResetFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp)
    ) {

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = filters.type == null,
                    onClick = { onTypeChange(null) },
                    label = { Text("Всі") }
                )
            }
            item {
                FilterChip(
                    selected = filters.type == TransactionType.INCOME,
                    onClick = { onTypeChange(TransactionType.INCOME) },
                    label = { Text("Доходи") }
                )
            }
            item {
                FilterChip(
                    selected = filters.type == TransactionType.EXPENSE,
                    onClick = { onTypeChange(TransactionType.EXPENSE) },
                    label = { Text("Витрати") }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))


        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = filters.period == PeriodFilter.ALL,
                    onClick = { onPeriodChange(PeriodFilter.ALL) },
                    label = { Text("Всі") }
                )
            }
            item {
                FilterChip(
                    selected = filters.period == PeriodFilter.THIS_WEEK,
                    onClick = { onPeriodChange(PeriodFilter.THIS_WEEK) },
                    label = { Text("Цього тижня") }
                )
            }
            item {
                FilterChip(
                    selected = filters.period == PeriodFilter.THIS_MONTH,
                    onClick = { onPeriodChange(PeriodFilter.THIS_MONTH) },
                    label = { Text("Цього місяця") }
                )
            }
            item {
                FilterChip(
                    selected = filters.period == PeriodFilter.LAST_MONTH,
                    onClick = { onPeriodChange(PeriodFilter.LAST_MONTH) },
                    label = { Text("Минулого місяця") }
                )
            }
        }


        if (filters.type != null || filters.categoryId != null || filters.period != PeriodFilter.ALL) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onResetFilters,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("Скинути фільтри")
            }
        }

        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}
