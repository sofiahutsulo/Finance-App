package com.finance.manager.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val icon: String,
    val color: String
)
