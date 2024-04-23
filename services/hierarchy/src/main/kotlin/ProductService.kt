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

package org.eclipse.apoapsis.ortserver.services

import org.eclipse.apoapsis.ortserver.dao.dbQuery
import org.eclipse.apoapsis.ortserver.dao.dbQueryCatching
import org.eclipse.apoapsis.ortserver.model.Product
import org.eclipse.apoapsis.ortserver.model.Repository
import org.eclipse.apoapsis.ortserver.model.RepositoryType
import org.eclipse.apoapsis.ortserver.model.repositories.ProductRepository
import org.eclipse.apoapsis.ortserver.model.repositories.RepositoryRepository
import org.eclipse.apoapsis.ortserver.model.util.ListQueryParameters
import org.eclipse.apoapsis.ortserver.model.util.OptionalValue

import org.jetbrains.exposed.sql.Database

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(OrganizationService::class.java)

/**
 * A service providing functions for working with [products][Product].
 */
class ProductService(
    private val db: Database,
    private val productRepository: ProductRepository,
    private val repositoryRepository: RepositoryRepository,
    private val authorizationService: AuthorizationService
) {
    /**
     * Create a repository inside a [product][productId].
     */
    suspend fun createRepository(type: RepositoryType, url: String, productId: Long): Repository = db.dbQueryCatching {
        repositoryRepository.create(type, url, productId)
    }.onSuccess { repository ->
        runCatching {
            authorizationService.createRepositoryPermissions(repository.id)
            authorizationService.createRepositoryRoles(repository.id)
        }.onFailure { e ->
            logger.error("Error while creating Keycloak roles for repository '${repository.id}'.", e)
        }
    }.getOrThrow()

    /**
     * Delete a product by [productId].
     */
    suspend fun deleteProduct(productId: Long): Unit = db.dbQueryCatching {
        productRepository.delete(productId)
    }.onSuccess {
        runCatching {
            authorizationService.deleteProductPermissions(productId)
            authorizationService.deleteProductRoles(productId)
        }.onFailure { e ->
            logger.error("Error while deleting Keycloak roles for product '$productId'.", e)
        }
    }.getOrThrow()

    /**
     * Get a product by [productId]. Returns null if the product is not found.
     */
    suspend fun getProduct(productId: Long): Product? = db.dbQuery {
        productRepository.get(productId)
    }

    /**
     * List all repositories for a [product][productId] according to the given [parameters].
     */
    suspend fun listRepositoriesForProduct(
        productId: Long,
        parameters: ListQueryParameters = ListQueryParameters.DEFAULT
    ): List<Repository> = db.dbQuery {
        repositoryRepository.listForProduct(productId, parameters)
    }

    /**
     * Update a product by [productId] with the [present][OptionalValue.Present] values.
     */
    suspend fun updateProduct(
        productId: Long,
        name: OptionalValue<String> = OptionalValue.Absent,
        description: OptionalValue<String?> = OptionalValue.Absent
    ): Product = db.dbQuery {
        productRepository.update(productId, name, description)
    }
}
