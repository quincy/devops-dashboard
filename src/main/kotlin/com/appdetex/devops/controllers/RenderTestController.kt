package com.appdetex.devops.controllers

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.em
import kotlinx.css.p
import kotlinx.html.*


fun Route.renderTestController() {

    get("/html-dsl") {
        call.respondHtml {
            body {
                h1 { +"HTML" }
                ul {
                    for (n in 1..10) {
                        li { +"$n" }
                    }
                }
            }
        }
    }

    get("/styles.css") {
        call.respondCss {
            kotlinx.css.body {
                backgroundColor = Color.red
            }
            p {
                fontSize = 2.em
            }
            rule("p.myclass") {
                color = Color.blue
            }
        }
    }

    get("/html-freemarker") {
        call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
    }

    get("/json/jackson") {
        call.respond(mapOf("hello" to "world"))
    }

    // Static feature. Try to access `/static/ktor_logo.svg`
    static("/static") {
        resources("static")
    }

}


@Suppress("unused")
fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        CSSBuilder().apply(builder).toString()
    }
}

@Suppress("unused")
fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

data class IndexData(val items: List<Int>)
