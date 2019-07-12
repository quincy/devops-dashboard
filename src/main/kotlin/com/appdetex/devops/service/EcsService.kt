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

        // TODO: Seems like we can only grab one task definition at a time?
        return tasks.associateWith { client.describeTaskDefinition(cluster, it.taskDefinitionArn()).get().taskDefinition() }
    }
}