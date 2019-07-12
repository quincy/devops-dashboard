package com.appdetex.devops

import com.appdetex.devops.client.EcsClient
import com.appdetex.devops.client.EcsClientImpl
import com.appdetex.devops.service.EcsService
import com.appdetex.devops.service.EcsServiceImpl
import org.koin.dsl.module.module
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.ecs.EcsAsyncClient

val appModule = module {

    single {
        EcsAsyncClient.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(
                        System.getProperty("cloud.aws.credentials.accessKey"),
                        System.getProperty("cloud.aws.credentials.secretKey"),
                        System.getProperty("cloud.aws.credentials.sessionToken")
                    )
                )
            )
            .build()
    }

    // Clients
    single<EcsClient> { EcsClientImpl(get()) }

    // Services
    single<EcsService> { EcsServiceImpl(get()) }
}
