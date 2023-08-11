/*
 * Copyright (C) 2022 The ORT Project Authors (See <https://github.com/oss-review-toolkit/ort-server/blob/main/NOTICE>)
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

package org.ossreviewtoolkit.server.core.apiDocs

import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute

import io.ktor.http.HttpStatusCode

import org.ossreviewtoolkit.server.api.v1.CreateInfrastructureService
import org.ossreviewtoolkit.server.api.v1.CreateOrganization
import org.ossreviewtoolkit.server.api.v1.CreateProduct
import org.ossreviewtoolkit.server.api.v1.CreateSecret
import org.ossreviewtoolkit.server.api.v1.InfrastructureService
import org.ossreviewtoolkit.server.api.v1.Organization
import org.ossreviewtoolkit.server.api.v1.Product
import org.ossreviewtoolkit.server.api.v1.Secret
import org.ossreviewtoolkit.server.api.v1.UpdateInfrastructureService
import org.ossreviewtoolkit.server.api.v1.UpdateOrganization
import org.ossreviewtoolkit.server.api.v1.UpdateSecret
import org.ossreviewtoolkit.server.model.util.asPresent

val getOrganizationById: OpenApiRoute.() -> Unit = {
    operationId = "GetOrganizationById"
    summary = "Get details of an organization."
    tags = listOf("Organizations")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<Organization> {
                example(
                    name = "Get Organization",
                    value = Organization(id = 1, name = "My Organization", description = "This is my organization.")
                )
            }
        }
    }
}

val getOrganizations: OpenApiRoute.() -> Unit = {
    operationId = "GetOrganizations"
    summary = "Get all organizations."
    tags = listOf("Organizations")

    request {
        standardListQueryParameters()
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<List<Organization>> {
                example(
                    name = "List all organizations",
                    value = listOf(
                        Organization(id = 1, name = "First Organization", description = "Description"),
                        Organization(id = 2, name = "Second Organization")
                    )
                )
            }
        }
    }
}

val postOrganizations: OpenApiRoute.() -> Unit = {
    operationId = "PostOrganizations"
    summary = "Create an organization."
    tags = listOf("Organizations")

    request {
        jsonBody<CreateOrganization> {
            example(
                name = "Create Organization",
                value = CreateOrganization(name = "My Organization", description = "This is my organization.")
            )
        }
    }

    response {
        HttpStatusCode.Created to {
            description = "Success"
            jsonBody<Organization> {
                example(
                    name = "Create Organization",
                    value = Organization(id = 1, name = "My Organization", description = "This is my organization.")
                )
            }
        }
    }
}

val patchOrganizationById: OpenApiRoute.() -> Unit = {
    operationId = "PatchOrganizationById"
    summary = "Update an organization."
    tags = listOf("Organizations")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
        }
        jsonBody<UpdateOrganization> {
            description = "Set the values that should be updated. To delete a value, set it explicitly to null."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<Organization> {
                example(
                    name = "Update Organization",
                    value = Organization(id = 1, name = "My updated Organization", description = "Updated description.")
                )
            }
        }
    }
}

val deleteOrganizationById: OpenApiRoute.() -> Unit = {
    operationId = "DeleteOrganizationById"
    summary = "Delete an organization."
    tags = listOf("Organizations")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
        }
    }

    response {
        HttpStatusCode.NoContent to {
            description = "Success"
        }
    }
}

val getOrganizationProducts: OpenApiRoute.() -> Unit = {
    operationId = "GetOrganizationProducts"
    summary = "Get all products of an organization."
    tags = listOf("Products")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
        }

        standardListQueryParameters()
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<List<Product>> {
                example(
                    name = "Get products of an organization",
                    value = listOf(
                        Product(id = 1, name = "My first product", description = "Description"),
                        Product(id = 2, name = "My second product")
                    )
                )
            }
        }
    }
}

val postProduct: OpenApiRoute.() -> Unit = {
    operationId = "PostProduct"
    summary = "Create a product for an organization."
    tags = listOf("Products")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
        }
        jsonBody<CreateProduct> {
            example(
                name = "Create product",
                value = CreateProduct(name = "My product", description = "Description")
            )
        }
    }

    response {
        HttpStatusCode.Created to {
            description = "Success"
            jsonBody<Product> {
                example(
                    name = "Create product",
                    value = Product(id = 1, name = "My product", description = "Description")
                )
            }
        }
    }
}

val getSecretsByOrganizationId: OpenApiRoute.() -> Unit = {
    operationId = "GetSecretsByOrganizationId"
    summary = "Get all secrets of an organization."
    tags = listOf("Secrets")

    request {
        pathParameter<Long>("organizationId") {
            description = "The ID of an organization."
        }
        standardListQueryParameters()
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<List<Secret>> {
                example(
                    name = "Get all secrets of an organization",
                    value = listOf(
                        Secret(name = "rsa", description = "rsa certificate"),
                        Secret(name = "secret", description = "another secret")
                    )
                )
            }
        }
    }
}

val getSecretByOrganizationIdAndName: OpenApiRoute.() -> Unit = {
    operationId = "GetSecretByOrganizationIdAndName"
    summary = "Get details of a secret of an organization."
    tags = listOf("Secrets")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
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

val postSecretForOrganization: OpenApiRoute.() -> Unit = {
    operationId = "PostSecretForOrganization"
    summary = "Create a secret for an organization."
    tags = listOf("Secrets")

    request {
        jsonBody<CreateSecret> {
            example(
                name = "Create Secret",
                value = CreateSecret(
                    name = "New secret",
                    value = "0rg-s3cr3t-08_15",
                    description = "The new org secret"
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

val patchSecretByOrganizationIdAndName: OpenApiRoute.() -> Unit = {
    operationId = "PatchSecretByOrganizationIdAndName"
    summary = "Update a secret of an organization."
    tags = listOf("Secrets")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
        }
        pathParameter<String>("secretName") {
            description = "The secret's name."
        }
        jsonBody<UpdateSecret> {
            example(
                name = "Update Secret",
                value = UpdateSecret(
                    name = "My updated Secret".asPresent(),
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

val deleteSecretByOrganizationIdAndName: OpenApiRoute.() -> Unit = {
    operationId = "DeleteSecretByOrganizationIdAndName"
    summary = "Delete a secret from an organization."
    tags = listOf("Secrets")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
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

val getInfrastructureServicesByOrganizationId: OpenApiRoute.() -> Unit = {
    operationId = "GetInfrastructureServicesByOrganizationId"
    summary = "List all infrastructure services of an organization."
    tags = listOf("Infrastructure services")

    request {
        pathParameter<Long>("organizationId") {
            description = "The ID of an organization."
        }
        standardListQueryParameters()
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<List<InfrastructureService>> {
                example(
                    name = "List all infrastructure services for an organization",
                    value = listOf(
                        InfrastructureService(
                            name = "Artifactory",
                            url = "https://artifactory.example.org/releases",
                            description = "Artifactory repository",
                            usernameSecretRef = "artifactoryUsername",
                            passwordSecretRef = "artifactoryPassword"
                        ),
                        InfrastructureService(
                            name = "GitHub",
                            url = "https://github.com",
                            description = "GitHub server",
                            usernameSecretRef = "gitHubUsername",
                            passwordSecretRef = "gitHubPassword"
                        )
                    )
                )
            }
        }
    }
}

val postInfrastructureServiceForOrganization: OpenApiRoute.() -> Unit = {
    operationId = "PostInfrastructureServiceForOrganization"
    summary = "Create an infrastructure service for an organization."
    tags = listOf("Infrastructure services")

    request {
        jsonBody<CreateInfrastructureService> {
            example(
                name = "Create infrastructure service",
                value = CreateInfrastructureService(
                    name = "Artifactory",
                    url = "https://artifactory.example.org/releases",
                    description = "Artifactory repository",
                    usernameSecretRef = "artifactoryUsername",
                    passwordSecretRef = "artifactoryPassword"
                )
            )
        }
    }

    response {
        HttpStatusCode.Created to {
            description = "Success"
            jsonBody<InfrastructureService> {
                example(
                    name = "Create infrastructure service",
                    value = InfrastructureService(
                        name = "Artifactory",
                        url = "https://artifactory.example.org/releases",
                        description = "Artifactory repository",
                        usernameSecretRef = "artifactoryUsername",
                        passwordSecretRef = "artifactoryPassword"
                    )
                )
            }
        }
    }
}

val patchInfrastructureServiceForOrganizationIdAndName: OpenApiRoute.() -> Unit = {
    operationId = "PatchInfrastructureServiceForOrganizationIdAndName"
    summary = "Update an infrastructure service for an organization."
    tags = listOf("Infrastructure services")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
        }
        pathParameter<String>("serviceName") {
            description = "The name of the infrastructure service."
        }
        jsonBody<UpdateInfrastructureService> {
            example(
                name = "Update infrastructure service",
                value = UpdateInfrastructureService(
                    url = "https://github.com".asPresent(),
                    description = "Updated description".asPresent(),
                    passwordSecretRef = "newGitHubPassword".asPresent()
                )
            )
            description = "Set the values that should be updated. To delete a value, set it explicitly to null."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<InfrastructureService> {
                example(
                    name = "Update infrastructure service",
                    value = InfrastructureService(
                        name = "GitHub",
                        url = "https://github.com",
                        description = "Updated description",
                        usernameSecretRef = "gitHubUsername",
                        passwordSecretRef = "newGitHubPassword"
                    )
                )
            }
        }
    }
}

val deleteInfrastructureServiceForOrganizationIdAndName: OpenApiRoute.() -> Unit = {
    operationId = "DeleteInfrastructureServiceForOrganizationIdAndName"
    summary = "Delete an infrastructure service from an organization."
    tags = listOf("Infrastructure services")

    request {
        pathParameter<Long>("organizationId") {
            description = "The organization's ID."
        }
        pathParameter<String>("serviceName") {
            description = "The name of the infrastructure service."
        }
    }

    response {
        HttpStatusCode.NoContent to {
            description = "Success"
        }
    }
}
