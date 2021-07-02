package com.xuandq.mylauncher.model

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Parcelable

import com.xuandq.mylauncher.utils.Definitions
import com.xuandq.mylauncher.utils.Tool
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
class Item : Parcelable, Serializable{
    // all items need these values
    var icon: Drawable? = null

    var label: String
    var type: Type? = null

    var id: Int

    var _location: Definitions.ItemPosition? = null

    var positionInPage = -1
    var packageName : String = ""
    var iconPath : String = ""

    // intent for shortcuts and apps
    var intent: String = ""

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

    fun copy() : Item{
        val item = Item()
        item.id = this.id
        item.label = this.label
        item.intent = this.intent
        item.items = this.items
        item.packageName = this.packageName
        item.type = this.type
        item.spanX = this.spanX
        item.spanY = this.spanX
        item.iconPath = this.iconPath
        item.widgetValue = this.widgetValue
        item.actionValue = this.actionValue
        return item
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
            item.intent = Tool.getIntentAsString(Tool.getIntentFromApp(app))
            item.packageName = app.packageName
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