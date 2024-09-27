/*
 * Copyright (C) 2024 The ORT Server Authors (See <https://github.com/eclipse-apoapsis/ort-server/blob/main/NOTICE>)
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

import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRoute

import io.ktor.http.HttpStatusCode

import org.eclipse.apoapsis.ortserver.api.v1.model.CreateUser

val runPermissionsSync: OpenApiRoute.() -> Unit = {
    operationId = "runPermissionsSync"
    summary = ""
    tags = listOf("Admin")

    request {
    }

    response {
        HttpStatusCode.Accepted to {
            description = "Success."
        }

        HttpStatusCode.Unauthorized to {
            description = "Unauth."
        }
    }
}

val postUsers: OpenApiRoute.() -> Unit = {
    operationId = "postUsers"
    summary = "Create a user, possibly with a password. This is enabled for server administrators only."
    tags = listOf("Admin")

    request {
        jsonBody<CreateUser> {
            example("Create User") {
                value = CreateUser(username = "newUser", password = "password", temporary = true)
                description = "temporary=true means the password is for one-time use only and needs to be changed " +
                        "on first login. If password is not set, temporary is ignored."
            }
        }
    }

    response {
        HttpStatusCode.Created to {
            description = "Successfully created the user."
        }

        // Note: Keycloak doesn't distinguish technical from logical errors; it just returns 500 for both.
        HttpStatusCode.InternalServerError to {
            description = "A user with the same username already exists."
        }
    }
}
