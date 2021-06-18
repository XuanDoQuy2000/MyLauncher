package com.xuandq.mylauncher.utils;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.xuandq.mylauncher.R;
import com.xuandq.mylauncher.adapter.AppAdapter;

public class DragListener implements View.OnDragListener {

    int positionTarget = -1;
    int positionSource = -1;
    
    @Override
    public boolean onDrag(View v, DragEvent event) {
        Log.d("aaa", "onDrag: ");
        View viewSource = (View) event.getLocalState();
        
        switch (event.getAction()){
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (v.getId() == R.id._app_container && v.getTag() != viewSource.getTag()){
                    Log.d("aaa", "onEntered: " + v.getTag());
                    swapItem(viewSource,v);
                }
                return true;

            case DragEvent.ACTION_DROP:
                Log.d("aaa", "onExited: " + v.getTag());
                viewSource.setVisibility(View.VISIBLE);
                return true;

        }


        return false;
    }

    private void swapItem(View viewSource, View v) {
        RecyclerView rvSource = (RecyclerView) viewSource.getParent();
        Log.d("aaa", "swapItem: "+rvSource);
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