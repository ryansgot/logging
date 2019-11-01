package com.fsryan.tools.logging.android

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

/**
 * A [ContentProvider] which will start [AppCenter] at the earliest moment so
 * you can start catching crashes and logging analytics events ASAP. If you
 * ever resolve this [ContentProvider] and attempt to access any of its
 * methods, then your app will crash.
 */
class AppCenterInitializationProvider : ContentProvider() {

    override fun attachInfo(context: Context, info: ProviderInfo) {
        super.attachInfo(context, info)
        AppCenter.start(
            context.applicationContext as Application,
            discoverAppSecret(context, info.authority),
            Analytics::class.java,
            Crashes::class.java
        )
    }

    override fun onCreate(): Boolean = true

    override fun getType(uri: Uri): String? = throw UnsupportedOperationException()
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = throw UnsupportedOperationException()
    override fun insert(uri: Uri, values: ContentValues?): Uri? = throw UnsupportedOperationException()
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = throw UnsupportedOperationException()
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = throw UnsupportedOperationException()

    private fun discoverAppSecret(context: Context, authority: String): String {
        val info = context.packageManager.resolveContentProvider(authority, PackageManager.GET_META_DATA)
        val ret = info?.metaData?.getString("fsacsec", "") ?: ""
        if (ret.isEmpty()) {
            throw Exception("Your provider must have meta-data indicating the app secret")
        }
        return ret
    }
}