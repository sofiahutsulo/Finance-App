package com.finance.manager.data.repository

import com.finance.manager.data.local.dao.CategoryDao
import com.finance.manager.data.local.entity.toEntity
import com.finance.manager.data.local.entity.toDomain
import com.finance.manager.domain.model.Category
import com.finance.manager.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> {
        return categoryDao.getByType(type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addCategory(category: Category): Long {
        return categoryDao.insert(category.toEntity())
    }

    suspend fun getCategoryById(categoryId: Long): Category? {
        return categoryDao.getById(categoryId)?.toDomain()
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category.toEntity())
    }
}
