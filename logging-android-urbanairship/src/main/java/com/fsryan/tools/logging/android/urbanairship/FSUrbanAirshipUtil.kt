@file:JvmName("FSUrbanAirshipUtil")

package com.fsryan.tools.logging.android.urbanairship

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.MainThread
import com.fsryan.tools.logging.FSEventLog

@Volatile internal lateinit var trackScreenAttrName: String

@MainThread
internal fun ensureInitialized(context: Context) {
    val appInfo = context.packageManager
        .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    trackScreenAttrName = appInfo.metaData.getString("fsryan.log.screen_attr_name", "fs_screen_attr_name")
}

fun FSEventLog.updateUrbanAirshipScreen(screenName: String) {
    addAttr(trackScreenAttrName, screenName, "urbanairship")
}