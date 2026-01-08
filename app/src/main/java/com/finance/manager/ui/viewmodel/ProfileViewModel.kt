package com.finance.manager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.manager.data.local.AuthDataStore
import com.finance.manager.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val currency: String = "UAH",
    val theme: String = "system",
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val userRepository: UserRepository
) : ViewModel() {


    val uiState: StateFlow<ProfileUiState> = combine(
        authDataStore.userName,
        authDataStore.userEmail,
        authDataStore.currency,
        authDataStore.theme
    ) { userName, email, currency, theme ->
        ProfileUiState(
            userName = userName ?: "Користувач",
            userEmail = email ?: "",
            currency = currency,
            theme = theme
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true)
    )


    fun updateCurrency(currency: String) {
        viewModelScope.launch {
            authDataStore.saveCurrency(currency)
        }
    }

    
    fun updateTheme(theme: String) {
        viewModelScope.launch {
            authDataStore.saveTheme(theme)
        }
    }


    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authDataStore.logout()
            onLogoutComplete()
        }
    }
}
