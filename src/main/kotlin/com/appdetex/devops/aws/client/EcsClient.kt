package com.appdetex.devops.aws.client

import software.amazon.awssdk.services.ecs.EcsAsyncClient
import software.amazon.awssdk.services.ecs.model.ListTasksRequest
import java.util.concurrent.CompletableFuture

interface EcsClient {
    fun listTasks(cluster: String): CompletableFuture<List<String>>
}

class EcsClientImpl(private val amazonEcsClient: EcsAsyncClient) : EcsClient {

    override fun listTasks(cluster: String): CompletableFuture<List<String>> {
        val request = ListTasksRequest.builder().cluster(cluster).build()
        return amazonEcsClient.listTasks(request).thenApply { it.taskArns() }
    }
}