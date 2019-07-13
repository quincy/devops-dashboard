package com.appdetex.devops.controllers

import com.appdetex.devops.service.EcsService
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import org.koin.ktor.ext.inject


fun Route.ecsTestController() {

    val ecsService: EcsService by inject()

    route("/") {
        get {
            val response = ecsService.getTasks("Waits")

            val responseHeader = "FAMILY - STATUS - IMAGE"
            val responseText = response.values.sortedBy { it.family() }
                .joinToString(separator = "\n") { "${it.family()} - ${it.statusAsString()} - ${it.containerDefinitions()[0].image()}" }

            call.respondText("$responseHeader\n$responseText", contentType = ContentType.Text.Plain)
        }
    }

}
