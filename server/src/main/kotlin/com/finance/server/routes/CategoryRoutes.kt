package com.finance.server.routes

import com.finance.server.repository.CategoryRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRoutes() {
    authenticate("auth-jwt") {
        route("/categories") {

            get {
                val categories = CategoryRepository.getAll()
                call.respond(categories)
            }
        }
    }
}
