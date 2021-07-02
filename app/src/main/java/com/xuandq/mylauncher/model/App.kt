package com.xuandq.mylauncher.model

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.LauncherActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.UserHandle
import android.util.DisplayMetrics
import android.util.Log

class App {
    var icon: Drawable
    var label: String
    var packageName: String
    var className: String
    var userHandle: UserHandle? = null

    constructor(pm: PackageManager, info: ResolveInfo) {
        icon = info.loadIcon(pm)

        label = info.loadLabel(pm).toString()
        packageName = info.activityInfo.packageName
        className = info.activityInfo.name
    }

    @SuppressLint("NewApi")
    constructor(pm: PackageManager, info: LauncherActivityInfo) {
        icon = info.getIcon(0)
//        Log.d("vanh", "api: ${info.getBadgedIcon(0)}")
        label = info.label.toString()
        packageName = info.componentName.packageName
        className = info.name
    }

    override fun equals(`object`: Any?): Boolean {
        return if (`object` is App) {
            packageName == `object`.packageName
        } else {
            false
        }
    }

    val componentName: String
        get() = ComponentName(packageName, className).toString()
}


