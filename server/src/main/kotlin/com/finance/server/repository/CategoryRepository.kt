package com.finance.server.repository

import com.finance.server.database.Categories
import com.finance.server.models.CategoryResponse
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object CategoryRepository {

    fun getAll(): List<CategoryResponse> = transaction {
        Categories.selectAll().map { it.toCategoryResponse() }
    }


    private fun ResultRow.toCategoryResponse() = CategoryResponse(
        id = this[Categories.id],
        name = this[Categories.name],
        type = this[Categories.type],
        icon = this[Categories.icon],
        color = this[Categories.color]
    )
}
