package com.finance.server.routes

import com.finance.server.models.CreateAccountRequest
import com.finance.server.models.ErrorResponse
import com.finance.server.repository.AccountRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.accountRoutes() {
    authenticate("auth-jwt") {
        route("/accounts") {

            get {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val accounts = AccountRepository.getAllByUserId(userId)
                call.respond(accounts)
            }


            get("/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невірний ID"))

                val account = AccountRepository.getById(id, userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Рахунок не знайдено"))

                call.respond(account)
            }


            post {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val request = call.receive<CreateAccountRequest>()

                val account = AccountRepository.create(userId, request)
                call.respond(HttpStatusCode.Created, account)
            }


            put("/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невірний ID"))

                val request = call.receive<CreateAccountRequest>()
                val success = AccountRepository.update(id, userId, request)

                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Рахунок оновлено"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Рахунок не знайдено"))
                }
            }


            delete("/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невірний ID"))

                val success = AccountRepository.delete(id, userId)

                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Рахунок видалено"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Рахунок не знайдено"))
                }
            }
        }
    }
}
