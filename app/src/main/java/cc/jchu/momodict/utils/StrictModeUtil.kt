package cc.jchu.momodict

import android.os.StrictMode
import cc.jchu.momodict.utils.AppConstants

object StrictModeUtil {

    fun enableInDevMode() {
        if (AppConstants.isReleaseBuild()) {
            return
        }

        val threadPolicy = StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
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
