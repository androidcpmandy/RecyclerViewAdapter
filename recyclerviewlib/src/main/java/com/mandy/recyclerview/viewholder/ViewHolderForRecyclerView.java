package com.mandy.recyclerview.viewholder;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created on 2017/7/4.
 */
public class ViewHolderForRecyclerView extends RecyclerView.ViewHolder {
    private View rootView;
    //    private RecyclerView recyclerView;
    private SparseArrayCompat<RecyclerView> nestedRecyclerViews;//rootView上的rv
    private SparseArray<View> views;
    private int offset;

    public ViewHolderForRecyclerView(View rootView, int offset) {
        super(rootView);
        this.rootView = rootView;
        views = new SparseArray<>();
        this.offset = offset;
    }

    public ViewHolderForRecyclerView(View rootView) {
        this(rootView, 0);
    }

    public View getRootView() {
        return rootView;
    }

    @SuppressWarnings("unchecked")
    public <v extends View> v getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = rootView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (v) view;
    }

    public void setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
    }

    public void setTextViewText(@IdRes int resId, String str) {
        View view = getView(resId);
        if (view instanceof TextView) {
            ((TextView) view).setText(str);
        }
    }

    public void setTextViewColor(@IdRes int resId, int color) {
        View view = getView(resId);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
    }

    /**
     * 内部使用
     */
    public void addRecyclerView(int id, RecyclerView rv) {
        if (nestedRecyclerViews == null) {
            nestedRecyclerViews = new SparseArrayCompat<>();
        }
        nestedRecyclerViews.put(id, rv);
    }

    /**
     * 内部使用
     */
    public RecyclerView getNestedRecyclerView(int id) {
        if (nestedRecyclerViews == null) {
            return null;
        }
        return nestedRecyclerViews.get(id);
    }

    /**
     * 内部使用
     */
    public SparseArrayCompat<RecyclerView> getNestedRecyclerViews() {
        return nestedRecyclerViews;
    }

    /**
     * 在initComponent中通过调用setViewClickListener来设置点击事件
     */
    public void setViewClickListener(@IdRes int resId, @NonNull final OnClickListener listener) {
        View view = getView(resId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition() - offset;
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClick(view, position);
                }


//                View rootView = recyclerView.findContainingItemView(view);
//                if (rootView != null) {
//                    int pos = (int) rootView.getTag(R.id.position);
//                    listener.onClick(view, pos);
//                }
            }
        });

    }

    public interface OnClickListener {
        void onClick(View view, int position);
    }

    /**
     * 在Application中调用MultiTypeAdapter的createLoader，否则无法获取到网络图片
     */
//    public void setImageBitmapFromNet(Context context, int viewId, String path, int defaultResourceId) {
//        MultiTypeAdapter.loadImg(getView(viewId), path, ContextCompat.getDrawable(context, defaultResourceId));
//    }
    public void setImageBitmap(int viewId, int resId) {
        getView(viewId).setBackgroundResource(resId);
    }
}