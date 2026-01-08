package com.finance.manager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.manager.data.local.AuthDataStore
import com.finance.manager.data.repository.BudgetRepository
import com.finance.manager.data.repository.CategoryRepository
import com.finance.manager.data.repository.TransactionRepository
import com.finance.manager.domain.model.Budget
import com.finance.manager.domain.model.BudgetPeriod
import com.finance.manager.domain.model.Category
import com.finance.manager.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


data class BudgetWithSpent(
    val budget: Budget,
    val category: Category,
    val currentSpent: Double,
    val percentage: Float,
    val isExceeded: Boolean
)


data class AddBudgetState(
    val selectedCategoryId: Long? = null,
    val limitAmount: String = "",
    val period: BudgetPeriod = BudgetPeriod.MONTH,
    val expenseCategories: List<Category> = emptyList(),
    val categoryError: String? = null,
    val amountError: String? = null
)


data class BudgetUiState(
    val budgets: List<BudgetWithSpent> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val currentUserId: Flow<Long> = authDataStore.userId.map { it ?: 1L }

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    private val _addBudgetState = MutableStateFlow(AddBudgetState())
    val addBudgetState: StateFlow<AddBudgetState> = _addBudgetState.asStateFlow()

    init {
        loadBudgets()
    }

    private fun loadBudgets() {
        viewModelScope.launch {
            currentUserId.collectLatest { userId ->
                combine(
                    budgetRepository.getAllBudgets(userId),
                    transactionRepository.getAllTransactions(userId),
                    categoryRepository.getAllCategories()
                ) { budgets, transactions, categories ->

                    val budgetsWithSpent = budgets.map { budget ->
                        calculateBudgetSpent(budget, transactions, categories)
                    }

                    BudgetUiState(
                        budgets = budgetsWithSpent,
                        isLoading = false
                    )
                }.collectLatest { state ->
                    _uiState.value = state
                }
            }
        }
    }


    private fun calculateBudgetSpent(
        budget: Budget,
        allTransactions: List<com.finance.manager.domain.model.Transaction>,
        categories: List<Category>
    ): BudgetWithSpent {
        val category = categories.find { it.id == budget.categoryId }
            ?: Category(
                id = budget.categoryId,
                name = "Невідома категорія",
                icon = "help",
                color = "#999999",
                type = TransactionType.EXPENSE
            )


        val (startDate, endDate) = getDateRangeForBudget(budget)


        val spent = allTransactions
            .filter { it.categoryId == budget.categoryId }
            .filter { it.type == TransactionType.EXPENSE }
            .filter { it.date in startDate..endDate }
            .sumOf { it.amount }

        val percentage = if (budget.amount > 0) {
            (spent / budget.amount * 100).toFloat()
        } else {
            0f
        }

        val isExceeded = spent > budget.amount

        return BudgetWithSpent(
            budget = budget,
            category = category,
            currentSpent = spent,
            percentage = percentage,
            isExceeded = isExceeded
        )
    }


    private fun getDateRangeForBudget(budget: Budget): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.time = budget.startDate

        return when (budget.period) {
            BudgetPeriod.MONTH -> {

                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time


                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val endDate = calendar.time

                Pair(startDate, endDate)
            }
            BudgetPeriod.WEEK -> {

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time


                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val endDate = calendar.time

                Pair(startDate, endDate)
            }
            BudgetPeriod.YEAR -> {

                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time


                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val endDate = calendar.time

                Pair(startDate, endDate)
            }
        }
    }


    fun initAddBudgetForm() {
        viewModelScope.launch {

            categoryRepository.getAllCategories().first { categories ->
                val expenseCategories = categories.filter { it.type == TransactionType.EXPENSE }
                _addBudgetState.value = AddBudgetState(
                    expenseCategories = expenseCategories
                )
                true
            }
        }
    }


    fun onCategorySelect(categoryId: Long) {
        _addBudgetState.value = _addBudgetState.value.copy(
            selectedCategoryId = categoryId,
            categoryError = null
        )
    }


    fun onLimitAmountChange(amount: String) {
        _addBudgetState.value = _addBudgetState.value.copy(
            limitAmount = amount,
            amountError = null
        )
    }


    fun onPeriodChange(period: BudgetPeriod) {
        _addBudgetState.value = _addBudgetState.value.copy(period = period)
    }


    fun saveBudget(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _addBudgetState.value


            var hasError = false

            if (state.selectedCategoryId == null) {
                _addBudgetState.value = state.copy(categoryError = "Оберіть категорію")
                hasError = true
            }

            val amountValue = state.limitAmount.toDoubleOrNull()
            if (amountValue == null || amountValue <= 0) {
                _addBudgetState.value = _addBudgetState.value.copy(
                    amountError = "Введіть коректну суму більше 0"
                )
                hasError = true
            }

            if (hasError) return@launch


            currentUserId.first { userId ->
                val budget = Budget(
                    userId = userId,
                    categoryId = state.selectedCategoryId!!,
                    amount = amountValue!!,
                    period = state.period,
                    startDate = Date()
                )

                budgetRepository.addBudget(budget)
                onSuccess()
                true
            }
        }
    }

    
    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budget)
            } catch (e: Exception) {

            }
        }
    }
}
