/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.tracing.internal.domain

import android.content.Context
import com.datadog.android.core.internal.domain.AsyncWriterFilePersistenceStrategy
import com.datadog.android.core.internal.domain.PayloadDecoration
import com.datadog.android.core.internal.net.info.NetworkInfoProvider
import com.datadog.android.core.internal.time.TimeProvider
import com.datadog.android.log.internal.user.UserInfoProvider
import datadog.opentracing.DDSpan
import java.io.File
import java.util.concurrent.ExecutorService

internal class TracingFileStrategy(
    context: Context,
    timeProvider: TimeProvider,
    networkInfoProvider: NetworkInfoProvider,
    userInfoProvider: UserInfoProvider,
    recentDelayMs: Long = MAX_DELAY_BETWEEN_LOGS_MS,
    maxBatchSize: Long = MAX_BATCH_SIZE,
    maxLogPerBatch: Int = MAX_ITEMS_PER_BATCH,
    oldFileThreshold: Long = OLD_FILE_THRESHOLD,
    maxDiskSpace: Long = MAX_DISK_SPACE,
    envName: String = "",
    dataPersistenceExecutorService: ExecutorService
) : AsyncWriterFilePersistenceStrategy<DDSpan>(
    File(context.filesDir, TRACES_FOLDER),
    SpanSerializer(timeProvider, networkInfoProvider, userInfoProvider, envName),
    recentDelayMs,
    maxBatchSize,
    maxLogPerBatch,
    oldFileThreshold,
    maxDiskSpace,
    PayloadDecoration.NEW_LINE_DECORATION,
    dataPersistenceExecutorService = dataPersistenceExecutorService
) {
    companion object {
        internal const val TRACES_DATA_VERSION = 1
        internal const val DATA_FOLDER_ROOT = "dd-tracing"
        internal const val TRACES_FOLDER = "$DATA_FOLDER_ROOT-v$TRACES_DATA_VERSION"
        internal const val MAX_DELAY_BETWEEN_LOGS_MS = 5000L
    }
}
