package com.xuandq.mylauncher.utils;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.xuandq.mylauncher.R;
import com.xuandq.mylauncher.adapter.AppAdapter;
import com.xuandq.mylauncher.adapter.HomePagerAdapter;
import com.xuandq.mylauncher.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragListener implements View.OnDragListener {


    int positionTarget = -1;
    int positionSource = -1;
    boolean canGroup = false;
    long firstEntered = 0L;
    long firstTouchHandleLeft = 0L;
    long firstTouchHandleRight = 0L;

    
    @Override
    public boolean onDrag(View v, DragEvent event) {
        Log.d("aaa", "onDrag: ");
        View viewSource = (View) event.getLocalState();

        if (v.getId() == R.id._app_container) {
            View shadow = v.findViewById(R.id._container_icon);
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    if (v.getTag() != viewSource.getTag()) {
                        firstEntered = System.currentTimeMillis();
                    }
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    if (v.getTag() != viewSource.getTag()) {
                        long time = System.currentTimeMillis() - firstEntered;
                        int spaceX = Math.abs(v.getWidth() / 2 - Math.round(event.getX()));
                        if (spaceX < 30){
                            shadow.setVisibility(View.VISIBLE);
                            canGroup = true;
                        }else if (time >= 300) {
                            swapItem(viewSource, v);
                            shadow.setVisibility(View.INVISIBLE);
                            canGroup = false;
                        }else {
                            canGroup = false;
                        }
                    }
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    shadow.setVisibility(View.INVISIBLE);
                    return true;


                case DragEvent.ACTION_DROP:
                    shadow.setVisibility(View.INVISIBLE);
                    if (canGroup){
                        onGroup(viewSource,v);
                    }
                    return true;


            }
        }else if (v.getId() == R.id.handle_left_pager){
            switch (event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    firstTouchHandleLeft = System.currentTimeMillis();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    long time = System.currentTimeMillis() - firstTouchHandleLeft;
                    if (time >= 500){
                        View parent = (View) v.getParent();
                        Log.d("ahandle", "onDrag: " + parent.getId());
                        if (parent != null){
                            ViewPager2 pager = parent.findViewById(R.id.home_page);
                            int cur = pager.getCurrentItem();
                            if (cur > 0){
                                HomePagerAdapter adapter = (HomePagerAdapter) pager.getAdapter();
                                ArrayList<Fragment> listFragment = adapter.getList();

                                pager.setCurrentItem(cur - 1);

                                RecyclerView rvSource = listFragment.get(cur).getView().findViewById(R.id.recyc_home_apps);
                                RecyclerView rvTarget = listFragment.get(cur - 1).getView().findViewById(R.id.recyc_home_apps);
                                AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
                                AppAdapter targetAdapter = (AppAdapter) rvTarget.getAdapter();
                                ArrayList<Item> listSource = sourceAdapter.getList();
                                ArrayList<Item> listTarget = targetAdapter.getList();

                                if (listTarget.size() < 20){
                                    Item itemRemoved = listSource.remove((int)viewSource.getTag());
                                    sourceAdapter.notifyDataSetChanged();
                                    listTarget.add(listTarget.size(),itemRemoved);
                                    targetAdapter.notifyDataSetChanged();
                                }else {
                                    int posSource = (int) viewSource.getTag();
                                    Item targetRemoved = listTarget.remove(listTarget.size()-1);
                                    Item sourceRemoved = listSource.remove(posSource);
                                    listTarget.add(listTarget.size(),sourceRemoved);
                                    targetAdapter.notifyDataSetChanged();
                                    listSource.add(posSource,targetRemoved);
                                    sourceAdapter.notifyDataSetChanged();
                                    viewSource = rvTarget.findViewWithTag(listTarget.size() - 1);
                                    Log.d("aaaleft", "onPageNextLeft: " + viewSource.getTag());
                                    Tool.invisibleViews(200,viewSource);
                                }
                            }
                            firstTouchHandleLeft = System.currentTimeMillis();
                        }
                    }
                    return true;


            }
        }else if (v.getId() == R.id.handle_right_pager){
            switch (event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    firstTouchHandleRight = System.currentTimeMillis();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    long time = System.currentTimeMillis() - firstTouchHandleRight;
                    if (time >= 500){
                        View parent = (View) v.getParent();
                        Log.d("ahandle", "onDrag: " + parent.getId());
                        if (parent != null){
                            ViewPager2 pager = parent.findViewById(R.id.home_page);
                            int cur = pager.getCurrentItem();
                            if (cur < pager.getAdapter().getItemCount()){
                                pager.setCurrentItem(cur + 1);
                            }
                            firstTouchHandleRight = System.currentTimeMillis();
                        }
                    }
                    return true;

            }
        }else if (v.getId() == R.id.home_page){
            switch (event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    Tool.visibleViews(200,viewSource);
                    return true;
            }
        }


        return false;
    }

    private void onGroup(View viewSource, View v) {
        RecyclerView rvSource = (RecyclerView) viewSource.getParent();
        RecyclerView rvTarget = (RecyclerView) v.getParent();
        positionSource = (int) viewSource.getTag();
        positionTarget = (int) v.getTag();

        if (rvTarget != null){
            if (rvSource == rvTarget){
                AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
                List<Item> listItemSource = sourceAdapter.getList();
                Item itemTarget = listItemSource.get(positionTarget);
                Item itemSource = listItemSource.get(positionSource);
                GridLayoutManager g = (GridLayoutManager) rvSource.getLayoutManager();
                int start = g.findFirstVisibleItemPosition();
                int end = g.findLastVisibleItemPosition();
                for (int i=start;i<=end;i++){
                    View temp = g.findViewByPosition(i);
                    Log.d("aaa", "onGroup: " + temp.getTag());
                }

                if (itemSource.getType() == Item.Type.APP && itemTarget.getType() == Item.Type.APP) {
                    listItemSource.remove(positionTarget);
                    Item newGroup = Item.Companion.newGroupItem();
                    newGroup.getItems().add(itemSource);
                    newGroup.getItems().add(itemTarget);
                    listItemSource.add(positionTarget,newGroup);
                    listItemSource.remove(positionSource);
                    sourceAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void swapItem(View viewSource, View v) {
        RecyclerView rvSource = (RecyclerView) viewSource.getParent();
        RecyclerView rvTarget = (RecyclerView) v.getParent();
        positionSource = (int) viewSource.getTag();
        positionTarget = (int) v.getTag();

        if (rvTarget != null){
            if (rvSource == rvTarget){
                AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();

                if (positionTarget > positionSource) {
                    for (int i = positionSource; i < positionTarget; i++) {
                        sourceAdapter.swapItem(i, i + 1);
                        View view1 = rvSource.findViewWithTag(i);
                        View view2 = rvSource.findViewWithTag(i + 1);
                        view1.setTag(i + 1);
                        view2.setTag(i);
                    }
                } else {
                    for (int i = positionSource; i > positionTarget; i--) {
                        sourceAdapter.swapItem(i, i - 1);
                        View view1 = rvSource.findViewWithTag(i);
                        View view2 = rvSource.findViewWithTag(i - 1);
                        view1.setTag(i - 1);
                        view2.setTag(i);
                    }
                }
            }else {
                Log.d("aaa", "swapItem: rvSource != rvTarget");
            }
        }else{
            Log.d("aaa", "swapItem: rvTarget null");
        }
    }


}