package com.finance.manager.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.manager.data.local.AuthDataStore
import com.finance.manager.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)


data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
)


data class RegisterFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val userRepository: UserRepository,
    private val apiService: com.finance.manager.data.remote.ApiService
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginForm = MutableStateFlow(LoginFormState())
    val loginForm: StateFlow<LoginFormState> = _loginForm.asStateFlow()

    private val _registerForm = MutableStateFlow(RegisterFormState())
    val registerForm: StateFlow<RegisterFormState> = _registerForm.asStateFlow()


    val isLoggedIn = authDataStore.isLoggedIn


    fun onLoginEmailChange(email: String) {
        _loginForm.value = _loginForm.value.copy(
            email = email,
            emailError = null
        )
    }

    fun onLoginPasswordChange(password: String) {
        _loginForm.value = _loginForm.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun login() {

        val emailError = validateEmail(_loginForm.value.email)
        val passwordError = validatePassword(_loginForm.value.password)

        if (emailError != null || passwordError != null) {
            _loginForm.value = _loginForm.value.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            try {
                Log.d("AuthViewModel", "Відправляємо запит на сервер для логіну: ${_loginForm.value.email}")


                val request = com.finance.manager.data.remote.dto.LoginRequest(
                    email = _loginForm.value.email,
                    password = _loginForm.value.password
                )

                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    Log.d("AuthViewModel", "Успішний логін! Token: ${loginResponse.token}")


                    authDataStore.saveUserData(
                        userId = loginResponse.user.id,
                        email = loginResponse.user.email,
                        name = loginResponse.user.name
                    )


                    _authState.value = AuthState(isSuccess = true)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Невірний email або пароль"
                    Log.e("AuthViewModel", "Помилка логіну: $errorMessage")
                    _authState.value = AuthState(error = "Невірний email або пароль")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Помилка входу", e)
                _authState.value = AuthState(error = "Помилка з'єднання з сервером: ${e.message}")
            }
        }
    }


    fun onRegisterNameChange(name: String) {
        _registerForm.value = _registerForm.value.copy(
            name = name,
            nameError = null
        )
    }

    fun onRegisterEmailChange(email: String) {
        _registerForm.value = _registerForm.value.copy(
            email = email,
            emailError = null
        )
    }

    fun onRegisterPasswordChange(password: String) {
        _registerForm.value = _registerForm.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun onRegisterConfirmPasswordChange(confirmPassword: String) {
        _registerForm.value = _registerForm.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )
    }

    fun register() {

        val nameError = validateName(_registerForm.value.name)
        val emailError = validateEmail(_registerForm.value.email)
        val passwordError = validatePassword(_registerForm.value.password)
        val confirmPasswordError = validateConfirmPassword(
            _registerForm.value.password,
            _registerForm.value.confirmPassword
        )

        if (nameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            _registerForm.value = _registerForm.value.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            try {
                Log.d("AuthViewModel", "Відправляємо запит на сервер для реєстрації: ${_registerForm.value.email}")


                val request = com.finance.manager.data.remote.dto.RegisterRequest(
                    name = _registerForm.value.name,
                    email = _registerForm.value.email,
                    password = _registerForm.value.password
                )

                val response = apiService.register(request)

                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!
                    Log.d("AuthViewModel", "Успішна реєстрація! Token: ${registerResponse.token}")


                    authDataStore.saveUserData(
                        userId = registerResponse.user.id,
                        email = registerResponse.user.email,
                        name = registerResponse.user.name
                    )


                    _authState.value = AuthState(isSuccess = true)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Помилка реєстрації"
                    Log.e("AuthViewModel", "Помилка реєстрації: $errorMessage")
                    _authState.value = AuthState(error = "Користувач з таким email вже існує")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Помилка реєстрації", e)
                _authState.value = AuthState(error = "Помилка з'єднання з сервером: ${e.message}")
            }
        }
    }


    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email не може бути порожнім"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Невірний формат email"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Пароль не може бути порожнім"
            password.length < 6 -> "Пароль має бути мінімум 6 символів"
            else -> null
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Ім'я не може бути порожнім"
            name.length < 2 -> "Ім'я має бути мінімум 2 символи"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Підтвердіть пароль"
            password != confirmPassword -> "Паролі не співпадають"
            else -> null
        }
    }


    fun resetAuthState() {
        _authState.value = AuthState()
    }


    fun logout() {
        viewModelScope.launch {
            authDataStore.logout()
        }
    }
}
