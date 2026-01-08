package com.finance.manager.domain.model

import java.util.Date

data class Transaction(
    val id: Long = 0,
    val accountId: Long,
    val categoryId: Long,
    val amount: Double,
    val date: Date,
    val note: String = "",
    val type: TransactionType
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
