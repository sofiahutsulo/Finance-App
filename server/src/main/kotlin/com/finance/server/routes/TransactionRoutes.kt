package com.finance.server.routes

import com.finance.server.models.CreateTransactionRequest
import com.finance.server.models.ErrorResponse
import com.finance.server.repository.TransactionRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.transactionRoutes() {
    authenticate("auth-jwt") {
        route("/transactions") {

            get {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val transactions = TransactionRepository.getAllByUserId(userId)
                call.respond(transactions)
            }


            post {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val request = call.receive<CreateTransactionRequest>()

                val transaction = TransactionRepository.create(userId, request)
                call.respond(HttpStatusCode.Created, transaction)
            }


            put("/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невірний ID"))

                val request = call.receive<CreateTransactionRequest>()
                val success = TransactionRepository.update(id, userId, request)

                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Транзакцію оновлено"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Транзакцію не знайдено"))
                }
            }


            delete("/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невірний ID"))

                val success = TransactionRepository.delete(id, userId)

                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Транзакцію видалено"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Транзакцію не знайдено"))
                }
            }
        }
    }
}
