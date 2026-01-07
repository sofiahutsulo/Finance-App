package com.finance.manager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.finance.manager.domain.model.Account
import com.finance.manager.domain.model.AccountType

@Entity(
    tableName = "accounts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val balance: Double,
    val currency: String,
    val type: String,
    val color: String,
    val icon: String
)

fun AccountEntity.toDomain() = Account(
    id = id,
    userId = userId,
    name = name,
    balance = balance,
    currency = currency,
    type = AccountType.valueOf(type),
    color = color,
    icon = icon
)

fun Account.toEntity() = AccountEntity(
    id = id,
    userId = userId,
    name = name,
    balance = balance,
    currency = currency,
    type = type.name,
    color = color,
    icon = icon
)
