package mandy.com.samples.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.mandy.recyclerview.log.Logger;
import com.mandy.recyclerview.view.SimpleLoadMoreView;

import mandy.com.samples.R;

public class LoadMoreView extends SimpleLoadMoreView {

    private final LinearInterpolator linearInterpolator;
    private TextView tv;
    private View icon;

    public LoadMoreView(RecyclerView rv, int layoutId) {
        super(rv, layoutId);
        linearInterpolator = new LinearInterpolator();
    }

    @Override
    public void startLoading() {
        Logger.log("开始加载");
        /*
         * 调用setRotation重置下，每次滑到底部加载更多item使用的
         * 都是同一个holder中获取到，不重置下rotation，转圈动画会
         * 越来越慢
         * */
        icon.setRotation(0);
        icon.animate().setInterpolator(linearInterpolator).rotation(360 * 10).setDuration(3000 * 10).start();
    }

    @Override
    public void stopLoading() {
        Logger.log("结束加载");
        icon.animate().cancel();
    }

    @Override
    public void onCreateView(final ViewGroup rootView) {
        tv = rootView.findViewById(R.id.tv);
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
