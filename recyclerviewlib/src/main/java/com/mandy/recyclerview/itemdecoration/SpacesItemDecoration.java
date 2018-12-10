package com.mandy.recyclerview.itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private boolean isNeedFirstSpace = false;
    private Drawable drawable;

    public SpacesItemDecoration(Context context, @DrawableRes int resId, int space, boolean isNeedFirstSpace) {
        this.space = space;
        this.isNeedFirstSpace = isNeedFirstSpace;
        drawable = ContextCompat.getDrawable(context, resId);
    }

    public SpacesItemDecoration(int color, int space, boolean isNeedFirstSpace) {
        this.space = space;
        this.isNeedFirstSpace = isNeedFirstSpace;
        drawable = new ColorDrawable(color);
    }

    public SpacesItemDecoration(int space, boolean isNeedFirstSpace) {
        this.space = space;
        this.isNeedFirstSpace = isNeedFirstSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = 0;
        outRect.right = 0;
        outRect.bottom = 0;
        // Add top margin only for the first item to avoid double space between items
        if (isNeedFirstSpace) {
            outRect.top = space;
        } else {
            if (parent.getChildLayoutPosition(view) != 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (drawable == null) {
            super.onDraw(c, parent, state);
            return;
        }
        int width = parent.getWidth();
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            if (i == 0 && !isNeedFirstSpace) {
                continue;
            }
            int top;
            if (i == 0) {
                top = 0;
            } else {
                View child = parent.getChildAt(i - 1);
                top = child.getBottom();
            }
            drawable.setBounds(0, top, width, top + space);
            drawable.draw(c);
        }
    }
}