/*
 * Copyright (C) 2023 The ORT Project Authors (See <https://github.com/oss-review-toolkit/ort-server/blob/main/NOTICE>)
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

package org.ossreviewtoolkit.server.dao.repositories

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

import kotlin.time.Duration.Companion.seconds

import kotlinx.datetime.Clock

import org.ossreviewtoolkit.server.dao.utils.toDatabasePrecision
import org.ossreviewtoolkit.server.model.JobStatus
import org.ossreviewtoolkit.server.model.WorkerJob
import org.ossreviewtoolkit.server.model.repositories.WorkerJobRepository

/**
 * An abstract test class that contains common tests for all [WorkerJobRepository] implementations.
 */
abstract class WorkerJobRepositoryTest<T : WorkerJob> : StringSpec() {
    abstract fun createJob(): T
    abstract fun getJobRepository(): WorkerJobRepository<T>

    init {
        "complete should mark a job as completed" {
            val job = createJob()

            val updatedFinishedAt = Clock.System.now()
            val updateStatus = JobStatus.FINISHED

            val updateResult = getJobRepository().complete(job.id, updatedFinishedAt, updateStatus)

            listOf(updateResult, getJobRepository().get(job.id)).forAll {
                it.shouldNotBeNull()
                it.finishedAt shouldBe updatedFinishedAt.toDatabasePrecision()
                it.status shouldBe updateStatus
            }
        }

        "tryComplete should mark a job as completed" {
            val job = createJob()

            val updatedFinishedAt = Clock.System.now()
            val updateStatus = JobStatus.FINISHED

            val updateResult = getJobRepository().tryComplete(job.id, updatedFinishedAt, updateStatus)

            listOf(updateResult, getJobRepository().get(job.id)).forAll {
                it.shouldNotBeNull()
                it.finishedAt shouldBe updatedFinishedAt.toDatabasePrecision()
                it.status shouldBe updateStatus
            }
        }

        "tryComplete should not change an already completed job" {
            val job = createJob()

            val updatedFinishedAt = Clock.System.now()
            val updateStatus = JobStatus.FAILED
            getJobRepository().complete(job.id, updatedFinishedAt, updateStatus)

            val updateResult =
                getJobRepository().tryComplete(job.id, updatedFinishedAt.plus(10.seconds), JobStatus.FINISHED)

            updateResult should beNull()

            with(getJobRepository().get(job.id)) {
                this.shouldNotBeNull()
                finishedAt shouldBe updatedFinishedAt.toDatabasePrecision()
                status shouldBe updateStatus
            }
        }

        "tryComplete should fail for a non-existing job" {
            shouldThrow<IllegalArgumentException> {
                getJobRepository().tryComplete(-1, Clock.System.now(), JobStatus.FAILED)
            }
        }
    }
}
