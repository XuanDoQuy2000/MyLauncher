package com.xuandq.mylauncher.model

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.xuandq.mylauncher.utils.Definitions
import com.xuandq.mylauncher.utils.Tool
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Item : Parcelable{
    // all items need these values
    var icon: Drawable? = null
    var label: String
    var type: Type? = null
    var id: Int
    var _location: Definitions.ItemPosition? = null
    var x = 0
    var y = 0
    var positionInPage = -1

    // intent for shortcuts and apps
    var intent: Intent? = null

    // list of items for groups
    var items: ArrayList<Item>? = null

    // int value for launcher action
    var actionValue = 0

    // widget specific values
    var widgetValue = 0
    var spanX = 1
    var spanY = 1

    override fun equals(`object`: Any?): Boolean {
        return if (`object` is Item) {
            id == `object`.id
        } else {
            false
        }
    }

    fun reset() {
        val random = Random()
        id = random.nextInt()
    }

    enum class Type {
        APP, GROUP, ACTION, WIDGET
    }



    companion object {
        fun newAppItem(app: App): Item {
            val item = Item()
            item.type = Type.APP
            item.label = app.label
            item.icon = app.icon
            item.intent = Tool.getIntentFromApp(app)
            return item
        }

        fun newGroupItem(): Item {
            val item = Item()
            item.type = Type.GROUP
            item.label = ""
            item.spanX = 1
            item.spanY = 1
            item.items = ArrayList()
            return item
        }

        fun newActionItem(action: Int): Item {
            val item = Item()
            item.type = Type.ACTION
            item.spanX = 1
            item.spanY = 1
            item.actionValue = action
            return item
        }

        fun newWidgetItem(componentName: ComponentName, widgetValue: Int): Item {
            val item = Item()
            item.type = Type.WIDGET
            item.label =
                componentName.packageName + Definitions.DELIMITER.toString() + componentName.className
            item.widgetValue = widgetValue
            item.spanX = 1
            item.spanY = 1
            return item
        }
    }

    init {
        val random = Random()
        id = random.nextInt()
        label = ""
    }
}