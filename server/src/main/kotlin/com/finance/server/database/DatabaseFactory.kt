package com.finance.server.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(environment: ApplicationEnvironment? = null) {

        val host = System.getenv("DB_HOST") ?: "localhost"
        val port = System.getenv("DB_PORT") ?: "5432"
        val dbName = System.getenv("DB_NAME") ?: "finance_manager"
        val user = System.getenv("DB_USER") ?: "postgres"
        val password = System.getenv("DB_PASSWORD") ?: "NewStrongPassword123"

        println("🔌 Підключення до PostgreSQL:")
        println("   Host: $host:$port")
        println("   Database: $dbName")
        println("   User: $user")


        try {
            Database.connect(createHikariDataSource(
                url = "jdbc:postgresql://$host:$port/$dbName",
                driver = "org.postgresql.Driver",
                user = user,
                password = password
            ))


            transaction {
                println("✅ З'єднання з PostgreSQL успішне!")
            }
        } catch (e: Exception) {
            println("❌ Помилка підключення до БД: ${e.message}")
            throw e
        }
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        user: String,
        password: String
    ): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            username = user
            this.password = password
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(hikariConfig)
    }
}
