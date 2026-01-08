package com.finance.server.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant


object Users : Table("users") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20).default("USER")
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}


object Accounts : Table("accounts") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(Users.id)
    val name = varchar("name", 100)
    val balance = decimal("balance", 15, 2).default(0.0.toBigDecimal())
    val currency = varchar("currency", 3).default("UAH")
    val type = varchar("type", 50).default("CASH")
    val color = varchar("color", 7).default("#6200EE")
    val icon = varchar("icon", 50).default("account_balance_wallet")
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}


object Categories : Table("categories") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 100)
    val type = varchar("type", 20)
    val icon = varchar("icon", 50).default("category")
    val color = varchar("color", 7).default("#999999")
    val isSystem = bool("is_system").default(false)
    val createdAt = timestamp("created_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}


object Transactions : Table("transactions") {
    val id = long("id").autoIncrement()
    val accountId = long("account_id").references(Accounts.id)
    val categoryId = long("category_id").references(Categories.id)
    val userId = long("user_id").references(Users.id)
    val amount = decimal("amount", 15, 2)
    val date = timestamp("date").default(Instant.now())
    val note = text("note").nullable()
    val type = varchar("type", 20)
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}


object Budgets : Table("budgets") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(Users.id)
    val categoryId = long("category_id").references(Categories.id)
    val limitAmount = decimal("limit_amount", 15, 2)
    val period = varchar("period", 20).default("MONTH")
    val startDate = timestamp("start_date")
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}
