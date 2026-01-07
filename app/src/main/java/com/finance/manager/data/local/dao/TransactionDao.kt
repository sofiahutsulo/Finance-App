package com.finance.manager.data.local.dao

import androidx.room.*
import com.finance.manager.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)


    @Query("""
        SELECT t.* FROM transactions t
        INNER JOIN accounts a ON t.accountId = a.id
        WHERE a.userId = :userId
        ORDER BY t.date DESC
    """)
    fun getAll(userId: Long): Flow<List<TransactionEntity>>


    @Query("""
        SELECT t.* FROM transactions t
        INNER JOIN accounts a ON t.accountId = a.id
        WHERE a.userId = :userId AND t.date BETWEEN :startDate AND :endDate
        ORDER BY t.date DESC
    """)
    fun getByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>


    @Query("""
        SELECT t.* FROM transactions t
        INNER JOIN accounts a ON t.accountId = a.id
        WHERE a.userId = :userId AND t.categoryId = :categoryId
        ORDER BY t.date DESC
    """)
    fun getByCategory(userId: Long, categoryId: Long): Flow<List<TransactionEntity>>


    @Query("""
        SELECT t.* FROM transactions t
        INNER JOIN accounts a ON t.accountId = a.id
        WHERE a.userId = :userId
        ORDER BY t.date DESC
        LIMIT :limit
    """)
    fun getRecent(userId: Long, limit: Int): Flow<List<TransactionEntity>>


    @Query("""
        SELECT SUM(t.amount) FROM transactions t
        INNER JOIN accounts a ON t.accountId = a.id
        WHERE a.userId = :userId
        AND t.type = :type
        AND t.date BETWEEN :startDate AND :endDate
    """)
    fun getSumByType(userId: Long, type: String, startDate: Long, endDate: Long): Flow<Double?>
}
