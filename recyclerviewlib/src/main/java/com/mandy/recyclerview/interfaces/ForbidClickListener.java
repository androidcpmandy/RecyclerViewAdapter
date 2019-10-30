package com.mandy.recyclerview.interfaces;

import android.view.View;

public class ForbidClickListener implements View.OnClickListener {
    private long lastTime;
    private final static long DEFAULT_INTERVAL = 1000;

    protected void forbidClick(View view) {
    }

    protected long getInterval() {
        return DEFAULT_INTERVAL;
    }

    @Override
    public final void onClick(View view) {
        long interval = getInterval();
        boolean noLimit;
        noLimit = interval <= 0;
        if (!noLimit) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime < interval) {
                showMsg();
                return;
            }
            lastTime = currentTime;
        }
        forbidClick(view);
    }

    protected void showMsg() {
    }
}
