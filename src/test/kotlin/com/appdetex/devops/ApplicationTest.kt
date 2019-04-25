package com.appdetex.devops

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.contains
import com.natpryce.hamkrest.equalTo
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.io.charsets.Charset
import org.araqnid.hamkrest.json.equivalentTo
import kotlin.test.Test

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertThat(response.status(), equalTo(HttpStatusCode.OK))
                assertThat(response.content, equalTo("HELLO WORLD!"))
            }
        }
    }

    @Test
    fun `can list versions for environment`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/versions/test").apply {
                assertThat(response, TestApplicationResponse::isStatusOk)
                assertThat(response, TestApplicationResponse::isApplicationJson)
                assertThat(
                    response.content!!, equivalentTo(
                        """{
                              "environment": "test",
                              "projects": [
                                {
                                  "name": "watch-service",
                                  "version": "1.0.0",
                                  "build": 23
                                }
                              ]
                           }"""
                    )
                )
            }
        }
    }

    @Test
    fun `bad request is returned for invalid environment name`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/versions/bad-environment-name").apply {
                assertThat(response, TestApplicationResponse::isStatusBadRequest)
                assertThat(response.content!!, contains("""Invalid environment name='bad-environment-name'""".toRegex()))
            }
        }
    }
}

private val APPLICATION_JSON = ContentType.Application.Json.withCharset(Charset.defaultCharset())

fun TestApplicationResponse.isStatusOk() = this.status() == HttpStatusCode.OK
fun TestApplicationResponse.isStatusBadRequest() = this.status() == HttpStatusCode.BadRequest
fun TestApplicationResponse.isApplicationJson() = this.contentType() == APPLICATION_JSON
