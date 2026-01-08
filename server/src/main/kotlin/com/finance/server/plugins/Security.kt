package com.finance.server.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {

    val jwtSecret = System.getenv("JWT_SECRET") ?: "finance-manager-secret-key-change-in-production"
    val jwtIssuer = System.getenv("JWT_ISSUER") ?: "http://localhost:8080"
    val jwtAudience = System.getenv("JWT_AUDIENCE") ?: "finance-manager-app"
    val jwtRealm = System.getenv("JWT_REALM") ?: "FinanceManager"

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asLong() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}


fun generateToken(userId: Long, environment: ApplicationEnvironment? = null): String {
    val jwtSecret = System.getenv("JWT_SECRET") ?: "finance-manager-secret-key-change-in-production"
    val jwtIssuer = System.getenv("JWT_ISSUER") ?: "http://localhost:8080"
    val jwtAudience = System.getenv("JWT_AUDIENCE") ?: "finance-manager-app"

    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim("userId", userId)
        .withExpiresAt(java.util.Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
        .sign(Algorithm.HMAC256(jwtSecret))
}
