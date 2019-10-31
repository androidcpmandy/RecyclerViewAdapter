package mandy.com.samples.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mandy.recyclerview.log.Logger;
import com.mandy.recyclerview.view.SimpleLoadMoreView;

import mandy.com.samples.R;

public class LoadMoreView1 extends SimpleLoadMoreView {

//    private final LinearInterpolator linearInterpolator;
    private TextView tv;
    private ProgressBar progress;

    public LoadMoreView1(RecyclerView rv, int layoutId) {
        super(rv, layoutId);
//        linearInterpolator = new LinearInterpolator();
    }

    @Override
    public void startLoadingAnimation() {
        Logger.log("开始加载");
        progress.setVisibility(View.VISIBLE);
        /*
         * 调用setRotation重置下，每次滑到底部加载更多item使用的
         * 都是同一个holder中获取到，不重置下rotation，转圈动画会
         * 越来越慢
         * */
//        icon.setRotation(0);
//        icon.animate().setInterpolator(linearInterpolator).rotation(360 * 10).setDuration(3000 * 10).start();
    }

    @Override
    public void stopLoading() {
        Logger.log("结束加载");
//        icon.animate().cancel();
    }

    @Override
    public void onCreateView(final ViewGroup rootView) {
        tv = rootView.findViewById(R.id.tv);
        progress = rootView.findViewById(R.id.progress);
    }

    @Override
    public void noMore() {
//        icon.animate().cancel();
        tv.setText("加载到底部了！！");
        progress.setVisibility(View.GONE);
    }

    @Override
    public void loading() {
        progress.setVisibility(View.VISIBLE);
        tv.setText("玩命加载中...");
    }

    @Override
    public void error() {
//        icon.animate().cancel();
        tv.setText("挂了");
        progress.setVisibility(View.GONE);
    }

    @Override
    public void reload() {
        progress.setVisibility(View.VISIBLE);
        tv.setText("重新加载中......");
    }
}
