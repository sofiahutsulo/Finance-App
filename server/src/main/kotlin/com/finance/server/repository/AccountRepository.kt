package com.finance.server.repository

import com.finance.server.database.Accounts
import com.finance.server.models.AccountResponse
import com.finance.server.models.CreateAccountRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object AccountRepository {

    fun getAllByUserId(userId: Long): List<AccountResponse> = transaction {
        Accounts.select { Accounts.userId eq userId }
            .map { it.toAccountResponse() }
    }


    fun getById(id: Long, userId: Long): AccountResponse? = transaction {
        Accounts.select { (Accounts.id eq id) and (Accounts.userId eq userId) }
            .map { it.toAccountResponse() }
            .singleOrNull()
    }


    fun create(userId: Long, request: CreateAccountRequest): AccountResponse = transaction {
        val insertedId = Accounts.insert {
            it[Accounts.userId] = userId
            it[name] = request.name
            it[balance] = request.balance.toBigDecimal()
            it[currency] = request.currency
            it[type] = request.type
            it[color] = request.color
            it[icon] = request.icon
        } get Accounts.id

        AccountResponse(
            id = insertedId,
            userId = userId,
            name = request.name,
            balance = request.balance,
            currency = request.currency,
            type = request.type,
            color = request.color,
            icon = request.icon
        )
    }


    fun update(id: Long, userId: Long, request: CreateAccountRequest): Boolean = transaction {
        Accounts.update({ (Accounts.id eq id) and (Accounts.userId eq userId) }) {
            it[name] = request.name
            it[balance] = request.balance.toBigDecimal()
            it[currency] = request.currency
            it[type] = request.type
            it[color] = request.color
            it[icon] = request.icon
            it[updatedAt] = Instant.now()
        } > 0
    }


    fun delete(id: Long, userId: Long): Boolean = transaction {
        Accounts.deleteWhere { (Accounts.id eq id) and (Accounts.userId eq userId) } > 0
    }


    private fun ResultRow.toAccountResponse() = AccountResponse(
        id = this[Accounts.id],
        userId = this[Accounts.userId],
        name = this[Accounts.name],
        balance = this[Accounts.balance].toDouble(),
        currency = this[Accounts.currency],
        type = this[Accounts.type],
        color = this[Accounts.color],
        icon = this[Accounts.icon]
    )
}
