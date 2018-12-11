package zjonline.com.purerecyclerview;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.mandy.recyclerview.view.SimpleLoadMoreView;

public class TestLoadMoreView extends SimpleLoadMoreView {

    public TestLoadMoreView(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public void onCreateView(final ViewGroup rootView) {
        rootView.post(new Runnable() {
            @Override
            public void run() {
                Log.e("mandy", "w==" + rootView.getWidth() + " h==" + rootView.getHeight());
            }
        });
    }
}
