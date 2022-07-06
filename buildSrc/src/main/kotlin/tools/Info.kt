package tools

import java.util.Date

object Info {
    val timestamp = Date().time

    val canBuildMacIos: Boolean by lazy {
        System.getProperty("os.name") == "Mac OS X"
    }
}