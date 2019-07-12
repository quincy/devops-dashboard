package com.appdetex.devops.service

import com.appdetex.devops.client.EcsClient
import software.amazon.awssdk.services.ecs.model.Task
import software.amazon.awssdk.services.ecs.model.TaskDefinition


interface EcsService {
    fun getTasks(cluster: String): Map<Task, TaskDefinition>
}


class EcsServiceImpl(private val client: EcsClient): EcsService {

    override fun getTasks(cluster: String): Map<Task, TaskDefinition> {
        val taskList = client.listTasks(cluster).get()
        val tasks = client.describeTasks(cluster, taskList.taskArns()).get().tasks()

        // TODO: Determine if we can perform a bulk request for multiple task definitions
        return tasks.associateWith { client.describeTaskDefinition(it.taskDefinitionArn()).get().taskDefinition() }
    }
}