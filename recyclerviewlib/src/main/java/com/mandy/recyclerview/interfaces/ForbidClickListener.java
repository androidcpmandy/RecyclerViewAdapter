package com.mandy.recyclerview.interfaces;

import android.view.View;

public class ForbidClickListener implements View.OnClickListener {
    private long lastTime;
    //    private int position = -1;
    private final static int DEFAULT_INTERVAL = 1000;

    protected void forbidClick(View view) {
    }

    protected void forbidClick(View view, int position) {
    }

    protected int getInterval() {
        return DEFAULT_INTERVAL;
    }

    @Override
    public void onClick(View view) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime < getInterval()) {
            showMsg();
            return;
        }
        lastTime = currentTime;
//        if (position == -1) {
        forbidClick(view);
//        } else {
//            forbidClick(view, position);
//        }
    }

//    @Override
//    public void onClick(View view, int position) {
//        this.position = position;
//        onClick(view);
//    }

    protected void showMsg() {
    }
}
