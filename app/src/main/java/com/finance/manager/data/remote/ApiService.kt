package com.finance.manager.data.remote

import com.finance.manager.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserDto>


}
