package com.finance.server.repository

import com.finance.server.database.Transactions
import com.finance.server.models.CreateTransactionRequest
import com.finance.server.models.TransactionResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object TransactionRepository {

    fun getAllByUserId(userId: Long): List<TransactionResponse> = transaction {
        Transactions.select { Transactions.userId eq userId }
            .orderBy(Transactions.date to SortOrder.DESC)
            .map { it.toTransactionResponse() }
    }


    fun create(userId: Long, request: CreateTransactionRequest): TransactionResponse = transaction {
        val date = request.date?.let { Instant.parse(it) } ?: Instant.now()

        val insertedId = Transactions.insert {
            it[Transactions.userId] = userId
            it[accountId] = request.accountId
            it[categoryId] = request.categoryId
            it[amount] = request.amount.toBigDecimal()
            it[Transactions.date] = date
            it[note] = request.note
            it[type] = request.type
        } get Transactions.id

        TransactionResponse(
            id = insertedId,
            accountId = request.accountId,
            categoryId = request.categoryId,
            userId = userId,
            amount = request.amount,
            date = date.toString(),
            note = request.note,
            type = request.type
        )
    }


    fun update(id: Long, userId: Long, request: CreateTransactionRequest): Boolean = transaction {
        val date = request.date?.let { Instant.parse(it) } ?: Instant.now()

        Transactions.update({ (Transactions.id eq id) and (Transactions.userId eq userId) }) {
            it[accountId] = request.accountId
            it[categoryId] = request.categoryId
            it[amount] = request.amount.toBigDecimal()
            it[Transactions.date] = date
            it[note] = request.note
            it[type] = request.type
            it[updatedAt] = Instant.now()
        } > 0
    }


    fun delete(id: Long, userId: Long): Boolean = transaction {
        Transactions.deleteWhere { (Transactions.id eq id) and (Transactions.userId eq userId) } > 0
    }


    private fun ResultRow.toTransactionResponse() = TransactionResponse(
        id = this[Transactions.id],
        accountId = this[Transactions.accountId],
        categoryId = this[Transactions.categoryId],
        userId = this[Transactions.userId],
        amount = this[Transactions.amount].toDouble(),
        date = this[Transactions.date].toString(),
        note = this[Transactions.note],
        type = this[Transactions.type]
    )
}
