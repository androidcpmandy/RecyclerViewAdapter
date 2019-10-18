package com.mandy.recyclerview.view;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

@SuppressLint("ViewConstructor")
public class SimpleLoadMoreView extends AbstractLoadMoreView {

    public SimpleLoadMoreView(RecyclerView rv, int layoutId) {
        super(rv, layoutId);
    }

    @Override
    public void onCreateView(ViewGroup rootView) {

    }

    @Override
    public void startLoading() {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void noMore() {

    }

    @Override
    public void loading() {

    }

    @Override
    public void error() {

    }

    @Override
    public void reload() {

    }
}
