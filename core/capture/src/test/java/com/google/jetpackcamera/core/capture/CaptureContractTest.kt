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

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CaptureContractTest {
    @Test
    fun cameraConfig_doesNotRequireProjectionConsent() {
        val config = CaptureConfig(CaptureMode.CAMERA, "camera.mp4")

        assertThat(config.mode).isEqualTo(CaptureMode.CAMERA)
    }

    @Test(expected = IllegalArgumentException::class)
    fun screenConfig_requiresProjectionConsent() {
        CaptureConfig(CaptureMode.SCREEN, "screen.mp4")
    }
}
