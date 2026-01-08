package com.finance.manager.data.remote.dto

import com.google.gson.annotations.SerializedName


data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)


data class RegisterRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)


data class LoginResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: UserDto
)


data class UserDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("role")
    val role: String = "USER",
    @SerializedName("createdAt")
    val createdAt: String? = null
)


data class ErrorResponse(
    @SerializedName("message")
    val message: String
)
