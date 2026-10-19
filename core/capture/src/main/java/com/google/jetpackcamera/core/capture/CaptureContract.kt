/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package com.google.jetpackcamera.core.capture

import kotlinx.coroutines.flow.StateFlow

enum class CaptureMode {
    CAMERA,
    SCREEN,
}

enum class CaptureState {
    IDLE,
    STARTING,
    CAPTURING,
    STOPPING,
    COMPLETED,
    FAILED,
}

data class CaptureConfig(
    val mode: CaptureMode,
    val outputName: String,
    val includeMicrophone: Boolean = false,
    val projectionResultData: ByteArray? = null,
) {
    init {
        require(outputName.isNotBlank()) { "outputName must not be blank" }
        if (mode == CaptureMode.SCREEN) {
            require(projectionResultData != null) {
                "Screen capture requires MediaProjection consent data"
            }
        }
    }
}

sealed interface CaptureCommand {
    data class Start(val config: CaptureConfig) : CaptureCommand
    data object Stop : CaptureCommand
}

interface CaptureSession {
    val state: StateFlow<CaptureState>
    suspend fun handle(command: CaptureCommand)
}
