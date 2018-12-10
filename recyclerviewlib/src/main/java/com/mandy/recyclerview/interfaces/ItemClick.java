package com.mandy.recyclerview.interfaces;

import android.view.View;

public interface ItemClick<T> {

    public void click(View view, T data);
}
