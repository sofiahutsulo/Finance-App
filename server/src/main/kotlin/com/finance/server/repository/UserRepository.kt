package com.finance.server.repository

import com.finance.server.database.Users
import com.finance.server.models.UserResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object UserRepository {

    fun create(name: String, email: String, password: String): UserResponse? = transaction {

        val existingUser = Users.select { Users.email eq email }.singleOrNull()
        if (existingUser != null) {
            return@transaction null
        }


        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())


        val isFirstUser = Users.selectAll().count() == 0L
        val role = if (isFirstUser) "ADMIN" else "USER"


        val insertedId = Users.insert {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.passwordHash] = passwordHash
            it[Users.role] = role
        } get Users.id

        UserResponse(
            id = insertedId,
            name = name,
            email = email,
            role = role
        )
    }


    fun findByEmailAndPassword(email: String, password: String): UserResponse? = transaction {
        val user = Users.select { Users.email eq email }.singleOrNull() ?: return@transaction null


        if (!BCrypt.checkpw(password, user[Users.passwordHash])) {
            return@transaction null
        }

        UserResponse(
            id = user[Users.id],
            name = user[Users.name],
            email = user[Users.email],
            role = user[Users.role]
        )
    }


    fun findById(id: Long): UserResponse? = transaction {
        Users.select { Users.id eq id }
            .map {
                UserResponse(
                    id = it[Users.id],
                    name = it[Users.name],
                    email = it[Users.email],
                    role = it[Users.role]
                )
            }
            .singleOrNull()
    }


    fun getAll(): List<UserResponse> = transaction {
        Users.selectAll().map {
            UserResponse(
                id = it[Users.id],
                name = it[Users.name],
                email = it[Users.email],
                role = it[Users.role]
            )
        }
    }
}
