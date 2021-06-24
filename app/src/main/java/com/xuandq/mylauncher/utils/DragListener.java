package com.xuandq.mylauncher.utils;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.xuandq.mylauncher.R;
import com.xuandq.mylauncher.activity.MainActivity;
import com.xuandq.mylauncher.adapter.AppAdapter;
import com.xuandq.mylauncher.adapter.HomePagerAdapter;
import com.xuandq.mylauncher.model.App;
import com.xuandq.mylauncher.model.Item;

import java.util.ArrayList;
import java.util.List;

public class DragListener implements View.OnDragListener {

    private MainActivity mainActivity;

    public DragListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    static int positionTarget = -1;
    static int positionSource = 0;
    static int pageSource = -1;
    static int pageTarget = -1;
    boolean canGroup = false;
    long firstEntered = 0L;
    long firstTouchHandleLeft = 0L;
    long firstTouchHandleRight = 0L;
    private volatile static View viewSource = null;
    private final static int LEFT = 69;
    private final static int RIGHT = 96;



    @Override
    public boolean onDrag(View v, DragEvent event) {

        if (viewSource == null) viewSource = (View) event.getLocalState();

        if (v.getId() == R.id._app_container || v.getId() == R.id._group_container) {
            View shadow;
            if (v.getId() == R.id._app_container) shadow = v.findViewById(R.id._container_icon);
            else shadow = v.findViewById(R.id._container_group);
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    viewSource = (View) event.getLocalState();
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
                        if (spaceX < 30) {
                            shadow.setVisibility(View.VISIBLE);
                            canGroup = true;
                        } else if (time >= 300) {
                            swapItem(viewSource, v);
                            shadow.setVisibility(View.INVISIBLE);
                            canGroup = false;
                        } else {
                            canGroup = false;
                        }
                    }
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    shadow.setVisibility(View.INVISIBLE);
                    return true;


                case DragEvent.ACTION_DROP:
                    shadow.setVisibility(View.INVISIBLE);
                    if (canGroup) {
                        onGroup(viewSource, v);
                    }
                    return true;
            }

        } else if (v.getId() == R.id.handle_left_pager) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    firstTouchHandleLeft = System.currentTimeMillis();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    long time = System.currentTimeMillis() - firstTouchHandleLeft;
                    if (time >= 800) {
                        View parent = (View) v.getParent();
                        Log.d("ahandle", "onDrag: " + parent.getId());
                        if (parent != null) {
                            ViewPager2 pager = parent.findViewById(R.id.home_page);
                            int cur = pager.getCurrentItem();
                            if (cur > 0) {
                                HomePagerAdapter adapter = (HomePagerAdapter) pager.getAdapter();
                                ArrayList<Fragment> listFragment = adapter.getList();

                                pager.setCurrentItem(cur - 1, true);

                                pageTarget = cur - 1;

//                                onChangePage(cur, listFragment, LEFT);

                                final RecyclerView rvSource, rvTarget;

                                rvSource = listFragment.get(cur).getView().findViewById(R.id.recyc_home_apps);
                                rvTarget = listFragment.get(cur - 1).getView().findViewById(R.id.recyc_home_apps);
                                AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
                                AppAdapter targetAdapter = (AppAdapter) rvTarget.getAdapter();
                                ArrayList<Item> listSource = sourceAdapter.getList();
                                final ArrayList<Item> listTarget = targetAdapter.getList();

                                Item itemRemoved = listSource.remove((int) viewSource.getTag());

                                onChangeRecyclerView(cur - 1 , cur , listFragment, itemRemoved);
                            }
                            firstTouchHandleLeft = System.currentTimeMillis()+200;
                        }
                    }
                    return true;

            }
        } else if (v.getId() == R.id.handle_right_pager) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    firstTouchHandleRight = System.currentTimeMillis();
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    long time = System.currentTimeMillis() - firstTouchHandleRight;
                    if (time >= 800) {
                        View parent = (View) v.getParent();
                        Log.d("ahandle", "onDrag: " + parent.getId());
                        if (parent != null) {
                            ViewPager2 pager = parent.findViewById(R.id.home_page);
                            int cur = pager.getCurrentItem();
                            if (cur < pager.getAdapter().getItemCount() - 1) {
                                HomePagerAdapter adapter = (HomePagerAdapter) pager.getAdapter();
                                ArrayList<Fragment> listFragment = adapter.getList();

                                pager.setCurrentItem(cur + 1, true);

                                pageTarget = cur + 1;

//                                onChangePage(cur, listFragment, RIGHT);


                                RecyclerView rvSource, rvTarget;
                                AppAdapter sourceAdapter,targetAdapter;

                                rvSource = listFragment.get(cur).getView().findViewById(R.id.recyc_home_apps);
                                rvTarget = listFragment.get(cur + 1).getView().findViewById(R.id.recyc_home_apps);
                                sourceAdapter = (AppAdapter) rvSource.getAdapter();
                                targetAdapter = (AppAdapter) rvTarget.getAdapter();
                                ArrayList<Item> listSource = sourceAdapter.getList();
                                ArrayList<Item> listTarget = targetAdapter.getList();

                                positionSource = (int) viewSource.getTag();

                                Item itemRemoved = listSource.remove(positionSource);
                                sourceAdapter.notifyDataSetChanged();

                                onChangeRecyclerView(cur + 1, cur, listFragment, itemRemoved);

                            }
                            firstTouchHandleRight = System.currentTimeMillis()+200;
                        }
                    }
                    return true;

            }
        } else if (v.getId() == R.id.home_page) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    ViewPager2 viewPager2 = mainActivity.findViewById(R.id.home_page);
                    pageSource = viewPager2.getCurrentItem();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    Tool.visibleViews(200, viewSource);
                    return true;
            }
        } else if (v.getId() == R.id.bound_dialog) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    View parentView = (View) v.getParent();
                    parentView.setVisibility(View.INVISIBLE);

