package zjonline.com.purerecyclerview;

import android.view.ViewGroup;
import android.widget.TextView;

import com.mandy.recyclerview.view.SimpleLoadMoreView;

import androidx.recyclerview.widget.RecyclerView;

public class InnerLoadMoreView extends SimpleLoadMoreView {

    private TextView tv;

    public InnerLoadMoreView(RecyclerView rv, int layoutId) {
        super(rv, layoutId);
    }

    @Override
    public void onCreateView(ViewGroup rootView) {
        super.onCreateView(rootView);
        tv = (TextView) rootView.getChildAt(0);
    }

    @Override
    public void noMore() {
        tv.setText("inner\n没\n有\n更\n多\n数\n据\n...");
    }

    @Override
    public void loading() {
        tv.setText("inner\n加\n载\n...");
    }

    @Override
    public void error() {
        tv.setText("inner\n挂\n了");
    }

    @Override
    public void reload() {
        tv.setText("inner\n重\n新\n加\n载");
    }

}
