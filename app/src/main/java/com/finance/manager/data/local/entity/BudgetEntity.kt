package com.finance.manager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.finance.manager.domain.model.Budget
import com.finance.manager.domain.model.BudgetPeriod
import java.util.Date

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"), Index("userId")]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val categoryId: Long,
    val amount: Double,
    val period: String,
    val startDate: Long
)

fun BudgetEntity.toDomain() = Budget(
    id = id,
    userId = userId,
    categoryId = categoryId,
    amount = amount,
    period = BudgetPeriod.valueOf(period),
    startDate = Date(startDate)
)

fun Budget.toEntity() = BudgetEntity(
    id = id,
    userId = userId,
    categoryId = categoryId,
    amount = amount,
    period = period.name,
    startDate = startDate.time
)
