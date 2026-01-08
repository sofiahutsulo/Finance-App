package com.finance.manager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.manager.data.local.AuthDataStore
import com.finance.manager.data.repository.CategoryRepository
import com.finance.manager.data.repository.TransactionRepository
import com.finance.manager.domain.model.Category
import com.finance.manager.domain.model.Transaction
import com.finance.manager.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


enum class PeriodFilter {
    ALL,
    THIS_WEEK,
    THIS_MONTH,
    LAST_MONTH
}


data class TransactionFilters(
    val type: TransactionType? = null,
    val categoryId: Long? = null,
    val period: PeriodFilter = PeriodFilter.ALL
)


data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filters: TransactionFilters = TransactionFilters(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val currentUserId: Flow<Long> = authDataStore.userId.map { it ?: 1L }

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            currentUserId.collectLatest { userId ->
                combine(
                    transactionRepository.getAllTransactions(userId),
                    categoryRepository.getAllCategories()
                ) { transactions, categories ->
                    val filteredTransactions = applyFilters(transactions, _uiState.value.filters)
                    TransactionsUiState(
                        transactions = filteredTransactions,
                        categories = categories,
                        filters = _uiState.value.filters,
                        isLoading = false
                    )
                }.collectLatest { state ->
                    _uiState.value = state
                }
            }
        }
    }


    private fun applyFilters(
        transactions: List<Transaction>,
        filters: TransactionFilters
    ): List<Transaction> {
        var filtered = transactions


        if (filters.type != null) {
            filtered = filtered.filter { it.type == filters.type }
        }


        if (filters.categoryId != null) {
            filtered = filtered.filter { it.categoryId == filters.categoryId }
        }


        filtered = when (filters.period) {
            PeriodFilter.THIS_WEEK -> {
                val (start, end) = getCurrentWeekDateRange()
                filtered.filter { it.date in start..end }
            }
            PeriodFilter.THIS_MONTH -> {
                val (start, end) = getCurrentMonthDateRange()
                filtered.filter { it.date in start..end }
            }
            PeriodFilter.LAST_MONTH -> {
                val (start, end) = getLastMonthDateRange()
                filtered.filter { it.date in start..end }
            }
            PeriodFilter.ALL -> filtered
        }


        return filtered.sortedByDescending { it.date }
    }


    fun onTypeFilterChange(type: TransactionType?) {
        val newFilters = _uiState.value.filters.copy(type = type)
        updateFilters(newFilters)
    }


    fun onCategoryFilterChange(categoryId: Long?) {
        val newFilters = _uiState.value.filters.copy(categoryId = categoryId)
        updateFilters(newFilters)
    }


    fun onPeriodFilterChange(period: PeriodFilter) {
        val newFilters = _uiState.value.filters.copy(period = period)
        updateFilters(newFilters)
    }


    private fun updateFilters(filters: TransactionFilters) {
        viewModelScope.launch {
            currentUserId.first { userId ->
                transactionRepository.getAllTransactions(userId).first { transactions ->
                    val filteredTransactions = applyFilters(transactions, filters)
                    _uiState.value = _uiState.value.copy(
                        transactions = filteredTransactions,
                        filters = filters
                    )
                    true
                }
                true
            }
        }
    }


    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteTransaction(transaction)
            } catch (e: Exception) {

            }
        }
    }


    fun resetFilters() {
        updateFilters(TransactionFilters())
    }


    private fun getCurrentWeekDateRange(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()


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
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.time

        return Pair(startDate, endDate)
    }


    private fun getCurrentMonthDateRange(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()

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
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.time

        return Pair(startDate, endDate)
    }


    private fun getLastMonthDateRange(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)

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
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.time

        return Pair(startDate, endDate)
    }
}
