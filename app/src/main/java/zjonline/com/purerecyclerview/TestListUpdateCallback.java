package zjonline.com.purerecyclerview;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

public class TestListUpdateCallback implements ListUpdateCallback {

    @NonNull
    private final RecyclerView.Adapter mAdapter;

    private Handler handler = new Handler();
    private long delayTime = 0;

    /**
     * Creates an AdapterListUpdateCallback that will dispatch update events to the given adapter.
     *
     * @param adapter The Adapter to send updates to.
     */
    public TestListUpdateCallback(@NonNull RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInserted(final int position, final int count) {
        if (position == 3) {
            return;
        }
        Log.e("mandy", "onInserted position==" + position + " count==" + count);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemRangeInserted(position, count);
            }
        }, delayTime);
//        delayTime += 3000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRemoved(final int position, final int count) {
        Log.e("mandy", "onRemoved position==" + position + " count==" + count);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemRangeRemoved(position, count);
            }
        }, delayTime);
//        delayTime += 3000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoved(final int fromPosition, final int toPosition) {
        Log.e("mandy", "onMoved fromPosition==" + fromPosition + " toPosition==" + toPosition);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemMoved(fromPosition, toPosition);
            }
        }, delayTime);
//        delayTime += 3000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChanged(final int position, final int count, final Object payload) {
        Log.e("mandy", "onChanged position==" + position + " count==" + count);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mAdapter.notifyItemRangeChanged(position, count, payload);
            }
        }, delayTime);
//        delayTime += 3000;
    }
}
