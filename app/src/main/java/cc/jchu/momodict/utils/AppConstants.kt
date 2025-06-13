package cc.jchu.momodict.utils

object AppConstants {

    val BUILD_TYPE_RELEASE = "release"

    //FIXME
    fun isReleaseBuild(): Boolean = BUILD_TYPE_RELEASE.equals(true)
}
