package com.finance.manager.domain.model

data class Account(
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val balance: Double,
    val currency: String = "UAH",
    val type: AccountType,
    val color: String,
    val icon: String
)

enum class AccountType {
    CASH,
    CARD,      
    BANK
}
