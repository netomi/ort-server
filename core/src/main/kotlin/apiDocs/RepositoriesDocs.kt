/*
 * Copyright (C) 2022 The ORT Server Authors (See <https://github.com/eclipse-apoapsis/ort-server/blob/main/NOTICE>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package org.eclipse.apoapsis.ortserver.core.apiDocs

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute

import io.ktor.http.HttpStatusCode

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

import kotlinx.datetime.Clock

import org.eclipse.apoapsis.ortserver.api.v1.AdvisorJob
import org.eclipse.apoapsis.ortserver.api.v1.AdvisorJobConfiguration
import org.eclipse.apoapsis.ortserver.api.v1.AnalyzerJob
import org.eclipse.apoapsis.ortserver.api.v1.AnalyzerJobConfiguration
import org.eclipse.apoapsis.ortserver.api.v1.CreateOrtRun
import org.eclipse.apoapsis.ortserver.api.v1.CreateSecret
import org.eclipse.apoapsis.ortserver.api.v1.EnvironmentConfig
import org.eclipse.apoapsis.ortserver.api.v1.EvaluatorJob
import org.eclipse.apoapsis.ortserver.api.v1.EvaluatorJobConfiguration
import org.eclipse.apoapsis.ortserver.api.v1.InfrastructureService
import org.eclipse.apoapsis.ortserver.api.v1.JobConfigurations
import org.eclipse.apoapsis.ortserver.api.v1.JobStatus
import org.eclipse.apoapsis.ortserver.api.v1.JobSummaries
import org.eclipse.apoapsis.ortserver.api.v1.JobSummary
import org.eclipse.apoapsis.ortserver.api.v1.Jobs
import org.eclipse.apoapsis.ortserver.api.v1.OrtRun
import org.eclipse.apoapsis.ortserver.api.v1.OrtRunStatus
import org.eclipse.apoapsis.ortserver.api.v1.OrtRunSummary
import org.eclipse.apoapsis.ortserver.api.v1.PackageManagerConfiguration
import org.eclipse.apoapsis.ortserver.api.v1.PagedResponse
import org.eclipse.apoapsis.ortserver.api.v1.ProviderPluginConfiguration
import org.eclipse.apoapsis.ortserver.api.v1.ReporterJob
import org.eclipse.apoapsis.ortserver.api.v1.ReporterJobConfiguration
import org.eclipse.apoapsis.ortserver.api.v1.Repository
import org.eclipse.apoapsis.ortserver.api.v1.RepositoryType
import org.eclipse.apoapsis.ortserver.api.v1.ScannerJob
import org.eclipse.apoapsis.ortserver.api.v1.ScannerJobConfiguration
import org.eclipse.apoapsis.ortserver.api.v1.Secret
import org.eclipse.apoapsis.ortserver.api.v1.UpdateRepository
import org.eclipse.apoapsis.ortserver.api.v1.UpdateSecret
import org.eclipse.apoapsis.ortserver.model.util.ListQueryParameters
import org.eclipse.apoapsis.ortserver.model.util.OrderDirection.ASCENDING
import org.eclipse.apoapsis.ortserver.model.util.OrderDirection.DESCENDING
import org.eclipse.apoapsis.ortserver.model.util.OrderField
import org.eclipse.apoapsis.ortserver.model.util.asPresent

private val jobConfigurations = JobConfigurations(
    analyzer = AnalyzerJobConfiguration(
        allowDynamicVersions = true,
        disabledPackageManagers = listOf("NPM", "SBT"),
        enabledPackageManagers = listOf("Gradle", "Maven"),
        environmentConfig = EnvironmentConfig(
            infrastructureServices = listOf(
                InfrastructureService(
                    name = "Artifactory",
                    url = "https://artifactory.example.org/repo",
                    description = "Our Artifactory server",
                    usernameSecretRef = "artifactoryUsername",
                    passwordSecretRef = "artifactoryPassword"
                )
            ),
            environmentDefinitions = mapOf(
                "maven" to listOf(
                    mapOf(
                        "service" to "Artifactory",
                        "id" to "repo"
                    )
                )
            )
        ),
        packageCurationProviders = listOf(
            ProviderPluginConfiguration(
                type = "ClearlyDefined",
                id = "ClearlyDefined",
                enabled = true,
                options = mapOf(
                    "serverUrl" to "https://api.clearlydefined.io",
                    "minTotalLicenseScore" to "0"
                )
            )
        ),
        packageManagerOptions = mapOf(
            "Gradle" to PackageManagerConfiguration(
                mustRunAfter = listOf("Maven"),
                options = mapOf("gradleVersion" to "8.1.1")
            )
        ),
        skipExcluded = true
    ),
    advisor = AdvisorJobConfiguration(
        advisors = listOf("VulnerableCode"),
        skipExcluded = true
    ),
    scanner = ScannerJobConfiguration(
        createMissingArchives = true,
        detectedLicenseMappings = mapOf("LicenseRef-scancode-generic-cla" to "NOASSERTION"),
        ignorePatterns = listOf("**/META-INF/DEPENDENCIES"),
        projectScanners = listOf("SCANOSS"),
        scanners = listOf("ScanCode"),
        skipConcluded = true,
        skipExcluded = true
    ),
    evaluator = EvaluatorJobConfiguration(
        copyrightGarbageFile = "copyright-garbage.yml",
        licenseClassificationsFile = "license-classifications.yml",
        packageConfigurationProviders = listOf(ProviderPluginConfiguration(type = "OrtConfig")),
        resolutionsFile = "resolutions.yml",
        ruleSet = "rules.evaluator.kts"
    ),
    reporter = ReporterJobConfiguration(formats = listOf("WebApp"))
)

