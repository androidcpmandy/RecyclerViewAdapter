package com.mandy.recyclerview.bean;

/**
 * Created on 2017/7/5.
 */

public class MultiTypeItem {

    public int type;
    public Object data;

    /**
     * 空白页面使用，不需要data
     * */
    public MultiTypeItem(int type) {
        this.type = type;
        this.data = null;
    }

    public MultiTypeItem(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        return data.equals(obj);
    }
}