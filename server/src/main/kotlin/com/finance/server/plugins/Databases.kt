package com.finance.server.plugins

import com.finance.server.database.DatabaseFactory
import io.ktor.server.application.*

fun Application.configureDatabases() {
    DatabaseFactory.init(environment)
}
