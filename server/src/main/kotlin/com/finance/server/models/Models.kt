package com.finance.server.models

import kotlinx.serialization.Serializable


@Serializable
data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)

@Serializable
data class AccountResponse(
    val id: Long,
    val userId: Long,
    val name: String,
    val balance: Double,
    val currency: String,
    val type: String,
    val color: String,
    val icon: String
)

@Serializable
data class CategoryResponse(
    val id: Long,
    val name: String,
    val type: String,
    val icon: String,
    val color: String
)

@Serializable
data class TransactionResponse(
    val id: Long,
    val accountId: Long,
    val categoryId: Long,
    val userId: Long,
    val amount: Double,
    val date: String,
    val note: String?,
    val type: String
)

@Serializable
data class BudgetResponse(
    val id: Long,
    val userId: Long,
    val categoryId: Long,
    val limitAmount: Double,
    val period: String,
    val startDate: String
)


@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: UserResponse
)

@Serializable
data class CreateAccountRequest(
    val name: String,
    val balance: Double = 0.0,
    val currency: String = "UAH",
    val type: String = "CASH",
    val color: String = "#6200EE",
    val icon: String = "account_balance_wallet"
)

@Serializable
data class CreateTransactionRequest(
    val accountId: Long,
    val categoryId: Long,
    val amount: Double,
    val date: String? = null,
    val note: String? = null,
    val type: String
)

@Serializable
data class CreateBudgetRequest(
    val categoryId: Long,
    val limitAmount: Double,
    val period: String = "MONTH",
    val startDate: String
)


@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

@Serializable
data class ErrorResponse(
    val error: String
)
