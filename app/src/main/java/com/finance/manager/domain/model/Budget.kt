package com.finance.manager.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


enum class BudgetPeriod {
    MONTH,
    WEEK,
    YEAR
}

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val categoryId: Long,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: Date
)
