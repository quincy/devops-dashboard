package com.appdetex.devops

import com.appdetex.devops.config.appConfig
import com.appdetex.devops.controllers.ecsTestController
import com.appdetex.devops.controllers.renderTestController
import com.appdetex.devops.controllers.sessionController
import com.appdetex.devops.domain.EnvironmentVersionsDTO
import com.appdetex.devops.domain.Project
import com.appdetex.devops.exception.AuthenticationException
import com.appdetex.devops.exception.AuthorizationException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.core.Koin
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin


fun main(args: Array<String>) {
    Koin.logger = PrintLogger()

    startKoin(listOf(appModule))

    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    // Configuration
    appConfig()

    // Routes
    routing {

        // Controllers
        ecsTestController()
        renderTestController()
        sessionController()

        get("/versions/{environment}") {
            call.parameters["environment"]?.takeIf { env -> env == "test" }?.let { environment ->
                call.respond(HttpStatusCode.OK, EnvironmentVersionsDTO(environment, listOf(Project("watch-service", "1.0.0", 23))))
            } ?: call.respondText(text = "Invalid environment name='${call.parameters["environment"]}'", status = HttpStatusCode.BadRequest)
        }

        // Status Page Definitions
        install(StatusPages) {
            exception<AuthenticationException> { call.respond(HttpStatusCode.Unauthorized) }
            exception<AuthorizationException> { call.respond(HttpStatusCode.Forbidden) }
        }
    }
}

