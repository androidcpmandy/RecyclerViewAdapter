package com.mandy.recyclerview.interfaces;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({State.NO_MORE, State.LOAD_MORE, State.HIDE, State.ERROR, State.RELOAD})
@Retention(RetentionPolicy.SOURCE)
public @interface State {
    int NO_MORE = 1;
    int LOAD_MORE = 2;
    int HIDE = 3;
    int ERROR = 4;
    int RELOAD = 5;
}