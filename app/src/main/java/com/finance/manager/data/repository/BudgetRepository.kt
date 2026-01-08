package com.finance.manager.data.repository

import com.finance.manager.data.local.dao.BudgetDao
import com.finance.manager.data.local.entity.toEntity
import com.finance.manager.data.local.entity.toDomain
import com.finance.manager.domain.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {

    fun getAllBudgets(userId: Long): Flow<List<Budget>> {
        return budgetDao.getAll(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addBudget(budget: Budget): Long {
        return budgetDao.insert(budget.toEntity())
    }

    suspend fun updateBudget(budget: Budget) {
        budgetDao.update(budget.toEntity())
    }

    suspend fun deleteBudget(budget: Budget) {
        budgetDao.delete(budget.toEntity())
    }

    suspend fun deleteBudgetById(budgetId: Long) {
        budgetDao.deleteById(budgetId)
    }

    suspend fun getBudgetByCategory(userId: Long, categoryId: Long): Budget? {
        return budgetDao.getByCategory(userId, categoryId)?.toDomain()
    }

    suspend fun getBudgetById(budgetId: Long): Budget? {
        return budgetDao.getById(budgetId)?.toDomain()
    }
}
