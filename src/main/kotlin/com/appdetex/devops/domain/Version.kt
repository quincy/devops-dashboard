package com.appdetex.devops.domain

data class EnvironmentVersionsDTO(val environment: String, val projects: List<Project>)

data class Project(val name: String, val version: String, val build: Int)