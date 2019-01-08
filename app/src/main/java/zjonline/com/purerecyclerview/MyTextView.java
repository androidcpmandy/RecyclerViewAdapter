package zjonline.com.purerecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

public class MyTextView extends AppCompatTextView {

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        Log.e("mandy","requestLayout");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("mandy","onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
