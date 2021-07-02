package com.xuandq.mylauncher.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import com.xuandq.mylauncher.fragment.AppFragment
import com.xuandq.mylauncher.model.App
import com.xuandq.mylauncher.model.Item
import java.io.*

class AppRepository {

    private var allListItem = ArrayList<ArrayList<Item>>()
    private var listPageApp = ArrayList<Fragment>()
    private var listDock = ArrayList<Item>()
    private var listApps = ArrayList<App>()
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val TAG = "AppRepository"
        private var INSTANCE: AppRepository? = null
        fun getInstance() = INSTANCE ?: AppRepository().also {
            INSTANCE = it
        }
    }

    fun saveAllListItem(context: Context, allListItem: List<List<Item>>) {
        val listNoIcon = ArrayList<ArrayList<Item>>()
        allListItem.forEach {
            val temp = ArrayList<Item>()
            it.forEach {
                val cloned = it.copy()
                cloned.icon = null
                if (it.items != null && it.items!!.size > 0){
                    val temp = ArrayList<Item>()
                    it.items!!.forEach {
                        val cloned = it.copy()
                        cloned.icon = null
                        temp.add(cloned)
                    }
                    cloned.items = temp
                }
                temp.add(cloned)
            }
            listNoIcon.add(temp)
        }
        try {
            val file = File(context.filesDir, "all_list_item.txt")
            file.createNewFile()
            val output = ObjectOutputStream(FileOutputStream(file))
            output.writeObject(listNoIcon)
            output.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun readAllListItem(context: Context): ArrayList<ArrayList<Item>> {
        var allListItem: ArrayList<ArrayList<Item>> = ArrayList<ArrayList<Item>>()
        try {
            val file = File(context.filesDir, "all_list_item.txt")
            file.createNewFile()
            val input = ObjectInputStream(FileInputStream(file))
            allListItem = input.readObject() as ArrayList<ArrayList<Item>>
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return allListItem
    }

    fun saveListDock(context: Context, listItem: List<Item>) {
        val listNoIcon = ArrayList<Item>()
        listItem.forEach {
            val cloned = it.copy()
            cloned.icon = null
            listNoIcon.add(cloned)
        }

        try {
            val file = File(context.filesDir, "list_dock.txt")
            file.createNewFile()
            val output = ObjectOutputStream(FileOutputStream(file))
            output.writeObject(listNoIcon)
            output.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun readListDock(context: Context): ArrayList<Item> {
        var listItem: ArrayList<Item> = ArrayList<Item>()
        try {
            val file = File(context.filesDir, "list_dock.txt")
            file.createNewFile()
            val input = ObjectInputStream(FileInputStream(file))
            listItem = input.readObject() as ArrayList<Item>
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return listItem
    }

    fun getAllListItem(context: Context, callBack: (ArrayList<Item>, ArrayList<Fragment>) -> Unit) {

        sharedPreferences = context.getSharedPreferences("xuandq", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("firstInstalled", true)) {
            sharedPreferences.edit().putBoolean("firstInstalled", false).commit()

            val taskGetApps = @SuppressLint("StaticFieldLeak")
            object : TaskGetApps(context){
                override fun onPostExecute(result: List<App>?) {
                    super.onPostExecute(result)

                    listApps = result as ArrayList<App>

                    allListItem.clear()
                    var k = 4
                    val listTemp = ArrayList<Item>()
                    while (k < listApps.size) {
                        listTemp.add(Item.newAppItem(listApps[k]))
                        k++
                        if (listTemp.size == AppSetting.pageSize || k == listApps.size) {
                            allListItem.add(ArrayList(listTemp))
                            listTemp.clear()
                        }
                    }

                    listPageApp.clear()
                    for (l in allListItem) {
                        listPageApp.add(AppFragment(l))
                    }

                    listDock.clear()
                    listDock.add(Item.newAppItem(listApps[0]))
                    listDock.add(Item.newAppItem(listApps[1]))
                    listDock.add(Item.newAppItem(listApps[2]))
                    listDock.add(Item.newAppItem(listApps[3]))


                    saveAllListItem(context, allListItem)
                    saveListDock(context, listDock)

                    listApps.forEach {
                        val bitmap = Tool.drawableToBitmap(it.icon)
                        if (bitmap == null) Log.d(TAG, "convert bitmap null")
                        saveIcon(context,bitmap!!,it.packageName)
                    }

                    Log.d(TAG, "onPostExecute: all list size" + allListItem.size)

                    callBack.invoke(listDock, listPageApp)
                }
            }
            taskGetApps.execute()

        } else {

            val task = object : AsyncTask<Unit, Unit, Unit>(){
                override fun doInBackground(vararg params: Unit?) {
                    val listNoIcon = readAllListItem(context)
                    listNoIcon.forEach {
                        it.forEach {
                            Log.d(TAG, "getAllListItem: ${it.label}")
                            it.icon = getIcon(context, it.packageName)
                            if (it.items != null && it.items!!.size > 0) {
                                it.items!!.forEach {
                                    it.icon = getIcon(context, it.packageName)
                                }
                            }
                        }
                    }

                    allListItem = listNoIcon

                    val listDockNoIcon = readListDock(context)
                    listDockNoIcon.forEach {
                        it.icon = getIcon(context, it.packageName)
                        if (it.items != null && it.items!!.size > 0) {
                            it.items!!.forEach {
                                it.icon = getIcon(context, it.packageName)
                            }
                        }
                    }

                    listDock = listDockNoIcon


                }

                override fun onPostExecute(result: Unit?) {
                    super.onPostExecute(result)
                    listPageApp.clear()
                    for (l in allListItem) {
                        listPageApp.add(AppFragment(l))
                    }
                    callBack.invoke(listDock, listPageApp)
                }
            }
            task.execute()
        }

    }

    fun getIcon(context: Context, filename: String): Drawable? {
        val bitmap = BitmapFactory.decodeFile(
            dirPath + "/icons/" + filename + ".png"
        )
        return if (bitmap != null) BitmapDrawable(context.resources, bitmap) else null
    }
    val dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()

    fun saveIcon(
        context: Context,
        icon: Bitmap,
        filename: String
    ) {

        val directory = File(dirPath+ "/icons/")
        if (!directory.exists()) directory.mkdir()
        val file = File(directory, filename+".png")
        try {
            file.createNewFile()
            val out = FileOutputStream(file)
            icon.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}