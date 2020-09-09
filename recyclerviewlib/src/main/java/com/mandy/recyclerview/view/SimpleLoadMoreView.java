package com.mandy.recyclerview.view;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class SimpleLoadMoreView extends AbstractLoadMoreView {

    public SimpleLoadMoreView(RecyclerView rv, int layoutId) {
        super(rv, layoutId);
    }

    @Override
    public void onCreateView(ViewGroup rootView) {

    }

    @Override
    public void startLoadingAnimation() {

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
