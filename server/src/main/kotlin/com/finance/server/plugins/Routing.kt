package com.finance.server.plugins

import com.finance.server.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {

        get("/") {
            call.respondText("FinanceManager Server is running! \uD83D\uDE80")
        }

        get("/health") {
            call.respond(mapOf("status" to "OK", "message" to "Server is healthy"))
        }


        authRoutes()


        accountRoutes()


        categoryRoutes()


        transactionRoutes()


        budgetRoutes()
    }
}
