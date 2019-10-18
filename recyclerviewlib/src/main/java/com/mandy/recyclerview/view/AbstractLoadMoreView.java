package com.mandy.recyclerview.view;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 自定义加载更多布局，建议在这里只进行控件ui的展示
 */
public abstract class AbstractLoadMoreView extends FrameLayout {

    protected boolean vertical;

    public AbstractLoadMoreView(RecyclerView recyclerView, int layoutId) {
        super(recyclerView.getContext());
        LayoutInflater.from(recyclerView.getContext()).inflate(layoutId, this, true);
        onCreateView(this);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            throw new IllegalStateException("layoutManager only can be LinearLayoutManager");
        }
        LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
        int widthMode, heightMode;
        if (llm.getOrientation() == LinearLayoutManager.VERTICAL) {
            vertical = true;
            widthMode = ViewGroup.LayoutParams.MATCH_PARENT;
            heightMode = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            vertical = false;
            widthMode = ViewGroup.LayoutParams.WRAP_CONTENT;
            heightMode = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(widthMode, heightMode);
        setLayoutParams(lp);
    }

    public abstract void onCreateView(ViewGroup rootView);

    /**
     * 正在加载的动画
     */
    public abstract void startLoading();

    /**
     * 结束动画
     */
    public abstract void stopLoading();

    /**
     * 没有更多数据可以加载，在这里设置对应控件状态
     */
    public abstract void noMore();

    /**
     * 加载更多数据，在这里设置对应控件状态
     */
    public abstract void loading();

    /**
     * 网络加载异常，在这里设置对应控件状态
     */
    public abstract void error();

    /**
     * 网络异常重新加载，在这里设置对应控件状态
     */
    public abstract void reload();

    @Override
    protected void onAttachedToWindow() {
        Log.e("mandy","load more view onAttachedToWindow");
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.e("mandy","load more view onDetachedFromWindow");
        super.onDetachedFromWindow();
    }
}
