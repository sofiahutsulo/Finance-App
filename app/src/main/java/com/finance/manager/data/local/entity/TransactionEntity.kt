package com.finance.manager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.finance.manager.domain.model.Transaction
import com.finance.manager.domain.model.TransactionType
import java.util.Date

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("accountId"), Index("categoryId"), Index("date")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long,
    val categoryId: Long,
    val amount: Double,
    val date: Long,
    val note: String,
    val type: String
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    amount = amount,
    date = Date(date),
    note = note,
    type = TransactionType.valueOf(type)
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    amount = amount,
    date = date.time,
    note = note,
    type = type.name
)
