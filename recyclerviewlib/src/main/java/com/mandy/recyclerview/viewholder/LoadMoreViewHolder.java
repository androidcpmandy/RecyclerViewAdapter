package com.mandy.recyclerview.viewholder;

import android.view.View;

import com.mandy.recyclerview.interfaces.State;
import com.mandy.recyclerview.log.Logger;
import com.mandy.recyclerview.view.AbstractLoadMoreView;

/**
 * 加载更多使用的holder，内部使用
 */
public class LoadMoreViewHolder extends ViewHolderForRecyclerView {

    private int currentState;

    public LoadMoreViewHolder(View rootView) {
        super(rootView);
    }

    public void stateChange(@State int state) {
        if (currentState == state) {
            return;
        }
        currentState = state;
        if (!(getRootView() instanceof AbstractLoadMoreView)) {
            throw new IllegalStateException("rootView is not AbstractLoadMoreView");
        }
        AbstractLoadMoreView loadMoreView = (AbstractLoadMoreView) getRootView();
        switch (state) {
            case State.NO_MORE:
                loadMoreView.noMore();
                Logger.log("loadMore state==没有更多");
                break;
            case State.LOAD_MORE:
                loadMoreView.loading();
                Logger.log("loadMore state==正在加载");
                break;
            case State.ERROR:
                loadMoreView.error();
                Logger.log("loadMore state==加载错误");
                break;
            case State.RELOAD:
                loadMoreView.reload();
                Logger.log("loadMore state==重新加载");
        }
    }

    public void stopLoading(@State int state) {
        if (state == State.LOAD_MORE || state == State.RELOAD) {
            ((AbstractLoadMoreView) getRootView()).stopLoading();
        }
    }

    public void startLoading() {
        ((AbstractLoadMoreView) getRootView()).startLoadingAnimation();
    }

}
