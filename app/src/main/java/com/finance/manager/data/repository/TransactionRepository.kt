package com.finance.manager.data.repository

import com.finance.manager.data.local.dao.TransactionDao
import com.finance.manager.data.local.entity.toEntity
import com.finance.manager.data.local.entity.toDomain
import com.finance.manager.domain.model.Transaction
import com.finance.manager.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    fun getAllTransactions(userId: Long): Flow<List<Transaction>> {
        return transactionDao.getAll(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addTransaction(transaction: Transaction): Long {
        if (transaction.amount <= 0) {
            throw IllegalArgumentException("Transaction amount must be greater than 0")
        }
        return transactionDao.insert(transaction.toEntity())
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
    }

    fun getTransactionsByDateRange(
        userId: Long,
        startDate: Date,
        endDate: Date
    ): Flow<List<Transaction>> {
        return transactionDao.getByDateRange(
            userId,
            startDate.time,
            endDate.time
        ).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTransactionsByCategory(userId: Long, categoryId: Long): Flow<List<Transaction>> {
        return transactionDao.getByCategory(userId, categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getRecentTransactions(userId: Long, limit: Int = 10): Flow<List<Transaction>> {
        return transactionDao.getRecent(userId, limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }


    fun getSumByType(
        userId: Long,
        type: TransactionType,
        startDate: Date,
        endDate: Date
    ): Flow<Double> {
        return transactionDao.getSumByType(
            userId,
            type.name,
            startDate.time,
            endDate.time
        ).map { it ?: 0.0 }
    }
}
