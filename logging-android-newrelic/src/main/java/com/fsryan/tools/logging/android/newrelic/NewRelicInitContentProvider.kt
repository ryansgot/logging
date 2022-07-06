package com.fsryan.tools.logging.android.newrelic

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri

/**
 * This [ContentProvider] does not provide content, but it does hook into the
 * application lifecycle _BEFORE_ the onCreate function of the application is
 * called. We need this to start NewRelic as early as possible in a way that
 * cannot be interrupted or duplicated because the underlying NewRelic library
 * is not thread-safe with respect to initialization.
 *
 * We must initialize on the main thread because the NewRelic Agent captures
 * its own logs on the main thread.
 *
 * Even still, it is possible that some logs may be missed due to not correctly
 * flushing NewRelic's static value from the UI thread to the FS logging
 * thread. This content provider makes the likelihood of that happening quite
 * low, though.
 */
class NewRelicInitContentProvider : ContentProvider() {

    override fun attachInfo(context: Context, info: ProviderInfo) {
        super.attachInfo(context, info)
        context.startNewRelicIfNecessary()
    }
    override fun onCreate(): Boolean = true
    override fun getType(uri: Uri): String? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? = null
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
}