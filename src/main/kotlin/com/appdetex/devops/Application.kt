package com.appdetex.devops

import com.appdetex.devops.controllers.taskController
import com.appdetex.devops.controllers.renderTestController
import com.appdetex.devops.controllers.sessionController
import com.appdetex.devops.domain.EnvironmentVersionsDTO
import com.appdetex.devops.domain.MySession
import com.appdetex.devops.domain.Project
import com.appdetex.devops.exception.AuthenticationException
import com.appdetex.devops.exception.AuthorizationException
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import org.koin.core.Koin
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.slf4j.event.Level


fun main(args: Array<String>) {
    Koin.logger = PrintLogger()

    startKoin(listOf(appModule))

    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    // Configuration
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ConditionalHeaders)
    install(AutoHeadResponse)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            registerModule(JavaTimeModule())
            registerModule(KotlinModule())
        }
    }

    // Routes
    routing {

        // Controllers
        taskController()
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

