package com.xuandq.mylauncher.utils

import android.content.Context
import android.content.SharedPreferences

object AppSetting {

    private var sharedPre: SharedPreferences? = null
    private var typeLayout: Int? = 1
    var numRow = 5
    @JvmField
    var pageSize = 20

    fun init(context: Context) {
        sharedPre = context.getSharedPreferences("xuandq", Context.MODE_PRIVATE)
        typeLayout = sharedPre?.getInt("layout", 1)
        if (AppSetting.typeLayout == 1) {
            numRow = 5
            pageSize = 20
        } else {
            numRow = 6
            pageSize = 24
        }
    }

    fun resetLayout(context: Context){
        sharedPre!!.edit().putBoolean("firstInstalled", true).commit()
    }

    fun setLayout(typeLayout: Int) {
        sharedPre!!.edit().putInt("layout", typeLayout).commit()
        this.typeLayout = typeLayout

    }
}
