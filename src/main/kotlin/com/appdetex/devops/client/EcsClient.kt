package com.appdetex.devops.client

import software.amazon.awssdk.services.ecs.EcsAsyncClient
import software.amazon.awssdk.services.ecs.model.*
import software.amazon.awssdk.services.ecs.transform.DescribeTasksRequestMarshaller
import java.util.concurrent.CompletableFuture

interface EcsClient {
    fun listTasks(cluster: String): CompletableFuture<ListTasksResponse>
    fun describeTasks(cluster: String, tasks: List<String>): CompletableFuture<DescribeTasksResponse>
    fun describeTaskDefinition(cluster: String, definition: String): CompletableFuture<DescribeTaskDefinitionResponse>
}

class EcsClientImpl(private val amazonEcsClient: EcsAsyncClient) : EcsClient {

    override fun listTasks(cluster: String): CompletableFuture<ListTasksResponse> {
        val request = ListTasksRequest.builder().cluster(cluster).build()
        return amazonEcsClient.listTasks(request)
    }

    override fun describeTasks(cluster: String, tasks: List<String>): CompletableFuture<DescribeTasksResponse> {
        val request = DescribeTasksRequest.builder().cluster(cluster).tasks(tasks).build()
        return amazonEcsClient.describeTasks(request)
    }

    override fun describeTaskDefinition(cluster: String, definition: String): CompletableFuture<DescribeTaskDefinitionResponse> {
        val request = DescribeTaskDefinitionRequest.builder().taskDefinition(definition).build()
        return amazonEcsClient.describeTaskDefinition(request)
    }
}