//                    onOutGroup(viewSource, v);

                    return true;
            }
        }

        return false;
    }

    private void onOutGroup(View viewSource, View v) {
        int page = (int) v.getTag(R.id.page);
        int posOfGroup = (int) v.getTag(R.id.group_position);
        int posOfItem = (int) viewSource.getTag();

        ViewPager2 viewPager = mainActivity.findViewById(R.id.home_page);
        HomePagerAdapter pagerAdapter = (HomePagerAdapter) viewPager.getAdapter();
        List<Fragment> listFragment = pagerAdapter.getList();
        RecyclerView rvSource = (RecyclerView) viewSource.getParent();
        RecyclerView rvTarget = listFragment.get(page).getView().findViewById(R.id.recyc_home_apps);
        AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
        AppAdapter targetAdapter = (AppAdapter) rvTarget.getAdapter();
        ArrayList<Item> listSource = sourceAdapter.getList();
        ArrayList<Item> listTarget = targetAdapter.getList();


    }

    private void onChangePage(int cur, ArrayList<Fragment> listFragment, int direction) {
        final RecyclerView rvSource, rvTarget;
        int nextCur = -1;

        if (direction == LEFT) {
            nextCur = cur - 1;
            rvSource = listFragment.get(cur).getView().findViewById(R.id.recyc_home_apps);
            rvTarget = listFragment.get(cur - 1).getView().findViewById(R.id.recyc_home_apps);
        } else {
            nextCur = cur + 1;
            rvSource = listFragment.get(cur).getView().findViewById(R.id.recyc_home_apps);
            rvTarget = listFragment.get(cur + 1).getView().findViewById(R.id.recyc_home_apps);
        }
        AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
        AppAdapter targetAdapter = (AppAdapter) rvTarget.getAdapter();
        ArrayList<Item> listSource = sourceAdapter.getList();
        final ArrayList<Item> listTarget = targetAdapter.getList();

        int posSource = (int) viewSource.getTag();

        if (listTarget.size() < 20) {
            Item itemRemoved = listSource.remove(posSource);
            sourceAdapter.notifyDataSetChanged();
            listTarget.add(listTarget.size(), itemRemoved);
            targetAdapter.notifyDataSetChanged();
            rvTarget.post(new Runnable() {
                @Override
                public void run() {
                    viewSource = rvTarget.findViewWithTag(listTarget.size() - 1);
                    Tool.invisibleViews(0, viewSource);
                }
            });
        } else {
            Item targetRemoved = listTarget.remove(listTarget.size() - 1);
            Item sourceRemoved = listSource.remove(posSource);
            listSource.add(posSource, targetRemoved);
            sourceAdapter.notifyDataSetChanged();
            listTarget.add(listTarget.size(), sourceRemoved);
            targetAdapter.notifyDataSetChanged();
            rvTarget.post(new Runnable() {
                @Override
                public void run() {
                    viewSource = rvTarget.findViewWithTag(listTarget.size() - 1);
                    Tool.invisibleViews(0, viewSource);
                }
            });
        }
    }

    private void onChangeRecyclerView(int curPage, int prevPage, ArrayList<Fragment> listFragment, Item sourceRemoved) {
        Item itemRemoved = sourceRemoved;

        final boolean[] first = {true};
        boolean canbreak = false;

        final RecyclerView rvSource, rvTarget;
        AppAdapter sourceAdapter,targetAdapter;

        rvSource = listFragment.get(prevPage).getView().findViewById(R.id.recyc_home_apps);
        rvTarget = listFragment.get(curPage).getView().findViewById(R.id.recyc_home_apps);
        sourceAdapter = (AppAdapter) rvSource.getAdapter();
        targetAdapter = (AppAdapter) rvTarget.getAdapter();

        if (pageTarget - pageSource >= 2){
            ArrayList<Item> listSource = sourceAdapter.getList();
            ArrayList<Item> listTarget = targetAdapter.getList();

            Item temp = listTarget.remove(0);
            listTarget.add(0,sourceRemoved);
            targetAdapter.notifyDataSetChanged();
            listSource.add(listSource.size() - 1,temp);
            sourceAdapter.notifyDataSetChanged();

            rvTarget.post(new Runnable() {
                @Override
                public void run() {
                    viewSource = rvTarget.findViewWithTag(0);
                    Tool.invisibleViews(0, viewSource);

                }
            });
            return;
        }

        if (pageSource - pageTarget >= 2){
            return;
        }

        for (int i = curPage; i < listFragment.size(); i++) {
            final RecyclerView rv = listFragment.get(i).getView().findViewById(R.id.recyc_home_apps);
            AppAdapter adapter = (AppAdapter) rv.getAdapter();
            ArrayList<Item> listItem = adapter.getList();
            if (listItem.size() == 20) {
                if (i != listFragment.size() - 1) {
                    Item temp = listItem.remove(listItem.size() - 1);
                    listItem.add(0, itemRemoved);
                    itemRemoved = temp;
                    adapter.notifyDataSetChanged();
                } else {

                }
            } else {
                listItem.add(0, itemRemoved);
                adapter.notifyDataSetChanged();
                canbreak = true;
            }
            if (first[0]) {
                first[0] = false;
                rv.post(new Runnable() {
                    @Override
                    public void run() {
                        viewSource = rv.findViewWithTag(0);
                        Tool.invisibleViews(0, viewSource);

                    }
                });

            }
            if (canbreak) break;
        }
    }


    private void onGroup(View viewSource, View v) {
        RecyclerView rvSource = (RecyclerView) viewSource.getParent();
        RecyclerView rvTarget = (RecyclerView) v.getParent();
        positionSource = (int) viewSource.getTag();
        positionTarget = (int) v.getTag();

        if (rvTarget != null) {
            if (rvSource == rvTarget) {
                AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
                List<Item> listItemSource = sourceAdapter.getList();
                Item itemTarget = listItemSource.get(positionTarget);
                Item itemSource = listItemSource.get(positionSource);

                if (itemSource.getType() == Item.Type.APP &&
                        itemTarget.getType() == Item.Type.APP &&
                        rvSource.getId() != R.id.recyc_dialog) {
                    listItemSource.remove(positionTarget);
                    Item newGroup = Item.Companion.newGroupItem();
                    newGroup.getItems().add(itemSource);
                    newGroup.getItems().add(itemTarget);
                    listItemSource.add(positionTarget, newGroup);
                    listItemSource.remove(positionSource);
                    sourceAdapter.notifyDataSetChanged();
                }

                if (itemSource.getType() == Item.Type.APP && itemTarget.getType() == Item.Type.GROUP) {
                    if (itemTarget.getItems().size() < 9) {
                        listItemSource.remove(positionSource);
                        itemTarget.getItems().add(itemSource);
                        sourceAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void swapItem(View viewSource, View v) {
        Log.d("aaaleft", "swapItem: " + viewSource.getTag());
        Log.d("aaaleft", "swapItem: " + v.getTag());
        RecyclerView rvSource = (RecyclerView) viewSource.getParent();
        RecyclerView rvTarget = (RecyclerView) v.getParent();
        positionSource = (int) viewSource.getTag();
        positionTarget = (int) v.getTag();

        if (rvTarget != null) {
            if (rvSource == rvTarget) {
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
            } else {
                Log.d("aaa", "swapItem: rvSource != rvTarget");
            }
        } else {
            Log.d("aaa", "swapItem: rvTarget null");
        }
    }


}