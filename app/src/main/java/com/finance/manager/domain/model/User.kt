package com.finance.manager.domain.model

data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole = UserRole.USER
)

enum class UserRole {
    USER,
    ADMIN
}
