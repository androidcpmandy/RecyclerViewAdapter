package com.mandy.recyclerview.viewholder;

import android.view.View;

import com.mandy.recyclerview.interfaces.State;
import com.mandy.recyclerview.view.AbstractLoadMoreView;

/**
 * 加载更多使用的holder，内部使用
 */
public class LoadMoreViewHolder extends ViewHolderForRecyclerView {

    public LoadMoreViewHolder(View rootView) {
        super(rootView);
    }

    public void stateChange(@State int state) {
        if (!(getRootView() instanceof AbstractLoadMoreView)) {
            throw new IllegalStateException("rootView is not AbstractLoadMoreView");
        }
        AbstractLoadMoreView loadMoreView = (AbstractLoadMoreView) getRootView();
        switch (state) {
            case State.NO_MORE:
                loadMoreView.noMore();
                break;
            case State.LOAD_MORE:
                loadMoreView.loading();
                break;
            case State.ERROR:
                loadMoreView.error();
                break;
            case State.RELOAD:
                loadMoreView.reload();
        }
    }

    public void stopLoading(@State int state) {
        if (state == State.LOAD_MORE || state == State.RELOAD) {
            ((AbstractLoadMoreView) getRootView()).stopLoading();
        }
    }

    public void startLoading() {
        ((AbstractLoadMoreView) getRootView()).startLoading();
    }

}
