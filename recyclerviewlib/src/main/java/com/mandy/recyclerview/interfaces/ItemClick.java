package com.mandy.recyclerview.interfaces;

import android.view.View;

public interface ItemClick<T> {

    void click(View view, T data);
}
