package com.mandy.recyclerview.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 弃用，有问题！！
 * */
public class StubView extends FrameLayout {

    private boolean hasNestedRecyclerView;

    public StubView(@NonNull Context context) {
        super(context);
    }

    @Override
    public boolean isLayoutRequested() {
        return hasNestedRecyclerView && super.isLayoutRequested();
    }

    /**
     * 只考虑stubView只有一个子view的情况,内部使用也只有这种情况。
     * 有多个子view的情况下hasNestedRecyclerView总是以最后一个add
     * 的view为准，不准确
     * */
    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        hasNestedRecyclerView = hasNestedRecyclerView(child);
    }

    private boolean hasNestedRecyclerView(View rootView) {
        if (rootView instanceof RecyclerView) {
            return true;
        }
        if (!(rootView instanceof ViewGroup)) {
            return false;
        }
        final ViewGroup parent = (ViewGroup) rootView;
        final int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            if (hasNestedRecyclerView(child)) {
                return true;
            }
        }
        return false;
    }
}
