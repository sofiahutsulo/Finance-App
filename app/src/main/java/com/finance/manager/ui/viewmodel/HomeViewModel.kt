package com.finance.manager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.manager.data.local.AuthDataStore
import com.finance.manager.data.repository.AccountRepository
import com.finance.manager.data.repository.CategoryRepository
import com.finance.manager.data.repository.TransactionRepository
import com.finance.manager.domain.model.Account
import com.finance.manager.domain.model.Category
import com.finance.manager.domain.model.Transaction
import com.finance.manager.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


data class HomeUiState(
    val accounts: List<Account> = emptyList(),
    val totalBalance: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val isLoading: Boolean = true
)


data class AddTransactionState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val selectedAccountId: Long? = null,
    val selectedCategoryId: Long? = null,
    val note: String = "",
    val date: Date = Date(),
    val categories: List<Category> = emptyList(),
    val amountError: String? = null,
    val accountError: String? = null,
    val categoryError: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {


    private val currentUserId: Flow<Long> = authDataStore.userId.map { it ?: 1L }


    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            currentUserId.collectLatest { userId ->

                combine(
                    accountRepository.getAllAccounts(userId),
                    transactionRepository.getRecentTransactions(userId, limit = 5),
                    getMonthIncomeFlow(userId),
                    getMonthExpenseFlow(userId)
                ) { accounts, transactions, income, expense ->
                    HomeUiState(
                        accounts = accounts,
                        totalBalance = calculateTotalBalance(accounts),
                        recentTransactions = transactions,
                        monthIncome = income,
                        monthExpense = expense,
                        isLoading = false
                    )
                }.collectLatest { state ->
                    _uiState.value = state
                }
            }
        }
    }


    private fun calculateTotalBalance(accounts: List<Account>): Double {
        return accounts.sumOf { it.balance }
    }


    private fun getMonthIncomeFlow(userId: Long): Flow<Double> {
        val (startDate, endDate) = getCurrentMonthDateRange()
        return transactionRepository.getSumByType(
            userId = userId,
            type = TransactionType.INCOME,
            startDate = startDate,
            endDate = endDate
        )
    }


    private fun getMonthExpenseFlow(userId: Long): Flow<Double> {
        val (startDate, endDate) = getCurrentMonthDateRange()
        return transactionRepository.getSumByType(
            userId = userId,
            type = TransactionType.EXPENSE,
            startDate = startDate,
            endDate = endDate
        )
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


    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadData()
    }


    private val _addTransactionState = MutableStateFlow(AddTransactionState())
    val addTransactionState: StateFlow<AddTransactionState> = _addTransactionState.asStateFlow()


    fun onTransactionTypeChange(type: TransactionType) {
        _addTransactionState.value = _addTransactionState.value.copy(
            type = type,
            selectedCategoryId = null
        )
        loadCategoriesForType(type)
    }

    private fun loadCategoriesForType(type: TransactionType) {
        viewModelScope.launch {
            categoryRepository.getCategoriesByType(type).collectLatest { categories ->
                _addTransactionState.value = _addTransactionState.value.copy(
                    categories = categories
                )
            }
        }
    }


    fun initAddTransactionForm() {
        _addTransactionState.value = AddTransactionState()
        loadCategoriesForType(TransactionType.EXPENSE)


        viewModelScope.launch {
            currentUserId.collectLatest { userId ->
                accountRepository.getAllAccounts(userId).collectLatest { accounts ->
                    if (accounts.isNotEmpty() && _addTransactionState.value.selectedAccountId == null) {
                        _addTransactionState.value = _addTransactionState.value.copy(
                            selectedAccountId = accounts.first().id
                        )
                    }
                }
            }
        }
    }

    fun onAmountChange(amount: String) {
        _addTransactionState.value = _addTransactionState.value.copy(
            amount = amount,
            amountError = null
        )
    }

    fun onAccountSelect(accountId: Long) {
        _addTransactionState.value = _addTransactionState.value.copy(
            selectedAccountId = accountId,
            accountError = null
        )
    }

    fun onCategorySelect(categoryId: Long) {
        _addTransactionState.value = _addTransactionState.value.copy(
            selectedCategoryId = categoryId,
            categoryError = null
        )
    }

    fun onNoteChange(note: String) {
        _addTransactionState.value = _addTransactionState.value.copy(note = note)
    }

    fun onDateChange(date: Date) {
        _addTransactionState.value = _addTransactionState.value.copy(date = date)
    }


    fun saveTransaction(onSuccess: () -> Unit) {
        val state = _addTransactionState.value


        val amountValue = state.amount.toDoubleOrNull()
        val amountError = when {
            state.amount.isBlank() -> "Введіть суму"
            amountValue == null -> "Невірний формат суми"
            amountValue <= 0 -> "Сума має бути більше 0"
            else -> null
        }

        val accountError = if (state.selectedAccountId == null) "Виберіть рахунок" else null
        val categoryError = if (state.selectedCategoryId == null) "Виберіть категорію" else null

        if (amountError != null || accountError != null || categoryError != null) {
            _addTransactionState.value = state.copy(
                amountError = amountError,
                accountError = accountError,
                categoryError = categoryError
            )
            return
        }


        viewModelScope.launch {
            try {
                currentUserId.first { userId ->
                    val transaction = Transaction(
                        accountId = state.selectedAccountId!!,
                        categoryId = state.selectedCategoryId!!,
                        amount = amountValue!!,
                        date = state.date,
                        note = state.note,
                        type = state.type
                    )

                    transactionRepository.addTransaction(transaction)


                    val account = accountRepository.getAccountById(state.selectedAccountId!!)
                    if (account != null) {
                        val newBalance = if (state.type == TransactionType.INCOME) {
                            account.balance + amountValue!!
                        } else {
                            account.balance - amountValue!!
                        }
                        accountRepository.updateAccount(account.copy(balance = newBalance))
                    }


                    _addTransactionState.value = AddTransactionState()
                    onSuccess()
                    true
                }
            } catch (e: Exception) {
                _addTransactionState.value = state.copy(
                    amountError = "Помилка збереження: ${e.message}"
                )
            }
        }
    }
}
