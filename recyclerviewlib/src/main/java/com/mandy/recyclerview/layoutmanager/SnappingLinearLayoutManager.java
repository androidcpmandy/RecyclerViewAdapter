package com.mandy.recyclerview.layoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

import com.mandy.recyclerview.log.Logger;

public class SnappingLinearLayoutManager extends LinearLayoutManager{

    private ScrollListener listener;

    public SnappingLinearLayoutManager(Context context){
        super(context);
    }

    public SnappingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    public void addOnScrollListener(ScrollListener listener){
        this.listener=listener;
    }

    private class TopSnappedSmoothScroller extends LinearSmoothScroller {
        public TopSnappedSmoothScroller(Context context) {
            super(context);
        }

        @Override
        protected void onStart() {
            super.onStart();
            if (listener!=null) {
                listener.onStart();
            }
            Logger.log("onStart=="+getTargetPosition());
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (listener!=null) {
                listener.onStop();
                listener.onStopForce();
            }
            Logger.log("onstop"+isRunning());
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return SnappingLinearLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
    }

    public interface ScrollListener{
        void onStart();
        void onStop();
        void onStopForce();
    }
}
