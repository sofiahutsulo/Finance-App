package com.finance.manager.data.local.dao

import androidx.room.*
import com.finance.manager.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY id DESC")
    fun getAll(userId: Long): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE userId = :userId AND categoryId = :categoryId LIMIT 1")
    suspend fun getByCategory(userId: Long, categoryId: Long): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE id = :budgetId LIMIT 1")
    suspend fun getById(budgetId: Long): BudgetEntity?

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    suspend fun deleteById(budgetId: Long)
}
