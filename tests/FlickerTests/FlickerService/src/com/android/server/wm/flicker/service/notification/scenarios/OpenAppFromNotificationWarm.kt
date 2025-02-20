/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wm.flicker.service.notification.scenarios

import android.app.Instrumentation
import android.tools.NavBar
import android.tools.Rotation
import android.tools.helpers.wakeUpAndGoToHomeScreen
import android.tools.traces.parsers.WindowManagerStateHelper
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.android.launcher3.tapl.LauncherInstrumentation
import com.android.server.wm.flicker.helpers.NotificationAppHelper
import com.android.server.wm.flicker.service.Utils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

abstract class OpenAppFromNotificationWarm(
    val gestureMode: NavBar = NavBar.MODE_GESTURAL,
    val rotation: Rotation = Rotation.ROTATION_0
) {
    private val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()
    private val tapl = LauncherInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    private val wmHelper = WindowManagerStateHelper(instrumentation)
    private val testApp: NotificationAppHelper = NotificationAppHelper(instrumentation)

    private val openingNotificationsFromLockScreen = false

    @Rule @JvmField val testSetupRule = Utils.testSetupRule(gestureMode, rotation)

    @Before
    fun setup() {
        device.wakeUpAndGoToHomeScreen()
        testApp.launchViaIntent(wmHelper)
        wmHelper.StateSyncBuilder().withFullScreenApp(testApp).waitForAndVerify()
        testApp.postNotification(wmHelper)
        device.pressHome()
        wmHelper.StateSyncBuilder().withHomeActivityVisible().waitForAndVerify()
    }

    @Test
    open fun openAppFromNotificationWarm() {
        NotificationUtils.openNotification(openingNotificationsFromLockScreen)
        // Wait for the app to launch
        wmHelper.StateSyncBuilder().withFullScreenApp(testApp).waitForAndVerify()
    }

    @After
    fun teardown() {
        testApp.exit(wmHelper)
    }
}
