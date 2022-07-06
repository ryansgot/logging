package com.fsryan.tools.logging.android

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.MainThread
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.AppCenterService
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
     * order to initialize when [initFSLogging] is called from meta-data
     * attached to the context.
     *
     * You can set the following meta-data:
     * ```
     * <!-- You should not store your app secret in code here, instead, you can
     *      take it from manifest placeholders as you define in your
     *      build.gradle file
     * -->
     * <meta-data android:name="fsacsec" android:value="Your app secret" />
     * <!-- Crashes will be enabled by default -->
     * <meta-data android:name="fsac_crashes_enabled" android:value="true" />
     * <!-- Analytics will be enabled by default -->
     * <meta-data android:name="fsac_analytics_enabled" android:value="true" />
     * ```
     */
    @MainThread
    internal fun ensureInitialized(context: Context) {
        val info = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val analyticsEnabled = info.metaData?.getBoolean("fsac_analytics_enabled", false) ?: true
        val crashesEnabled = info.metaData?.getBoolean("fsac_crashes_enabled", false) ?: true
        info.metaData?.getString("fsacsec")?.let { appSecret ->
            ensureInitialized(context, appSecret, analyticsEnabled, crashesEnabled)
        }
    }

    /**
     * If you want to delay the initiation of AppCenter, then you can use this
     * function instead. You may want to do this if there are tight regulations
     * on data you can collect.
     */
    fun ensureInitialized(
        context: Context,
        appSecret: String,
        analyticsEnabled: Boolean,
        crashesEnabled: Boolean
    ) {
        this.analyticsEnabled.set(analyticsEnabled)
        this.crashesEnabled.set(crashesEnabled)
        if (AppCenter.isConfigured()) {
            Crashes.setEnabled(crashesEnabled)
            Analytics.setEnabled(analyticsEnabled)
        } else {
            val toStart = mutableListOf<Class<out AppCenterService>>()
            if (analyticsEnabled) {
                toStart.add(Analytics::class.java)
            }
            if (crashesEnabled) {
                toStart.add(Crashes::class.java)
            }
            if (toStart.isNotEmpty()) {
                AppCenter.start(
                    context.applicationContext as Application,
                    appSecret,
                    *toStart.toTypedArray()
                )
            }
        }
    }
}