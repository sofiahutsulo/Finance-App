package com.finance.manager.data.repository

import com.finance.manager.data.local.dao.UserDao
import com.finance.manager.data.local.entity.toEntity
import com.finance.manager.data.local.entity.toDomain
import com.finance.manager.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {


    suspend fun addUser(user: User): Long {
        return userDao.insert(user.toEntity())
    }


    suspend fun registerUser(user: User): Long {
        return addUser(user)
    }


    suspend fun getUserByEmail(email: String): User? {
        return userDao.getByEmail(email)?.toDomain()
    }


    suspend fun getUserById(userId: Long): User? {
        return userDao.getById(userId)?.toDomain()
    }


    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
