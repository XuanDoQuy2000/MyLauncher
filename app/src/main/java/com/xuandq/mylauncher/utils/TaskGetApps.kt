package com.xuandq.mylauncher.utils

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import com.xuandq.mylauncher.model.App

abstract class TaskGetApps(val context: Context) : AsyncTask<Any, Any, List<App>>() {
    private val TAG = "TaskGetApps"
    private val packageManager = context.packageManager
    override fun doInBackground(vararg p0: Any?): List<App> {
        Log.d(TAG, "doInBackground: ")

        val nonFilteredAppsTemp = ArrayList<App>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val launcherApps =
                context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val profiles = launcherApps.profiles
            for (userHandle in profiles) {
                val apps =
                    launcherApps.getActivityList(null, userHandle)
                for (info in apps) {
                    val app = App(packageManager, info)
                    app.userHandle = userHandle
                    nonFilteredAppsTemp!!.add(app)
                }
                Log.d(TAG, "doInBackground: "+apps.size)
            }
        } else {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val activitiesInfo =
                packageManager.queryIntentActivities(intent, 0)
            for (info in activitiesInfo) {
                val app = App(packageManager, info)
                nonFilteredAppsTemp!!.add(app)
            }
        }

        nonFilteredAppsTemp?.sortBy {
            it.label
        }

        return nonFilteredAppsTemp
    }
}