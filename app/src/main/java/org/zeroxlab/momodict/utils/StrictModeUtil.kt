package org.zeroxlab.momodict

import android.os.StrictMode

object StrictModeUtil {

    fun enableInDevMode() {
        if (AppConstants.isReleaseBuild()) {
            return
        }

        val threadPolicy = StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDialog()
                .build()
        val vmPolicy = StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build()

        StrictMode.setVmPolicy(vmPolicy)
        StrictMode.setThreadPolicy(threadPolicy)
    }
}
