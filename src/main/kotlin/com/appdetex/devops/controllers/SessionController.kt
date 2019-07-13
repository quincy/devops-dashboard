package com.appdetex.devops.controllers

import com.appdetex.devops.domain.MySession
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set


fun Route.sessionController() {

    get("/session/increment") {
        val session = call.sessions.get<MySession>() ?: MySession()
        call.sessions.set(session.copy(count = session.count + 1))
        call.respondText("Counter is ${session.count}. Refresh to increment.")
    }

}
