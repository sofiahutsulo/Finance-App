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
import com.finance.manager.ui.components.AddBudgetDialog
import com.finance.manager.ui.components.BudgetItem
import com.finance.manager.ui.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val addBudgetState by viewModel.addBudgetState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(showDialog) {
        if (showDialog) {
            viewModel.initAddBudgetForm()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Бюджети",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Додати бюджет"
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
        } else if (uiState.budgets.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "📊",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "Поки немає бюджетів",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Натисніть + щоб створити бюджет для категорії та контролювати витрати",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.budgets,
                    key = { it.budget.id }
                ) { budgetWithSpent ->
                    BudgetItem(
                        budgetWithSpent = budgetWithSpent,
                        onDelete = {
                            viewModel.deleteBudget(budgetWithSpent.budget)
                        }
                    )
                }


                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }


        if (showDialog) {
            AddBudgetDialog(
                state = addBudgetState,
                onCategorySelect = viewModel::onCategorySelect,
                onLimitAmountChange = viewModel::onLimitAmountChange,
                onPeriodChange = viewModel::onPeriodChange,
                onSave = {
                    viewModel.saveBudget {
                        showDialog = false
                    }
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}
