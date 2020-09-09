package com.mandy.recyclerview.layoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;

public class SmoothScroller extends LinearSmoothScroller {

    public LinearLayoutManager layoutManager;

    public SmoothScroller(Context context, LinearLayoutManager layoutManager) {
        super(context);
        this.layoutManager = layoutManager;
    }

    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return layoutManager.computeScrollVectorForPosition(targetPosition);
    }

    @Override
    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
        return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
    }

    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return 1f;
    }

    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;
    }
}