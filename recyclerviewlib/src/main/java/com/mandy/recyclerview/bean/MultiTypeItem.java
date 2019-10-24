package com.mandy.recyclerview.bean;

/**
 * Created on 2017/7/5.
 */

public class MultiTypeItem {
    //    private final static int NOT_SUPPORT_DATA_BINDING = -1;
    private final int type;
    private final Object data;
//    private final int dataType;

//    public MultiTypeItem(int type, Object data) {
//        this(type, data, NOT_SUPPORT_DATA_BINDING);
//    }

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
//        this.dataType = dataType;
    }

//    public int getDataType() {
//        return dataType;
//    }

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

//    public boolean isSupportDataBinding() {
//        return dataType != NOT_SUPPORT_DATA_BINDING && data != null;
//    }
}