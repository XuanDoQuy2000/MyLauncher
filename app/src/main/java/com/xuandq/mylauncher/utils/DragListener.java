package com.xuandq.mylauncher.utils;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
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
    static boolean canGroup = false;
    static boolean notified = false;
    static boolean fromDock = false;
    long firstEntered = 0L;
    long firstTouchHandleLeft = 0L;
    long firstTouchHandleRight = 0L;
    private volatile static View viewSource = null;
    private final static int LEFT = 69;
    private final static int RIGHT = 96;
    private View temp;


    @Override
    public boolean onDrag(final View v, DragEvent event) {

        if (viewSource == null) viewSource = (View) event.getLocalState();


        if (v.getId() == R.id._app_container || v.getId() == R.id._group_container) {
            View shadow;
            if (v.getId() == R.id._app_container) shadow = v.findViewById(R.id._container_icon);
            else shadow = v.findViewById(R.id._container_group);

            Log.d("ccc", "onDrag action : " + event.getAction());
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("ddd", "onStart item: " + v.getTag());
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d("ccc", "onEnter item: ");
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

                            swapItem(v);
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
                        final RecyclerView rvSource = (RecyclerView) viewSource.getParent();
                        AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
                        sourceAdapter.notifyDataSetChanged();
                        Tool.visibleViews(0, viewSource);

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
                    Log.d("ahandle", "on Enter: ");
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    long time = System.currentTimeMillis() - firstTouchHandleLeft;
                    if (time >= 500) {
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
                                if (!fromDock) {
                                    final RecyclerView rvSource, rvTarget;

                                    rvSource = listFragment.get(cur).getView().findViewById(R.id.recyc_home_apps);
                                    rvTarget = listFragment.get(cur - 1).getView().findViewById(R.id.recyc_home_apps);
                                    AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
                                    AppAdapter targetAdapter = (AppAdapter) rvTarget.getAdapter();
                                    ArrayList<Item> listSource = sourceAdapter.getList();
                                    final ArrayList<Item> listTarget = targetAdapter.getList();

                                    Item itemRemoved = listSource.remove((int) viewSource.getTag());
                                    sourceAdapter.notifyDataSetChanged();

                                    onChangeRecyclerView(cur - 1, cur, listFragment, itemRemoved);
                                }
                            }
                            firstTouchHandleLeft = System.currentTimeMillis() + 500;
                        }
                    }

                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("ahandle", "on Exit: ");
                    return true;
            }
            return true;

    } else if(v.getId()==R.id.handle_right_pager)

    {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                firstTouchHandleRight = System.currentTimeMillis();

                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                long time = System.currentTimeMillis() - firstTouchHandleRight;
                if (time >= 500) {
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


                            if (!fromDock) {
                                RecyclerView rvSource, rvTarget;
                                AppAdapter sourceAdapter, targetAdapter;

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

                        }
                        firstTouchHandleRight = System.currentTimeMillis() + 500;

                    }
                }
                return true;

        }
    } else if(v.getId()==R.id.home_page)

    {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                ViewPager2 viewPager2 = mainActivity.findViewById(R.id.home_page);
                pageSource = viewPager2.getCurrentItem();
                pageTarget = -1;
                notified = false;
                viewSource = (View) event.getLocalState();
                RecyclerView parent = (RecyclerView) viewSource.getParent();
                if (parent.getId() == R.id.home_dock_rv) {
                    fromDock = true;
                } else {
                    fromDock = false;
                }
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                if (!notified) {
                    final RecyclerView rvSource = (RecyclerView) viewSource.getParent();
                    AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
                    sourceAdapter.notifyDataSetChanged();
                    Tool.visibleViews(0, viewSource);
                }
                return true;
        }
    } else if(mainActivity.isShowDialogGroup()&&v.getId()==R.id.bound_dialog)

    {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                mainActivity.hideDialogGroup();
                onOutGroup(v);




                return true;
        }
    } else if(mainActivity.isShowDialogGroup()&&v.getId()==R.id.dialog__group_background)

    {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:

                return true;
        }
    }else if(v.getId()==R.id.home_dock_rv)

    {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DROP:
                RecyclerView rvSource = (RecyclerView) viewSource.getParent();
                RecyclerView rvTarget = (RecyclerView) v;
                AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
                AppAdapter targetAdapter = (AppAdapter) rvTarget.getAdapter();
                ArrayList<Item> listSource = sourceAdapter.getList();
                ArrayList<Item> listTarget = targetAdapter.getList();
                Log.d("ddd", "onDrop: ");

                if (listTarget.size() < 4) {
                    Item item = listSource.remove((int) viewSource.getTag());
                    listTarget.add(listTarget.size(), item);
                    sourceAdapter.notifyDataSetChanged();
                    targetAdapter.notifyDataSetChanged();
                    notified = true;
                }

                return true;
        }
    }else if(v.getId()==R.id.recyc_home_apps)

    {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                return true;
            case DragEvent.ACTION_DROP:
                if (fromDock) {
                    positionSource = (int) viewSource.getTag();
                    ViewPager2 viewPager = mainActivity.findViewById(R.id.home_page);
                    HomePagerAdapter pagerAdapter = (HomePagerAdapter) viewPager.getAdapter();
                    int curPage = viewPager.getCurrentItem();
                    ArrayList<Fragment> listFragment = pagerAdapter.getList();
                    RecyclerView rvSource = (RecyclerView) viewSource.getParent();
                    RecyclerView rvTarget = (RecyclerView) v;
                    AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
                    AppAdapter targetAdapter = (AppAdapter) rvTarget.getAdapter();
                    ArrayList<Item> listSource = sourceAdapter.getList();
                    ArrayList<Item> listTarget = targetAdapter.getList();

                    if (listTarget.size() < AppSetting.pageSize) {
                        Item sourceRemoved = listSource.remove(positionSource);
                        listTarget.add(listTarget.size(), sourceRemoved);
                        sourceAdapter.notifyDataSetChanged();
                        targetAdapter.notifyDataSetChanged();
                    }
                    notified = true;

                }
                return true;
        }
    }

        return true;
}

    private void onOutGroup(View v) {
        int page = (int) v.getTag(R.id.page);
        int posOfGroup = (int) v.getTag(R.id.group_position);
        int posOfItem = (int) viewSource.getTag();
        Log.d("bbb", "onOutGroup: posOfItem" + posOfItem);
        Log.d("bbb", "onOutGroup: posOfGroup" + posOfGroup);

        ViewPager2 viewPager = mainActivity.findViewById(R.id.home_page);
        HomePagerAdapter pagerAdapter = (HomePagerAdapter) viewPager.getAdapter();
        ArrayList<Fragment> listFragment = pagerAdapter.getList();
        RecyclerView rvSource = (RecyclerView) viewSource.getParent();
        RecyclerView rvTarget = listFragment.get(page).getView().findViewById(R.id.recyc_home_apps);
        AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
        AppAdapter targetAdapter = (AppAdapter) rvTarget.getAdapter();
        ArrayList<Item> listSource = sourceAdapter.getList();
        ArrayList<Item> listTarget = targetAdapter.getList();

        Item sourceRemoved = listSource.remove(posOfItem);
        listTarget.get(posOfGroup).getItems().remove(sourceRemoved);
        if (listTarget.get(posOfGroup).getItems().size() == 1) {
            Item item = listTarget.get(posOfGroup).getItems().get(0);
            listSource.clear();
            listTarget.remove(posOfGroup);
            listTarget.add(posOfGroup, item);
        }

        sourceAdapter.notifyDataSetChanged();
        targetAdapter.notifyDataSetChanged();

        pageSource = pageTarget = page;

        onChangeRecyclerView(viewPager.getCurrentItem(), viewPager.getCurrentItem(), listFragment, sourceRemoved);

    }


    private void onChangeRecyclerView(int curPage, int prevPage, ArrayList<Fragment> listFragment, Item sourceRemoved) {
        Item itemRemoved = sourceRemoved;

        final boolean[] first = {true};
        boolean canbreak = false;

        final RecyclerView rvSource, rvTarget;
        AppAdapter sourceAdapter, targetAdapter;

        rvSource = listFragment.get(prevPage).getView().findViewById(R.id.recyc_home_apps);
        rvTarget = listFragment.get(curPage).getView().findViewById(R.id.recyc_home_apps);
        sourceAdapter = (AppAdapter) rvSource.getAdapter();
        targetAdapter = (AppAdapter) rvTarget.getAdapter();

        Log.d("bbb", "onChangeRecyclerView: " + pageSource);
        Log.d("bbb", "onChangeRecyclerView: " + pageTarget);

        if (pageTarget - pageSource >= 2) {
            ArrayList<Item> listSource = sourceAdapter.getList();
            ArrayList<Item> listTarget = targetAdapter.getList();

            if (listTarget.size() == AppSetting.pageSize) {

                Item temp = listTarget.remove(0);
                listTarget.add(0, sourceRemoved);
                targetAdapter.notifyDataSetChanged();
                listSource.add(listSource.size() - 1, temp);
                sourceAdapter.notifyDataSetChanged();

                rvTarget.post(new Runnable() {
                    @Override
                    public void run() {
                        viewSource = rvTarget.findViewWithTag(0);
                        Tool.invisibleViews(0, viewSource);

                    }
                });
            }
            else{
                listTarget.add(0, sourceRemoved);
                targetAdapter.notifyDataSetChanged();

                rvTarget.post(new Runnable() {
                    @Override
                    public void run() {
                        viewSource = rvTarget.findViewWithTag(0);
                        Tool.invisibleViews(0, viewSource);

                    }
                });

            }
            return;
        }

        if (pageSource - pageTarget >= 2) {

            ArrayList<Item> listSource = sourceAdapter.getList();
            ArrayList<Item> listTarget = targetAdapter.getList();

            if (listTarget.size() == AppSetting.pageSize) {

                Item temp = listTarget.remove(0);
                listTarget.add(0, sourceRemoved);
                targetAdapter.notifyDataSetChanged();
                listSource.add(0, temp);
                sourceAdapter.notifyDataSetChanged();

                rvTarget.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            viewSource.cancelDragAndDrop();
                        }
                        viewSource = rvTarget.findViewWithTag(0);
                        Tool.invisibleViews(0, viewSource);

                    }
                });
            } else {
                listTarget.add(0, sourceRemoved);
                targetAdapter.notifyDataSetChanged();

                rvTarget.post(new Runnable() {
                    @Override
                    public void run() {
                        viewSource = rvTarget.findViewWithTag(0);
                        Tool.invisibleViews(0, viewSource);

                    }
                });
            }
            return;
        }

        for (int i = curPage; i < listFragment.size(); i++) {
            final RecyclerView rv = listFragment.get(i).getView().findViewById(R.id.recyc_home_apps);
            AppAdapter adapter = (AppAdapter) rv.getAdapter();
            ArrayList<Item> listItem = adapter.getList();
            if (listItem.size() == AppSetting.INSTANCE.pageSize) {
                if (i != listFragment.size() - 1) {
                    Item temp = listItem.remove(listItem.size() - 1);
                    Log.d("bbb", "onChangeRecyclerView: remove" + temp.getLabel());
                    listItem.add(0, itemRemoved);
                    Log.d("bbb", "onChangeRecyclerView: add" + itemRemoved.getLabel());
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
                rvTarget.post(new Runnable() {
                    @Override
                    public void run() {
                        viewSource = rvTarget.findViewWithTag(0);
                        Tool.invisibleViews(0, viewSource);
                        Log.d("bbb", "onExit: " + viewSource.getTag());
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
            AppAdapter sourceAdapter = (AppAdapter) rvSource.getAdapter();
            AppAdapter targetAdapter = (AppAdapter) rvTarget.getAdapter();
            ArrayList<Item> listItemSource = sourceAdapter.getList();
            ArrayList<Item> listItemTarget = targetAdapter.getList();
            Item itemTarget = listItemTarget.get(positionTarget);
            Item itemSource = listItemSource.get(positionSource);
            if (rvSource == rvTarget) {
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
            } else {
                if (itemSource.getType() == Item.Type.APP &&
                        itemTarget.getType() == Item.Type.APP &&
                        rvSource.getId() != R.id.recyc_dialog) {
                    listItemTarget.remove(positionTarget);

                    Item newGroup = Item.Companion.newGroupItem();
                    newGroup.getItems().add(itemSource);
                    newGroup.getItems().add(itemTarget);
                    listItemTarget.add(positionTarget, newGroup);
                    targetAdapter.notifyDataSetChanged();

                    listItemSource.remove(positionSource);
                    sourceAdapter.notifyDataSetChanged();
                }

                if (itemSource.getType() == Item.Type.APP && itemTarget.getType() == Item.Type.GROUP) {
                    if (itemTarget.getItems().size() < 9) {
                        listItemSource.remove(positionSource);
                        itemTarget.getItems().add(itemSource);
                        sourceAdapter.notifyDataSetChanged();
                        targetAdapter.notifyDataSetChanged();
                    }
                }
            }

            notified = true;

        }
    }

    private void swapItem(View v) {
        Log.d("aaaswap", "swapItem: " + viewSource.getTag());
        Log.d("aaaswap", "swapItem: " + v.getTag());
        final RecyclerView rvSource = (RecyclerView) viewSource.getParent();
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

            }
        } else {
            Log.d("aaa", "swapItem: rvTarget null");
        }
    }
}