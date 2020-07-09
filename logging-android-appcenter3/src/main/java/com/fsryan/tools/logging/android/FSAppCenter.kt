package com.fsryan.tools.logging.android

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.annotation.MainThread
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import java.util.concurrent.atomic.AtomicBoolean

object FSAppCenter {

    /**
     * Threading: set on main thread. Read on any thread
     */
    internal val crashesEnabled = AtomicBoolean(false)
    /**
     * Threading: set on main thread. Read on any thread
     */
    internal val analyticsEnabled = AtomicBoolean(false)

    /**
     * Takes the configured properties from the meta-data of the application in
     * order to initialize
     */
    @MainThread
    fun ensureInitialized(context: Context) {
        val info = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val analyticsEnabled = info.metaData?.getBoolean("fsac_analytics_enabled", false) ?: true
        val crashesEnabled = info.metaData?.getBoolean("fsac_crashes_enabled", false) ?: true
        info.metaData?.getString("fsacsec")?.let { appSecret ->
            ensureInitialized(context, appSecret, analyticsEnabled, crashesEnabled)
        }
    }

    @MainThread
    fun ensureInitialized(context: Context, appSecret: String, analyticsEnabled: Boolean, crashesEnabled: Boolean) {
        if (Looper.getMainLooper().thread != Thread.currentThread()) {
            throw IllegalStateException("Can only initialize on Application main thread.")
        }

        this.analyticsEnabled.set(analyticsEnabled)
        this.crashesEnabled.set(crashesEnabled)
        if (AppCenter.isConfigured()) {
            Crashes.setEnabled(crashesEnabled)
            Analytics.setEnabled(analyticsEnabled)
        } else {
            AppCenter.start(
                context.applicationContext as Application,
                appSecret,
                Analytics::class.java,
                Crashes::class.java
            )
        }
    }
}