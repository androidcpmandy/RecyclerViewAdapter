package com.mandy.recyclerview.viewholder;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.mandy.recyclerview.adapter.DataSource;
import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.interfaces.ForbidClickListener;
import com.mandy.recyclerview.log.Logger;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on 2017/7/4.
 */
public class ViewHolderForRecyclerView extends RecyclerView.ViewHolder {
    private View rootView;
    private DataSource dataSource;
    private SparseArrayCompat<RecyclerView> nestedRecyclerViews;//rootView上的rv
    private SparseArray<View> views;
    private int offset;

    public ViewHolderForRecyclerView(View rootView, int offset) {
        super(rootView);
        this.rootView = rootView;
        views = new SparseArray<>();
        this.offset = offset;
    }

    public void setDataSource(DataSource ds) {
        Logger.log("setDataSource");
        dataSource = ds;
    }

    public DataSource getDataSource() {
        return dataSource;
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
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void addRecyclerView(int id, RecyclerView rv) {
        if (nestedRecyclerViews == null) {
            nestedRecyclerViews = new SparseArrayCompat<>();
        }
        nestedRecyclerViews.put(id, rv);
    }

    /**
     * 内部使用
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public RecyclerView getNestedRecyclerView(int id) {
        if (nestedRecyclerViews == null) {
            return null;
        }
        return nestedRecyclerViews.get(id);
    }

    /**
     * 内部使用
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public SparseArrayCompat<RecyclerView> getNestedRecyclerViews() {
        return nestedRecyclerViews;
    }

    /**
     * 在initComponent中通过调用setViewClickListener来设置点击事件
     */
    public void setViewClickListener(@IdRes int resId, @NonNull final OnClickListener listener) {
        setViewClickListener(resId, 1000, listener);
    }

    /**
     * @param interval 点击间隔时间控制
     */
    public void setViewClickListener(@IdRes int resId, final long interval, @NonNull final OnClickListener listener) {
        View view = getView(resId);
        view.setOnClickListener(new ForbidClickListener() {

            @Override
            protected long getInterval() {
                return interval;
            }

            @Override
            public void forbidClick(final View view) {
                int position = getAdapterPosition();
                final int offsetPos = position - offset;
                if (position != RecyclerView.NO_POSITION) {

                    dataSource.getInternal(offsetPos, new DataSource.ItemCallback() {
                        @Override
                        public void callback(MultiTypeItem item) {
                            listener.onClick(view, item, offsetPos);
                        }
                    });
                }
            }
        });
    }

    public interface OnClickListener {
        void onClick(View view, MultiTypeItem item, int position);
    }

    public void setImageBitmap(int viewId, int resId) {
        getView(viewId).setBackgroundResource(resId);
    }
}