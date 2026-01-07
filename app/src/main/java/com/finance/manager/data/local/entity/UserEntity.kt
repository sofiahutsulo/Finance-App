package com.finance.manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finance.manager.domain.model.User
import com.finance.manager.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String
)


fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    role = UserRole.valueOf(role)
)


fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    role = role.name
)
