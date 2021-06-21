package com.xuandq.mylauncher.adapter

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.xuandq.mylauncher.R
import com.xuandq.mylauncher.model.Item
import com.xuandq.mylauncher.utils.DragListener
import java.util.*
import kotlin.collections.ArrayList

class AppAdapter(val context: Context, var list : ArrayList<Item>, val isDock: Boolean = false)
    : RecyclerView.Adapter<AppAdapter.AppViewHolder>(){


    private var itemClickListener : ((Int) -> Unit)? = null

    fun setItemClickListenner(listener : ((Int) -> Unit)){
        this.itemClickListener = listener
    }

    inner class AppViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val icon = itemView.findViewById<ImageView>(R.id._app_icon)
        val label = itemView.findViewById<TextView>(R.id._app_label)
        val container = itemView.findViewById<ConstraintLayout>(R.id._app_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_app,parent,false)

        if (!isDock) {
            val height = parent.measuredHeight / 5
            view.layoutParams.height = height
        }

        return AppViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }



    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val item = list[position]

//        for (temp in list) {
//            Log.d("aaalauncher", "onBindViewHolder: " + temp.label)
//            Log.w("aaalauncher", "onBindViewHolder: " + holder.container.tag)
//        }

//        Log.w("aaalauncher", "--------------------------------------: " )
        if (item.type == Item.Type.APP) {
            holder.icon.setImageDrawable(item.icon)
        } else {
            holder.icon.setImageResource(R.mipmap.ic_launcher)
        }
        holder.container.setOnClickListener {
            itemClickListener?.let { it.invoke(position) }
        }
        if (!isDock) {
            holder.label.setText(item.label)
        } else {
            holder.label.visibility = View.GONE
        }

        holder.container.setTag(position)
        holder.setIsRecyclable(false)
        holder.container.setOnDragListener(DragListener())
//        Log.d("aaalauncher", "onBindViewHolder: "+ holder.container.getTag())

        holder.container.setOnLongClickListener {
            it.visibility = View.INVISIBLE
            val data = ClipData.newPlainText("","")
            val shadowBuilder = View.DragShadowBuilder(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(data, shadowBuilder, it, 0)
            } else {
                it.startDrag(data, shadowBuilder, it, 0)
            }
        }
    }

    fun logItem() {
        for (temp in list) {
            Log.d("anhhct", "logItem: " + temp.label)
        }
        Log.w("anhhct", "-----------------------------------------")
    }

    fun updateData(newList : List<Item>){
        list = newList as ArrayList<Item>
        notifyDataSetChanged()
    }

    fun swapItem(src : Int, des : Int){
        Collections.swap(list,src,des)
        notifyItemMoved(src,des)
    }


    fun removeItem(pos : Int){
        list.removeAt(pos)
        notifyItemRemoved(pos)
    }

    fun insertItem(pos : Int, item : Item){
        list.add(pos, item)
        notifyItemInserted(pos)
    }


}