package com.xuandq.mylauncher.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xuandq.mylauncher.model.App
import com.xuandq.mylauncher.model.Item
import com.xuandq.mylauncher.utils.TaskGetApps


class AppViewModel : ViewModel() {

    var listApps = MutableLiveData<List<App>>()
    var allListItem = MutableLiveData<List<List<Item>>>()
    var listPage = MutableLiveData<List<Fragment>>()

    fun fetchListApps(context: Context) {
        val task = @SuppressLint("StaticFieldLeak")
        object : TaskGetApps(context) {
            override fun onPostExecute(result: List<App>?) {
                super.onPostExecute(result)
                listApps.value = result
            }
        }
        task.execute()
    }

    fun fetchAllListItem(){

    }
}