val jobs = Jobs(
    analyzer = AnalyzerJob(
        id = 1L,
        createdAt = Clock.System.now(),
        configuration = jobConfigurations.analyzer,
        status = JobStatus.CREATED
    ),
    advisor = AdvisorJob(
        id = 1L,
        createdAt = Clock.System.now(),
        configuration = jobConfigurations.advisor!!,
        status = JobStatus.CREATED
    ),
    scanner = ScannerJob(
        id = 1L,
        createdAt = Clock.System.now(),
        configuration = jobConfigurations.scanner!!,
        status = JobStatus.CREATED
    ),
    evaluator = EvaluatorJob(
        id = 1L,
        createdAt = Clock.System.now(),
        configuration = jobConfigurations.evaluator!!,
        status = JobStatus.CREATED
    ),
    reporter = ReporterJob(
        id = 1L,
        createdAt = Clock.System.now(),
        configuration = jobConfigurations.reporter!!,
        status = JobStatus.CREATED
    )
)

/**
 * Create a [JobSummary] for a job that was created the provided [offset] duration ago.
 */
private fun createJobSummary(offset: Duration, status: JobStatus = JobStatus.FINISHED): JobSummary {
    val createdAt = Clock.System.now() - offset
    return JobSummary(
        id = 1L,
        createdAt = createdAt,
        startedAt = createdAt + 1.minutes,
        finishedAt = (createdAt + 2.minutes).takeIf { status == JobStatus.FINISHED },
        status = status
    )
}

val getRepositoryById: OpenApiRoute.() -> Unit = {
    operationId = "GetRepositoryById"
    summary = "Get details of a repository."
    tags = listOf("Repositories")

    request {
        pathParameter<Long>("repositoryId") {
            description = "The repository's ID."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<Repository> {
                example(
                    name = "Get Repository",
                    value = Repository(id = 1, type = RepositoryType.GIT, url = "https://example.com/org/repo.git")
                )
            }
        }
    }
}

val patchRepositoryById: OpenApiRoute.() -> Unit = {
    operationId = "PatchRepositoryById"
    summary = "Update a repository."
    tags = listOf("Repositories")

    request {
        pathParameter<Long>("repositoryId") {
            description = "The repository's ID."
        }
        jsonBody<UpdateRepository> {
            description = "Set the values that should be updated. To delete a value, set it explicitly to null."
            example(
                name = "Update Repository",
                value = UpdateRepository(
                    type = RepositoryType.GIT_REPO.asPresent(),
                    url = "https://example.com/org/updated-repo.git".asPresent()
                )
            )
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<Repository> {
                example(
                    name = "Update Repository",
                    value = Repository(
                        id = 1,
                        type = RepositoryType.GIT_REPO,
                        url = "https://example.com/org/updated-repo.git"
                    )
                )
            }
        }
    }
}

