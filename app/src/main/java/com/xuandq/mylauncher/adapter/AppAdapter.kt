package com.xuandq.mylauncher.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xuandq.mylauncher.R
import com.xuandq.mylauncher.activity.MainActivity
import com.xuandq.mylauncher.model.Item
import com.xuandq.mylauncher.utils.DragListener
import java.util.*
import kotlin.collections.ArrayList

class AppAdapter(
    val context: Context,
    var list: ArrayList<Item>,
    val isDock: Boolean = false,
    val numRow: Int = 5
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val APP = 456
        const val GROUP = 789
    }

    private var itemClickListener: ((Int) -> Unit)? = null

    fun setItemClickListenner(listener: ((Int) -> Unit)) {
        this.itemClickListener = listener
    }

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon = itemView.findViewById<ImageView>(R.id._app_icon)
        val label = itemView.findViewById<TextView>(R.id._app_label)
        val container = itemView.findViewById<ConstraintLayout>(R.id._app_container)
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconsGroup = itemView.findViewById<RecyclerView>(R.id._group_icon)
        val labelGroup = itemView.findViewById<TextView>(R.id._group_label)
    }

    override fun getItemViewType(position: Int): Int {
        when (list[position].type) {
            Item.Type.APP -> return APP
            Item.Type.GROUP -> return GROUP
            else -> return APP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            APP -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_app, parent, false)
                if (!isDock) {
                    val height = parent.measuredHeight / numRow
                    view.layoutParams.height = height
                }

                return AppViewHolder(view)
            }
            GROUP -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_group, parent, false)
                if (!isDock) {
                    val height = parent.measuredHeight / numRow
                    view.layoutParams.height = height
                }
                return GroupViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_app, parent, false)
                if (!isDock) {
                    val height = parent.measuredHeight / numRow
                    view.layoutParams.height = height
                }

                return AppViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = list[position]
        var myHolder: RecyclerView.ViewHolder? = null
        if (holder.itemViewType == APP) {
            myHolder = holder as AppViewHolder
            myHolder.icon.setImageDrawable(item.icon)
            if (!isDock) {
                myHolder.label.setText(item.label)
            } else {
                myHolder.label.visibility = View.GONE
            }

        } else if (holder.itemViewType == GROUP) {
            myHolder = holder as GroupViewHolder
            val groupAdapter = item.items?.let {
                GroupAdapter(context, it, myHolder.iconsGroup)}
            val gridManager = GridLayoutManager(context,3)
            myHolder.iconsGroup.layoutManager = gridManager
            myHolder.iconsGroup.adapter = groupAdapter
            myHolder.iconsGroup.isClickable = false
            myHolder.iconsGroup.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return true
                }
            })

            if (!isDock) {
                myHolder.labelGroup.setText(item.label)
            } else {
                myHolder.labelGroup.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            itemClickListener?.let { it.invoke(position) }
        }

        holder.itemView.setTag(position)
        holder.setIsRecyclable(false)
        holder.itemView.setOnDragListener(DragListener(context as MainActivity))


        holder.itemView.setOnLongClickListener {

            it.visibility = View.INVISIBLE
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(data, shadowBuilder, it, 0)
            } else {
                it.startDrag(data, shadowBuilder, it, 0)
            }
        }
    }


    fun updateData(newList: List<Item>) {
        list = newList as ArrayList<Item>
        notifyDataSetChanged()
    }

    fun swapItem(src: Int, des: Int) {
        Collections.swap(list, src, des)
        notifyItemMoved(src, des)
    }

    class AnimatorX(val adapter: AppAdapter) : DefaultItemAnimator(){
//        override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
//            adapter.notifyDataSetChanged()
//            super.onAnimationFinished(viewHolder)
//        }
    }




}