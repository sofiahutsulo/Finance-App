package com.finance.server.repository

import com.finance.server.database.Budgets
import com.finance.server.models.BudgetResponse
import com.finance.server.models.CreateBudgetRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object BudgetRepository {

    fun getAllByUserId(userId: Long): List<BudgetResponse> = transaction {
        Budgets.select { Budgets.userId eq userId }
            .map { it.toBudgetResponse() }
    }


    fun create(userId: Long, request: CreateBudgetRequest): BudgetResponse = transaction {
        val startDate = Instant.parse(request.startDate)

        val insertedId = Budgets.insert {
            it[Budgets.userId] = userId
            it[categoryId] = request.categoryId
            it[limitAmount] = request.limitAmount.toBigDecimal()
            it[period] = request.period
            it[Budgets.startDate] = startDate
        } get Budgets.id

        BudgetResponse(
            id = insertedId,
            userId = userId,
            categoryId = request.categoryId,
            limitAmount = request.limitAmount,
            period = request.period,
            startDate = startDate.toString()
        )
    }


    fun update(id: Long, userId: Long, request: CreateBudgetRequest): Boolean = transaction {
        val startDate = Instant.parse(request.startDate)

        Budgets.update({ (Budgets.id eq id) and (Budgets.userId eq userId) }) {
            it[categoryId] = request.categoryId
            it[limitAmount] = request.limitAmount.toBigDecimal()
            it[period] = request.period
            it[Budgets.startDate] = startDate
            it[updatedAt] = Instant.now()
        } > 0
    }


    fun delete(id: Long, userId: Long): Boolean = transaction {
        Budgets.deleteWhere { (Budgets.id eq id) and (Budgets.userId eq userId) } > 0
    }


    private fun ResultRow.toBudgetResponse() = BudgetResponse(
        id = this[Budgets.id],
        userId = this[Budgets.userId],
        categoryId = this[Budgets.categoryId],
        limitAmount = this[Budgets.limitAmount].toDouble(),
        period = this[Budgets.period],
        startDate = this[Budgets.startDate].toString()
    )
}
