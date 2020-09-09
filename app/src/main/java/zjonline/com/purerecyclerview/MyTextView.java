package zjonline.com.purerecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

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
