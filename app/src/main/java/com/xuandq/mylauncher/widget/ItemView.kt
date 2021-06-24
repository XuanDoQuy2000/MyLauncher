package com.xuandq.mylauncher.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

class ItemView(context: Context , attributeSet: AttributeSet) : ConstraintLayout(context,attributeSet) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
}