val deleteRepositoryById: OpenApiRoute.() -> Unit = {
    operationId = "DeleteRepositoryById"
    summary = "Delete a repository."
    tags = listOf("Repositories")

    request {
        pathParameter<Long>("repositoryId") {
            description = "The repository's ID."
        }
    }

    response {
        HttpStatusCode.NoContent to {
            description = "Success"
        }
    }
}

val getOrtRuns: OpenApiRoute.() -> Unit = {
    operationId = "getOrtRuns"
    summary = "Get all ORT runs of a repository."
    tags = listOf("Repositories")

    request {
        pathParameter<Long>("repositoryId") {
            description = "The repository's ID."
        }

        standardListQueryParameters()
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<PagedResponse<OrtRun>> {
                example(
                    name = "Get ORT runs",
                    value = PagedResponse(
                        listOf(
                            OrtRunSummary(
                                id = 2,
                                index = 1,
                                repositoryId = 1,
                                revision = "main",
                                createdAt = Clock.System.now() - 15.minutes,
                                finishedAt = Clock.System.now(),
                                jobs = JobSummaries(
                                    analyzer = createJobSummary(10.minutes),
                                    advisor = createJobSummary(8.minutes),
                                    scanner = createJobSummary(8.minutes),
                                    evaluator = createJobSummary(6.minutes),
                                    reporter = createJobSummary(4.minutes)
                                ),
                                status = OrtRunStatus.FINISHED,
                                labels = mapOf("label key" to "label value"),
                                jobConfigContext = null,
                                resolvedJobConfigContext = "c80ef3bcd2bec428da923a188dd0870b1153995c"
                            ),
                            OrtRunSummary(
                                id = 3,
                                index = 2,
                                repositoryId = 1,
                                revision = "main",
                                createdAt = Clock.System.now() - 15.minutes,
                                finishedAt = Clock.System.now(),
                                jobs = JobSummaries(
                                    analyzer = createJobSummary(5.minutes),
                                    advisor = createJobSummary(3.minutes),
                                    scanner = createJobSummary(3.minutes),
                                    evaluator = createJobSummary(1.minutes, JobStatus.RUNNING)
                                ),
                                status = OrtRunStatus.ACTIVE,
                                labels = mapOf("label key" to "label value"),
                                jobConfigContext = null,
                                resolvedJobConfigContext = "32f955941e94d0a318e1c985903f42af924e9050"
                            )
                        ),
                        ListQueryParameters(
                            sortFields = listOf(OrderField("createdAt", DESCENDING)),
                            limit = 20,
                            offset = 0
                        )
                    )
                )
            }
        }
    }
}

val postOrtRun: OpenApiRoute.() -> Unit = {
    operationId = "postOrtRun"
    summary = "Create an ORT run for a repository."
    tags = listOf("Repositories")

    request {
        pathParameter<Long>("repositoryId") {
            description = "The repository's ID."
        }

        jsonBody<CreateOrtRun> {
            example(
                name = "Create ORT run",
                value = CreateOrtRun(
                    revision = "main",
                    jobConfigs = jobConfigurations,
                    labels = mapOf("label key" to "label value")
                )
            )
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<OrtRun> {
                example(
                    name = "Create ORT run",
                    value = OrtRun(
                        id = 1,
                        index = 2,
                        repositoryId = 1,
                        revision = "main",
                        createdAt = Clock.System.now(),
                        jobConfigs = jobConfigurations,
                        resolvedJobConfigs = jobConfigurations,
                        jobs = jobs,
                        status = OrtRunStatus.CREATED,
                        finishedAt = null,
                        labels = mapOf("label key" to "label value"),
                        issues = emptyList(),
                        jobConfigContext = null,
                        resolvedJobConfigContext = null
                    )
                )
            }
        }
    }
}

