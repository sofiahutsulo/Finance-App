package com.finance.server.routes

import com.finance.server.models.CreateBudgetRequest
import com.finance.server.models.ErrorResponse
import com.finance.server.repository.BudgetRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.budgetRoutes() {
    authenticate("auth-jwt") {
        route("/budgets") {

            get {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val budgets = BudgetRepository.getAllByUserId(userId)
                call.respond(budgets)
            }


            post {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val request = call.receive<CreateBudgetRequest>()

                val budget = BudgetRepository.create(userId, request)
                call.respond(HttpStatusCode.Created, budget)
            }


            put("/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невірний ID"))

                val request = call.receive<CreateBudgetRequest>()
                val success = BudgetRepository.update(id, userId, request)

                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Бюджет оновлено"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Бюджет не знайдено"))
                }
            }


            delete("/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asLong()
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невірний ID"))

                val success = BudgetRepository.delete(id, userId)

                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Бюджет видалено"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Бюджет не знайдено"))
                }
            }
        }
    }
}
