package com.xuandq.mylauncher.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.xuandq.mylauncher.R
import com.xuandq.mylauncher.activity.MainActivity
import com.xuandq.mylauncher.adapter.AppAdapter
import com.xuandq.mylauncher.model.Item
import com.xuandq.mylauncher.utils.AppSetting
import com.xuandq.mylauncher.utils.DragListener
import com.xuandq.mylauncher.utils.Tool
import kotlinx.android.synthetic.main.fragment_app.*
import java.lang.RuntimeException


class AppFragment(var listItem : ArrayList<Item>) : Fragment() {

    lateinit var appAdapter: AppAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    private fun initView() {
        val grid = GridLayoutManager(requireActivity(), 4)
        recyc_home_apps.layoutManager = grid
        appAdapter = AppAdapter(
            requireActivity() as MainActivity,
            listItem ,
            false, AppSetting.numRow,
            recyc_home_apps.measuredHeight
        )
        recyc_home_apps.adapter = appAdapter
        recyc_home_apps.itemAnimator?.changeDuration = 1000
        recyc_home_apps.setOnDragListener(DragListener(requireActivity() as MainActivity))
        recyc_home_apps.itemAnimator = AppAdapter.AnimatorX(appAdapter)
        Log.d("aaa", "initView: " + listItem.size)
        appAdapter.setItemClickListenner {
            if (listItem[it].type == Item.Type.APP) {
                val intentLauncher = Tool.getIntentFromString(listItem[it].intent)
                startActivity(intentLauncher)
            }else{
                val mainActivity = requireActivity() as MainActivity
                mainActivity.showDialogGroup(listItem[it], it)
            }
        }
    }

    fun setListItem(list : List<Item>){
        listItem = list as ArrayList<Item>
    }






}