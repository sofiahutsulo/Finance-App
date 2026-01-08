package com.finance.manager.data.repository

import com.finance.manager.data.local.dao.AccountDao
import com.finance.manager.data.local.entity.toEntity
import com.finance.manager.data.local.entity.toDomain
import com.finance.manager.domain.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountDao: AccountDao
) {

    fun getAllAccounts(userId: Long): Flow<List<Account>> {
        return accountDao.getAll(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun addAccount(account: Account): Long {
        return accountDao.insert(account.toEntity())
    }

    suspend fun updateAccount(account: Account) {
        accountDao.update(account.toEntity())
    }

    suspend fun deleteAccount(account: Account) {
        accountDao.delete(account.toEntity())
    }

    suspend fun getAccountById(accountId: Long): Account? {
        return accountDao.getById(accountId)?.toDomain()
    }

    fun getTotalBalance(userId: Long): Flow<Double> {
        return accountDao.getTotalBalance(userId).map { it ?: 0.0 }
    }
}
