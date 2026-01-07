package com.finance.manager.data.local.dao

import androidx.room.*
import com.finance.manager.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE userId = :userId")
    fun getAll(userId: Long): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :accountId LIMIT 1")
    suspend fun getById(accountId: Long): AccountEntity?


    @Query("SELECT SUM(balance) FROM accounts WHERE userId = :userId")
    fun getTotalBalance(userId: Long): Flow<Double?>
}
