package com.appdetex.devops

import com.appdetex.devops.aws.client.EcsClientImpl
import org.koin.dsl.module.module
import software.amazon.awssdk.services.ecs.EcsAsyncClient

val appModule = module {
    single { EcsAsyncClient.create() }
    single { EcsClientImpl(get()) }
}
