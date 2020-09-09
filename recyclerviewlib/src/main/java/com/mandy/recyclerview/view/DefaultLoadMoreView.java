package com.mandy.recyclerview.view;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mandy.recyclerview.R;

import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class DefaultLoadMoreView extends SimpleLoadMoreView {

    private TextView tv;

    public DefaultLoadMoreView(RecyclerView rv, int layoutId) {
        super(rv, layoutId);
    }

    @Override
    public void onCreateView(ViewGroup rootView) {
        super.onCreateView(rootView);
        tv = rootView.findViewById(R.id.tv);
    }

    @Override
    public void noMore() {
        if (vertical) {
            tv.setText("没有更多数据了");
        } else {
            tv.setText("没\n有\n更\n多\n数\n据\n了");
        }
    }

    @Override
    public void loading() {
        if (vertical) {
            tv.setText("加载更多......");
        } else {
            tv.setText("加\n载\n更\n多\n...");
        }
    }

    @Override
    public void error() {
        if (vertical) {
            tv.setText("网络异常......");
        } else {
            tv.setText("网\n络\n异\n常\n...");
        }
    }

    @Override
    public void reload() {
        if (vertical) {
            tv.setText("重新加载中......");
        } else {
            tv.setText("重\n新\n加\n载\n中\n...");
        }
    }
}
