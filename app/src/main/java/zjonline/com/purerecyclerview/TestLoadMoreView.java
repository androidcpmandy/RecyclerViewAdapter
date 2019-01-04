package zjonline.com.purerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mandy.recyclerview.adapter.MultiTypeAdapter;
import com.mandy.recyclerview.log.Logger;
import com.mandy.recyclerview.view.AbstractLoadMoreView;

public class TestLoadMoreView extends AbstractLoadMoreView {

    private TextView tv;
    private MultiTypeAdapter adapter;
    private View icon;

    public TestLoadMoreView(RecyclerView rv, int layoutId, MultiTypeAdapter adapter) {
        super(rv, layoutId);
        this.adapter = adapter;
    }

    @Override
    public void startLoading() {
        Logger.log("开始加载");
        icon.animate().rotation(360*10).setDuration(3000*10).start();
    }

    @Override
    public void stopLoading() {
        Logger.log("结束加载");
        icon.animate().cancel();
    }

    @Override
    public void onCreateView(final ViewGroup rootView) {
        tv = (TextView) rootView.findViewById(R.id.tv);
        icon = rootView.findViewById(R.id.icon);
    }

    @Override
    public void noMore() {
        icon.animate().cancel();
        tv.setText("空空");
    }

    @Override
    public void loading() {
        tv.setText("玩命加载中...");
    }

    @Override
    public void error() {
        icon.animate().cancel();
        tv.setText("挂了");
    }

    @Override
    public void reload() {
        tv.setText("重新加载中......");
    }
}
