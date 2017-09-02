package org.zeroxlab.momodict

object AppConstants {

    val BUILD_TYPE_RELEASE = "release"

    fun isReleaseBuild(): Boolean = BUILD_TYPE_RELEASE.equals(BuildConfig.BUILD_TYPE)
}
