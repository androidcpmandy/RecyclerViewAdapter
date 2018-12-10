package com.mandy.recyclerview.itemdecoration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.mandy.recyclerview.viewholder.ViewHolderForRecyclerView;

public class PinnedItemDecoration extends SpacesItemDecoration {

    private SparseIntArray sectionChildCount;
    private int headItemType;
    private int headHeight;
    private int headWidth;
    private SparseArray<Bitmap> bitmapSparseArray = new SparseArray<>();
    private SectionChangeListener sectionChangeListener;
    private int currentSectionIndex = -1;

    public PinnedItemDecoration(Context context, @DrawableRes int resId, int space, boolean isNeedFirstSpace) {
        super(context, resId, space, isNeedFirstSpace);
    }

    public PinnedItemDecoration(int headItemType, int color, int space, boolean isNeedFirstSpace, SparseIntArray sectionChildCount) {
        super(color, space, isNeedFirstSpace);
        this.headItemType = headItemType;
        this.sectionChildCount = sectionChildCount;
    }

    public PinnedItemDecoration(int space, boolean isNeedFirstSpace) {
        super(space, isNeedFirstSpace);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
//        /*
        if (parent.getChildCount() < 2 || sectionChildCount.size() == 0) {
            return;
        }
        int offset = 0;

        ViewHolderForRecyclerView holder;
        View view;
        Bitmap bitmap;
        for (int i = 0; i < parent.getChildCount(); i++) {
            view = parent.getChildAt(i);
            holder = (ViewHolderForRecyclerView) parent.getChildViewHolder(view);
            int pos = getSectionIndex(parent.getChildLayoutPosition(view));

            bitmap = bitmapSparseArray.get(pos);
            if (holder.getItemViewType() == headItemType && (bitmap == null || bitmap.isRecycled())) {
                i += sectionChildCount.get(pos);
                headHeight = view.getHeight();
                headWidth = view.getWidth();
                view.setDrawingCacheEnabled(true);
                bitmapSparseArray.append(pos, view.getDrawingCache().copy(Bitmap.Config.ARGB_8888, false));
                view.setDrawingCacheEnabled(false);
            }
        }

        ViewHolderForRecyclerView firstItemHolder = (ViewHolderForRecyclerView) parent.getChildViewHolder(parent.getChildAt(0));
        ViewHolderForRecyclerView secondItemHolder = (ViewHolderForRecyclerView) parent.getChildViewHolder(parent.getChildAt(1));
        View headView = firstItemHolder.getRootView();
        View secondView = secondItemHolder.getRootView();
        int position = parent.getChildLayoutPosition(headView);
        int sectionIndex = getSectionIndex(position);
//        Logger.log("position==" + position + " sectionIndex==" + sectionIndex);
//        bitmap = bitmapSparseArray.get(sectionIndex);
        if (currentSectionIndex != sectionIndex) {
            if (sectionChangeListener != null) {
                currentSectionIndex = sectionIndex;
                sectionChangeListener.sectionChange(sectionIndex);
            }
        }

        if (secondItemHolder.getItemViewType() == headItemType) {
            if (secondView.getTop() < headHeight) {
                offset = secondItemHolder.getRootView().getTop() - headHeight;
            }
        }
        if (bitmapSparseArray.get(sectionIndex) == null) {
            View head = createHead(sectionIndex);
            if (head == null) {
                return;
            }
            specialProcess(head);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headHeight);
            head.setLayoutParams(lp);
            int widthSpec = View.MeasureSpec.makeMeasureSpec(headWidth, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(headHeight, View.MeasureSpec.EXACTLY);
            head.measure(widthSpec, heightSpec);
            head.layout(0, 0, headWidth, headHeight);
            head.draw(canvas);
            head.setDrawingCacheEnabled(true);
            Bitmap b = head.getDrawingCache();
            if (b == null) {
                head.setDrawingCacheEnabled(false);
                return;
            } else {
                bitmapSparseArray.put(sectionIndex, b.copy(Bitmap.Config.ARGB_8888, true));
                head.setDrawingCacheEnabled(false);
            }
        }
        canvas.save();
        canvas.translate(0, offset);
        canvas.drawBitmap(bitmapSparseArray.get(sectionIndex), 0, 0, null);
        canvas.restore();
//        */




        /*
        if (firstItemHolder.getItemViewType() == headItemType && (currentBitmap == null || triggerCreateCurrentBitmap)) {
            triggerCreateCurrentBitmap = false;
            currentHeadView = firstItemHolder.getRootView();
            currentHeadView.setDrawingCacheEnabled(true);
            if (currentBitmap != null) {
                preBitmap = currentBitmap.copy(Bitmap.Config.ARGB_8888, true);
                index = parent.getChildLayoutPosition(currentHeadView);
            }
            bitmap = currentHeadView.getDrawingCache();
            currentBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            headHeight = currentHeadView.getHeight();
        }

        if (secondItemHolder.getItemViewType() == headItemType) {
            if (secondItemHolder.getRootView().getTop() < headHeight) {
                triggerCreateCurrentBitmap = true;
                offset = secondItemHolder.getRootView().getTop() - headHeight;

                if (preBitmap != null && index == parent.getChildLayoutPosition(secondItemHolder.getRootView())) {
                    currentBitmap = preBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    preBitmap.recycle();
                    preBitmap = null;
                }else if(preBitmap!=null){
                    preBitmap.recycle();
                    preBitmap = null;
                }
            }
        }
        canvas.save();
        canvas.translate(0, offset);
        canvas.drawBitmap(currentBitmap, 0, 0, null);
        canvas.restore();
        */
    }

    private int getSectionIndex(int position) {
        int childCount;
        int end = 0;
        for (int i = 0; i < sectionChildCount.size(); i++) {
            childCount = sectionChildCount.get(i);
            if (i == 0) {
                end += childCount;
            } else {
                end = end + 1 + childCount;
            }
            if (end >= position) {
                return i;
            }
        }
        return -1;
    }

    public void addSectionChangeListener(SectionChangeListener sectionChangeListener) {
        this.sectionChangeListener = sectionChangeListener;
    }

    public interface SectionChangeListener {
        void sectionChange(int sectionIndex);
    }

    protected View createHead(int sectionIndex) {
        return null;
    }

    /**
     * 需要对生成的悬浮view进行特殊的处理，否则生成的悬浮view不能屏幕适配
     */
    protected void specialProcess(View root) {

    }

    public int getHeadHeight(){
        return headHeight;
    }
}