package com.finance.manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finance.manager.domain.model.Category
import com.finance.manager.domain.model.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val icon: String,
    val color: String
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    type = TransactionType.valueOf(type),
    icon = icon,
    color = color
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    type = type.name,
    icon = icon,
    color = color
)
