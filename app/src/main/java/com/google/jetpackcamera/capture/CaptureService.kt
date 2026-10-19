/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package com.google.jetpackcamera.capture

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.google.jetpackcamera.R
import com.google.jetpackcamera.core.capture.CaptureMode

class CaptureService : Service() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val mode = intent.getStringExtra(EXTRA_MODE)
                    ?.let { value -> CaptureMode.entries.firstOrNull { it.name == value } }
                    ?: CaptureMode.CAMERA
                startForegroundCompat(mode, intent.getBooleanExtra(EXTRA_INCLUDE_MICROPHONE, false))
            }

            ACTION_STOP -> stopCapture()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundCompat(mode: CaptureMode, includeMicrophone: Boolean) {
        val baseType = when (mode) {
            CaptureMode.CAMERA -> ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
            CaptureMode.SCREEN -> ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
        }
        val type = if (includeMicrophone) {
            baseType or ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
        } else {
            baseType
        }
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification(mode), type)
    }

    private fun stopCapture() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun notification(mode: CaptureMode): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name_short))
            .setContentText(getString(if (mode == CaptureMode.CAMERA) R.string.capture_camera_active else R.string.capture_screen_active))
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.capture_notification_channel),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )
    }

    companion object {
        const val ACTION_START = "com.google.jetpackcamera.capture.START"
        const val ACTION_STOP = "com.google.jetpackcamera.capture.STOP"
        const val EXTRA_MODE = "com.google.jetpackcamera.capture.MODE"
        const val EXTRA_INCLUDE_MICROPHONE = "com.google.jetpackcamera.capture.INCLUDE_MICROPHONE"
        private const val CHANNEL_ID = "capture"
        private const val NOTIFICATION_ID = 4101
    }
}
