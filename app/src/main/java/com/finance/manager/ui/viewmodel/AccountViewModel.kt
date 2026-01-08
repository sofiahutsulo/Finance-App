package com.finance.manager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.manager.data.local.AuthDataStore
import com.finance.manager.data.repository.AccountRepository
import com.finance.manager.domain.model.Account
import com.finance.manager.domain.model.AccountType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AccountsUiState(
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = true
)


data class AddAccountState(
    val name: String = "",
    val balance: String = "",
    val type: AccountType = AccountType.CARD,
    val color: String = "#667EEA",
    val icon: String = "account_balance_wallet",
    val nameError: String? = null,
    val balanceError: String? = null
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val authDataStore: AuthDataStore
) : ViewModel() {


    private val currentUserId: Flow<Long> = authDataStore.userId.map { it ?: 1L }


    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()


    private val _addAccountState = MutableStateFlow(AddAccountState())
    val addAccountState: StateFlow<AddAccountState> = _addAccountState.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            currentUserId.collectLatest { userId ->
                accountRepository.getAllAccounts(userId).collectLatest { accounts ->
                    _uiState.value = AccountsUiState(
                        accounts = accounts,
                        isLoading = false
                    )
                }
            }
        }
    }


    fun initAddAccountForm() {
        _addAccountState.value = AddAccountState()
    }

    fun onNameChange(name: String) {
        _addAccountState.value = _addAccountState.value.copy(
            name = name,
            nameError = null
        )
    }

    fun onBalanceChange(balance: String) {
        _addAccountState.value = _addAccountState.value.copy(
            balance = balance,
            balanceError = null
        )
    }

    fun onTypeChange(type: AccountType) {
        _addAccountState.value = _addAccountState.value.copy(type = type)
    }

    fun onColorChange(color: String) {
        _addAccountState.value = _addAccountState.value.copy(color = color)
    }

    fun onIconChange(icon: String) {
        _addAccountState.value = _addAccountState.value.copy(icon = icon)
    }


    fun saveAccount(onSuccess: () -> Unit) {
        val state = _addAccountState.value


        val nameError = when {
            state.name.isBlank() -> "Введіть назву рахунку"
            state.name.length < 2 -> "Назва має бути мінімум 2 символи"
            else -> null
        }

        val balanceValue = state.balance.toDoubleOrNull()
        val balanceError = when {
            state.balance.isBlank() -> "Введіть початковий баланс"
            balanceValue == null -> "Невірний формат суми"
            balanceValue < 0 -> "Баланс не може бути від'ємним"
            else -> null
        }

        if (nameError != null || balanceError != null) {
            _addAccountState.value = state.copy(
                nameError = nameError,
                balanceError = balanceError
            )
            return
        }


        viewModelScope.launch {
            try {
                currentUserId.first { userId ->
                    val account = Account(
                        userId = userId,
                        name = state.name,
                        balance = balanceValue!!,
                        type = state.type,
                        color = state.color,
                        icon = state.icon
                    )

                    accountRepository.addAccount(account)


                    _addAccountState.value = AddAccountState()
                    onSuccess()
                    true
                }
            } catch (e: Exception) {
                _addAccountState.value = state.copy(
                    nameError = "Помилка збереження: ${e.message}"
                )
            }
        }
    }


    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            try {
                accountRepository.deleteAccount(account)
            } catch (e: Exception) {

            }
        }
    }


    fun updateAccount(account: Account) {
        viewModelScope.launch {
            try {
                accountRepository.updateAccount(account)
            } catch (e: Exception) {

            }
        }
    }
}
