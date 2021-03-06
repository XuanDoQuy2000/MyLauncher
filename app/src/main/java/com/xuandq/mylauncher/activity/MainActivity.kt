package com.xuandq.mylauncher.activity

import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.xuandq.mylauncher.R
import com.xuandq.mylauncher.adapter.AppAdapter
import com.xuandq.mylauncher.adapter.HomePagerAdapter
import com.xuandq.mylauncher.fragment.AppFragment
//import com.xuandq.mylauncher.fragment.GroupFragment
import com.xuandq.mylauncher.model.Item
import com.xuandq.mylauncher.utils.AppRepository
import com.xuandq.mylauncher.utils.AppSetting
import com.xuandq.mylauncher.utils.DragListener
import com.xuandq.mylauncher.utils.Tool
import com.xuandq.mylauncher.viewmodel.AppViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.fragment_group.*
import java.io.File

class MainActivity : AppCompatActivity() {


    val TAG = "homeactivity"
    private lateinit var appViewModel: AppViewModel

//    var groupFragment : GroupFragment? =null

    private lateinit var homeAdapter: HomePagerAdapter
    private var allListItem = ArrayList<ArrayList<Item>>()
    private var listPageApp = ArrayList<Fragment>()
    private lateinit var dockAdapter: AppAdapter
    private var listDock = ArrayList<Item>()
    private var currentPage = 0

    var inGroup = false

    private lateinit var groupAppAdapter : AppAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppSetting.init(this)

        initView()

        showLoadingDialog()
        AppRepository.getInstance().getAllListItem(this){listDock,listPage ->
            dockAdapter.updateData(listDock)
            homeAdapter.updateListTabs(listPage)
            home_indicator.count = listPage.size
            dialog_loading.visibility = View.GONE
        }


    }


    private fun initView() {

        initViewPager()
        initDock()
//        setUpObserver()

        handle_left_pager.setOnDragListener(DragListener(this))
        handle_right_pager.setOnDragListener(DragListener(this))
        home_page.setOnDragListener(DragListener(this))
        dialog_group.setOnDragListener(DragListener(this))
        home.setOnDragListener(DragListener(this))
        home_dock_rv.setOnDragListener(DragListener(this))
    }

    private fun initDialog(itemGroup : Item, page: Int, groupPos : Int) {

        val recyc_dialog = dialog_group.findViewById<RecyclerView>(R.id.recyc_dialog)
        val bound_dialog = dialog_group.findViewById<ConstraintLayout>(R.id.bound_dialog)
        val dialog_background = dialog_group.findViewById<ConstraintLayout>(R.id.dialog__group_background)

        val gridManager = GridLayoutManager(this,3)
        recyc_dialog.layoutManager = gridManager
        groupAppAdapter = AppAdapter(this, itemGroup?.items!!, false, 3, recyc_dialog.measuredHeight)
        recyc_dialog.adapter = groupAppAdapter

        groupAppAdapter.setItemClickListenner {
            val intent = Tool.getIntentFromString(itemGroup?.items!!.get(it).intent)
            startActivity(intent)
        }

        bound_dialog.setTag(R.id.page, page)
        bound_dialog.setTag(R.id.group_position, groupPos)
        bound_dialog.setOnDragListener(DragListener(this))
        dialog_group.setOnClickListener {
            hideDialogGroup()
        }
    }


    private fun initDock() {


        val wallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable = wallpaperManager.drawable
        val bitmap = wallpaperDrawable.toBitmap()

        val grid = GridLayoutManager(this, 4)
        home_dock_rv.layoutManager = grid
        dockAdapter = AppAdapter(this, listDock, true, parentHeight = 0)
        home_dock_rv.adapter = dockAdapter
        dockAdapter.setItemClickListenner {
            val intent = Tool.getIntentFromString(dockAdapter.list[it].intent)
            startActivity(intent)
        }


        home_dock.viewTreeObserver.addOnGlobalLayoutListener {
            val bitmap = Tool.createBackGroundView(this,bitmap,home_dock)
            var d : Drawable? = null
            if (bitmap != null) {
                d = RoundedBitmapDrawableFactory.create(resources, bitmap)
                (d as RoundedBitmapDrawable).cornerRadius =
                    resources.getDimensionPixelOffset(R.dimen._24sdp).toFloat()
            }
            home_dock.background = d
        }
    }

    private fun setUpObserver() {
        appViewModel = ViewModelProvider(this).get(AppViewModel::class.java)

        appViewModel.fetchListApps(this)

        appViewModel.listApps.observe(this, Observer {
            allListItem.clear()
            var k = 0
            val listTemp = ArrayList<Item>()
            Log.d(TAG, "setUpObserver: ${it.size}")
            while (k < it.size) {
                listTemp.add(Item.newAppItem(it[k]))
                k++
                if (listTemp.size == 20 || k == it.size) {
                    allListItem.add(ArrayList(listTemp))
                    listTemp.clear()
                }
            }
            listPageApp.clear()
            for (l in allListItem) {
                listPageApp.add(AppFragment(l))
            }

            listDock.add(allListItem[0][1])
            listDock.add(allListItem[0][2])
            listDock.add(allListItem[1][1])
            listDock.add(allListItem[1][2])

            appViewModel.allListItem.value = allListItem
            appViewModel.listPage.value = listPageApp

            homeAdapter.updateListTabs(listPageApp)
            dockAdapter.updateData(listDock)

        })
    }

    private fun initViewPager() {
        homeAdapter = HomePagerAdapter(this, listPageApp)
        home_page.adapter = homeAdapter
        home_page.offscreenPageLimit = 5
        home_page.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        home_page.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                home_indicator.setSelected(position)
                currentPage = position

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                val xOffset = (position + positionOffset) / (listPageApp.size - 1)
                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                wallpaperManager.setWallpaperOffsets(home_page.windowToken, xOffset, 0.0f)
            }

        })
    }

    fun showDialogGroup(group : Item, itemPos : Int){
//        dialog_group.removeAllViews()
//        groupFragment = GroupFragment.newInstance(group,home_page.currentItem, itemPos)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.dialog_group, groupFragment!!)
//            .commit()
//        dialog_group.visibility = View.VISIBLE
        initDialog(group , home_page.currentItem, itemPos)
        dialog_group.visibility = View.VISIBLE
    }

    fun isShowDialogGroup() = dialog_group.visibility == View.VISIBLE

    fun visibleDialog(){
        dialog_group.visibility = View.VISIBLE
    }

    fun hideDialogGroup(){
        dialog_group.visibility = View.GONE
    }


    override fun onBackPressed() {
        if (dialog_group.isShown){
            hideDialogGroup()
        }else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        listPageApp = homeAdapter.list
        allListItem.clear()
        listPageApp.forEach {
            allListItem.add((it as AppFragment).appAdapter.list)
        }
        AppRepository.getInstance().saveAllListItem(this,allListItem)
        AppRepository.getInstance().saveListDock(this, dockAdapter.list)
        super.onStop()
    }

    fun showLoadingDialog(){
        dialog_loading.visibility = View.VISIBLE
        pb_main_loading.max = 100
        pb_main_loading.progress = 80
        pb_main_loading.isIndeterminate =true
    }


}