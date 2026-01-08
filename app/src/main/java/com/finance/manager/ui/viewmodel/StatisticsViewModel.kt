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


enum class StatisticsPeriod {
    WEEK,
    MONTH,     
    YEAR
}


data class CategoryExpense(
    val category: Category,
    val amount: Double,
    val percentage: Float
)


data class PeriodData(
    val label: String,
    val income: Double,
    val expense: Double
)


data class TotalStats(
    val totalIncome: Double,
    val totalExpense: Double,
    val difference: Double
)


data class StatisticsUiState(
    val period: StatisticsPeriod = StatisticsPeriod.MONTH,
    val categoryExpenses: List<CategoryExpense> = emptyList(),
    val periodData: List<PeriodData> = emptyList(),
    val totalStats: TotalStats = TotalStats(0.0, 0.0, 0.0),
    val topCategories: List<CategoryExpense> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {

    private val currentUserId: Flow<Long> = authDataStore.userId.map { it ?: 1L }

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

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
                    calculateStatistics(transactions, categories, _uiState.value.period)
                }.collectLatest { state ->
                    _uiState.value = state
                }
            }
        }
    }


    fun onPeriodChange(period: StatisticsPeriod) {
        viewModelScope.launch {
            val userId = currentUserId.first()
            val transactions = transactionRepository.getAllTransactions(userId).first()
            val categories = categoryRepository.getAllCategories().first()
            _uiState.value = calculateStatistics(transactions, categories, period)
        }
    }


    private fun calculateStatistics(
        transactions: List<Transaction>,
        categories: List<Category>,
        period: StatisticsPeriod
    ): StatisticsUiState {

        val (startDate, endDate) = getDateRangeForPeriod(period)
        val filteredTransactions = transactions.filter { it.date in startDate..endDate }


        val categoryExpenses = calculateCategoryExpenses(filteredTransactions, categories)


        val periodData = calculatePeriodData(filteredTransactions, period)


        val totalIncome = filteredTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val totalExpense = filteredTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }


        val topCategories = categoryExpenses.take(5)

        return StatisticsUiState(
            period = period,
            categoryExpenses = categoryExpenses,
            periodData = periodData,
            totalStats = TotalStats(totalIncome, totalExpense, totalIncome - totalExpense),
            topCategories = topCategories,
            isLoading = false
        )
    }


    private fun calculateCategoryExpenses(
        transactions: List<Transaction>,
        categories: List<Category>
    ): List<CategoryExpense> {

        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        val totalExpense = expenses.sumOf { it.amount }

        if (totalExpense == 0.0) return emptyList()


        val expensesByCategory = expenses.groupBy { it.categoryId }

        return expensesByCategory.map { (categoryId, transactions) ->
            val category = categories.find { it.id == categoryId }
                ?: Category(id = categoryId, name = "Інше", icon = "help", color = "#999999", type = TransactionType.EXPENSE)
            val amount = transactions.sumOf { it.amount }
            val percentage = (amount / totalExpense * 100).toFloat()

            CategoryExpense(category, amount, percentage)
        }.sortedByDescending { it.amount }
    }


    private fun calculatePeriodData(
        transactions: List<Transaction>,
        period: StatisticsPeriod
    ): List<PeriodData> {
        return when (period) {
            StatisticsPeriod.WEEK -> calculateWeekData(transactions)
            StatisticsPeriod.MONTH -> calculateMonthData(transactions)
            StatisticsPeriod.YEAR -> calculateYearData(transactions)
        }
    }


    private fun calculateWeekData(transactions: List<Transaction>): List<PeriodData> {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK)


        val daysOfWeek = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд")

        return daysOfWeek.mapIndexed { index, dayName ->
            val dayOfWeek = if (index == 6) Calendar.SUNDAY else Calendar.MONDAY + index


            val dayTransactions = transactions.filter { transaction ->
                val transactionCalendar = Calendar.getInstance()
                transactionCalendar.time = transaction.date
                transactionCalendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek
            }

            val income = dayTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            val expense = dayTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            PeriodData(dayName, income, expense)
        }
    }


    private fun calculateMonthData(transactions: List<Transaction>): List<PeriodData> {
        val calendar = Calendar.getInstance()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        return (1..daysInMonth).map { day ->

            val dayTransactions = transactions.filter { transaction ->
                val transactionCalendar = Calendar.getInstance()
                transactionCalendar.time = transaction.date
                transactionCalendar.get(Calendar.DAY_OF_MONTH) == day
            }

            val income = dayTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            val expense = dayTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            PeriodData(day.toString(), income, expense)
        }
    }


    private fun calculateYearData(transactions: List<Transaction>): List<PeriodData> {
        val monthNames = listOf(
            "Січ", "Лют", "Бер", "Кві", "Тра", "Чер",
            "Лип", "Сер", "Вер", "Жов", "Лис", "Гру"
        )

        return monthNames.mapIndexed { index, monthName ->

            val monthTransactions = transactions.filter { transaction ->
                val transactionCalendar = Calendar.getInstance()
                transactionCalendar.time = transaction.date
                transactionCalendar.get(Calendar.MONTH) == index
            }

            val income = monthTransactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            val expense = monthTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            PeriodData(monthName, income, expense)
        }
    }


    private fun getDateRangeForPeriod(period: StatisticsPeriod): Pair<Date, Date> {
        val calendar = Calendar.getInstance()

        return when (period) {
            StatisticsPeriod.WEEK -> {

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
            StatisticsPeriod.MONTH -> {

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
            StatisticsPeriod.YEAR -> {

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
}

