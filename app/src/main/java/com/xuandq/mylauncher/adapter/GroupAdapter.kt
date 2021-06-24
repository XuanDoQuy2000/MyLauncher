package com.xuandq.mylauncher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.xuandq.mylauncher.R
import com.xuandq.mylauncher.model.Item
import com.xuandq.mylauncher.utils.Tool

class GroupAdapter (
    val context: Context,
    var listItem : ArrayList<Item>,
    val viewG : View
): RecyclerView.Adapter<GroupAdapter.TinyAppViewHolder>() {

    private var itemClickListener: ((Int) -> Unit)? = null

    fun setItemClickListenner(listener: ((Int) -> Unit)) {
        this.itemClickListener = listener
    }

    inner class TinyAppViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val icon = itemView.findViewById<RoundedImageView>(R.id._app_icon_tiny)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TinyAppViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_tiny_app,parent,false)
        val height = (viewG.measuredHeight - Tool.dp2px(8F)) / 3
        view.layoutParams.height = height
        return TinyAppViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: TinyAppViewHolder, position: Int) {
        val item = listItem[position]
        holder.icon.setImageDrawable(item.icon)
    }
}