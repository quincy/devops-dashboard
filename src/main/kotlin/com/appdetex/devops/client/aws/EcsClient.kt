package com.appdetex.devops.client.aws

import software.amazon.awssdk.services.ecs.EcsAsyncClient
import software.amazon.awssdk.services.ecs.model.*
import java.util.concurrent.CompletableFuture

interface EcsClient {
    fun listTasks(cluster: String): CompletableFuture<ListTasksResponse>
    fun describeTasks(cluster: String, tasks: List<String>): CompletableFuture<DescribeTasksResponse>
    fun describeTaskDefinition(definition: String): CompletableFuture<DescribeTaskDefinitionResponse>
}

class EcsClientImpl(private val amazonEcsClient: EcsAsyncClient) : EcsClient {

    // https://docs.aws.amazon.com/cli/latest/reference/ecs/list-tasks.html
    override fun listTasks(cluster: String): CompletableFuture<ListTasksResponse> {
        require(cluster.isNotBlank())

        val request = ListTasksRequest.builder().cluster(cluster).build()
        return amazonEcsClient.listTasks(request)
    }

    // https://docs.aws.amazon.com/cli/latest/reference/ecs/describe-tasks.html
    override fun describeTasks(cluster: String, tasks: List<String>): CompletableFuture<DescribeTasksResponse> {
        require(cluster.isNotBlank())
        require(tasks.size <= 100)

        val request = DescribeTasksRequest.builder().cluster(cluster).tasks(tasks).build()
        return amazonEcsClient.describeTasks(request)
    }

    // https://docs.aws.amazon.com/cli/latest/reference/ecs/describe-task-definition.html
    override fun describeTaskDefinition(definition: String): CompletableFuture<DescribeTaskDefinitionResponse> {
        require(definition.isNotBlank())

        val request = DescribeTaskDefinitionRequest.builder().taskDefinition(definition).build()
        return amazonEcsClient.describeTaskDefinition(request)
    }
}