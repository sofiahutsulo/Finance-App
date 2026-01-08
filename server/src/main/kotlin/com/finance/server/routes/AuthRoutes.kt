package com.finance.server.routes

import com.finance.server.models.*
import com.finance.server.plugins.generateToken
import com.finance.server.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    route("/auth") {

        post("/register") {
            val request = call.receive<RegisterRequest>()

            if (request.name.isBlank() || request.email.isBlank() || request.password.length < 6) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Невірні дані"))
                return@post
            }

            val user = UserRepository.create(request.name, request.email, request.password)
            if (user == null) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("Користувач з таким email вже існує"))
                return@post
            }

            val token = generateToken(user.id, call.application.environment)

            call.respond(HttpStatusCode.Created, LoginResponse(token, user))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            val user = UserRepository.findByEmailAndPassword(request.email, request.password)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Невірний email або пароль"))
                return@post
            }

            val token = generateToken(user.id, call.application.environment)

            call.respond(LoginResponse(token, user))
        }

        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("userId").asLong()

                val user = UserRepository.findById(userId)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Користувача не знайдено"))
                    return@get
                }

                call.respond(user)
            }
        }
    }
}
