package com.mandy.recyclerview.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 自定义加载更多布局
 */
public abstract class AbstractLoadMoreView extends FrameLayout {

    public AbstractLoadMoreView(Context context, int layoutId) {
        super(context);
        LayoutInflater.from(context).inflate(layoutId, this, true);
        onCreateView(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLoading();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoading();
    }

    /**
     * 可以做一些加载的动画
     */
    public abstract void startLoading();

    public abstract void stopLoading();

    public abstract void onCreateView(ViewGroup rootView);
}