val getOrtRunByIndex: OpenApiRoute.() -> Unit = {
    operationId = "getOrtRunByIndex"
    summary = "Get details of an ORT run of a repository."
    tags = listOf("Repositories")

    request {
        pathParameter<Long>("repositoryId") {
            description = "The repository's ID."
        }

        pathParameter<Long>("ortRunIndex") {
            description = "The index of an ORT run."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<OrtRun> {
                example(
                    name = "Get ORT run",
                    value = OrtRun(
                        id = 1,
                        index = 2,
                        repositoryId = 1,
                        revision = "main",
                        createdAt = Clock.System.now(),
                        jobConfigs = jobConfigurations,
                        resolvedJobConfigs = jobConfigurations,
                        jobs = jobs,
                        status = OrtRunStatus.ACTIVE,
                        finishedAt = null,
                        labels = mapOf("label key" to "label value"),
                        issues = emptyList(),
                        jobConfigContext = null,
                        resolvedJobConfigContext = "32f955941e94d0a318e1c985903f42af924e9050"
                    )
                )
            }
        }
    }
}

val getSecretsByRepositoryId: OpenApiRoute.() -> Unit = {
    operationId = "GetSecretsByRepositoryId"
    summary = "Get all secrets of a repository."
    tags = listOf("Secrets")

    request {
        pathParameter<Long>("repositoryId") {
            description = "The ID of a repository."
        }
        standardListQueryParameters()
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<PagedResponse<Secret>> {
                example(
                    name = "Get all secrets of a repository",
                    value = PagedResponse(
                        listOf(
                            Secret(name = "rsa", description = "ssh rsa certificate"),
                            Secret(name = "secret", description = "another secret")
                        ),
                        ListQueryParameters(
                            sortFields = listOf(OrderField("name", ASCENDING)),
                            limit = 20,
                            offset = 0
                        )
                    )
                )
            }
        }
    }
}

val getSecretByRepositoryIdAndName: OpenApiRoute.() -> Unit = {
    operationId = "GetSecretByRepositoryIdAndName"
    summary = "Get details of a secret of a repository."
    tags = listOf("Secrets")

    request {
        pathParameter<Long>("repositoryId") {
            description = "The repository's ID."
        }
        pathParameter<String>("secretName") {
            description = "The secret's name."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<Secret> {
                example(
                    name = "Get Secret",
                    value = Secret(name = "rsa", description = "rsa certificate")
                )
            }
        }
    }
}

val postSecretForRepository: OpenApiRoute.() -> Unit = {
    operationId = "PostSecretForRepository"
    summary = "Create a secret for a repository."
    tags = listOf("Secrets")

    request {
        jsonBody<CreateSecret> {
            example(
                name = "Create Secret",
                value = CreateSecret(
                    name = "New secret",
                    value = "r3p0-s3cr3t-08_15",
                    description = "The new repo secret"
                )
            )
        }
    }

    response {
        HttpStatusCode.Created to {
            description = "Success"
            jsonBody<Secret> {
                example(
                    name = "Create Secret",
                    value = Secret(name = "rsa", description = "New secret")
                )
            }
        }
    }
}

val patchSecretByRepositoryIdAndName: OpenApiRoute.() -> Unit = {
    operationId = "PatchSecretByRepositoryIdIdAndName"
    summary = "Update a secret of a repository."
    tags = listOf("Secrets")

    request {
        pathParameter<Long>("repositoryIdId") {
            description = "The repository's ID."
        }
        pathParameter<String>("secretName") {
            description = "The secret's name."
        }
        jsonBody<UpdateSecret> {
            example(
                name = "Update Secret",
                value = UpdateSecret(
                    name = "My updated Secret".asPresent(),
                    value = "My updated value".asPresent(),
                    description = "Updated description".asPresent()
                )
            )
            description = "Set the values that should be updated. To delete a value, set it explicitly to null."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<Secret> {
                example(
                    name = "Update Secret",
                    value = Secret(name = "My updated Secret", description = "Updated description.")
                )
            }
        }
    }
}

val deleteSecretByRepositoryIdAndName: OpenApiRoute.() -> Unit = {
    operationId = "DeleteSecretByRepositoryIdAndName"
    summary = "Delete a secret from a repository."
    tags = listOf("Secrets")

    request {
        pathParameter<Long>("repositoryId") {
            description = "The repository's ID."
        }
        pathParameter<String>("secretName") {
            description = "The secret's name."
        }
    }

    response {
        HttpStatusCode.NoContent to {
            description = "Success"
        }
    }
}
