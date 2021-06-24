package com.xuandq.mylauncher.activity

import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.xuandq.mylauncher.fragment.GroupFragment
import com.xuandq.mylauncher.model.Item
import com.xuandq.mylauncher.utils.DragListener
import com.xuandq.mylauncher.utils.Tool
import com.xuandq.mylauncher.viewmodel.AppViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    val TAG = "homeactivity"
    private lateinit var appViewModel: AppViewModel

    var groupFragment : GroupFragment? =null

    private lateinit var homeAdapter: HomePagerAdapter
    private var allListItem = ArrayList<ArrayList<Item>>()
    private var listPageApp = ArrayList<Fragment>()
    private lateinit var dockAdapter: AppAdapter
    private var listDock = ArrayList<Item>()
    private var currentPage = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {

        initViewPager()
        initDock()

        handle_left_pager.setOnDragListener(DragListener(this))
        handle_right_pager.setOnDragListener(DragListener(this))
        home_page.setOnDragListener(DragListener(this))

        setUpObserver()
    }


    private fun initDock() {

        val wallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable = wallpaperManager.drawable
        val bitmap = wallpaperDrawable.toBitmap()

        val grid = GridLayoutManager(this, 4)
        home_dock_rv.layoutManager = grid
        dockAdapter = AppAdapter(this, listDock, true)
        home_dock_rv.adapter = dockAdapter


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
        dialog_group.removeAllViews()
        val groupFragment = GroupFragment.newInstance(group,home_page.currentItem, itemPos)
        supportFragmentManager.beginTransaction()
            .replace(R.id.dialog_group,groupFragment)
            .commit()
        dialog_group.visibility = View.VISIBLE
    }

    fun isShowDialogGroup() = dialog_group.visibility == View.VISIBLE

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

}