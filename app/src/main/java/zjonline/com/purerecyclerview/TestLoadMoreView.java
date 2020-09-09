package zjonline.com.purerecyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mandy.recyclerview.log.Logger;
import com.mandy.recyclerview.view.AbstractLoadMoreView;

import androidx.recyclerview.widget.RecyclerView;

public class TestLoadMoreView extends AbstractLoadMoreView {

    private TextView tv;
    private View icon;

    public TestLoadMoreView(RecyclerView rv, int layoutId) {
        super(rv, layoutId);
    }

    @Override
    public void startLoadingAnimation() {
        Logger.log("开始加载");
        icon.setRotation(0);
        icon.animate().rotation(360 * 10).setDuration(3000 * 10).start();
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
