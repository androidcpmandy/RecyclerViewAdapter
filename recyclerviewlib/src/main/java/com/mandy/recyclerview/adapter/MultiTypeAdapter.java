package com.mandy.recyclerview.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mandy.recyclerview.R;
import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.exception.InvalidMethodException;
import com.mandy.recyclerview.log.Logger;
import com.mandy.recyclerview.viewholder.ViewHolderForRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/7/5.
 */
public class MultiTypeAdapter extends RecyclerView.Adapter<ViewHolderForRecyclerView> implements RecyclerView.OnItemTouchListener {
    //    public final static int SIMPLE_ANIMATION = R.id.simpleAnimation;
    public final static int LOAD_MORE_TYPE = 11100819;
    //    private final boolean supportDataBinding;
//    private boolean useMandy;//原来使用的一个屏幕适配库，不使用的情况下直接设置false即可
    //    private List<MultiTypeItem> data;
    private DataSource dataSource;
    private GestureDetectorCompat gestureDetectorCompat;
    private RecyclerView recyclerView;
    private boolean setFooterEnable;
    private static Loader imgLoader;

    private Map<String, RecyclerView.RecycledViewPool> pool;
    private Map<Integer, SparseArrayCompat<Parcelable>> states;

    private DataObserver observer = new DataObserver();

    private boolean loadMore;//是否允许加载更多
    private final static int INVALID = -1;
    private boolean removeByAdapter;
    //    private boolean addByAdapter;
//    private View tempView;
    private boolean loading;
    private boolean withoutAnimation;
    private boolean saveSate;

    public MultiTypeAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }
//    public MultiTypeAdapter() {
//        this(new ArrayList<MultiTypeItem>(), false);
//    }

//    public MultiTypeAdapter(List<MultiTypeItem> data) {
//        this(data, false);
//    }

//    public MultiTypeAdapter(List<MultiTypeItem> data, boolean useMandy) {
//        this.data = data;
//        this.useMandy = useMandy;
//    }

    public static void createLoader(Context context, Loader loader, boolean printLog) {
        if (context instanceof Activity) {
            Toast.makeText(context, "需要在Application中调用", Toast.LENGTH_LONG).show();
            return;
        }
        Logger.setDebuggable(printLog);
        imgLoader = loader;
    }

    public interface Loader {
        void loadImg(View view, String path, Drawable error);
    }

    @Override
    public int getItemViewType(int position) {
        if (dataSource.isEmpty()) {
            return loadMore ? LOAD_MORE_TYPE : INVALID;
        }
        int count = dataSource.size();
        if (position == count && loadMore) {
            return LOAD_MORE_TYPE;
        } else if (position < count) {
            return dataSource.get(position).getType();
        }
        return INVALID;
    }

    private boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        Logger.log("onAttachedToRecyclerView");
        super.onAttachedToRecyclerView(recyclerView);
        registerAdapterDataObserver(observer);
        dataSource.setAdapter(this);
        this.recyclerView = recyclerView;
        recyclerView.addOnItemTouchListener(this);
        if (withoutAnimation) {
            recyclerView.setItemAnimator(null);
        } else {
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator instanceof SimpleItemAnimator) {
                ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
            }
        }
        gestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), new SingleClick());
        ViewCompat.postOnAnimation(recyclerView, new Runnable() {
            @Override
            public void run() {
                setFooter();
            }
        });
        Logger.setDebuggable(isApkInDebug(recyclerView.getContext()));
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        Logger.log("onDetachedFromRecyclerView");
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(observer);
        recyclerView.removeOnItemTouchListener(this);
    }

    @NonNull
    @Override
    public ViewHolderForRecyclerView onCreateViewHolder(@NonNull ViewGroup parent, int layoutId) {
        if (layoutId == INVALID) {
            throw new IllegalArgumentException("layoutId should not be INVALID");
        }
        View rootView;
        if (layoutId == LOAD_MORE_TYPE) {
            View loadMoreView = createLoadMoreView();
            rootView = loadMoreView == null ? LayoutInflater.from(parent.getContext()).inflate(R.layout.default_multi_adapter_loading, parent, false) : loadMoreView;
        } else {
            rootView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }
        ViewHolderForRecyclerView holder = new ViewHolderForRecyclerView(recyclerView, rootView);
        if (layoutId != LOAD_MORE_TYPE) {
            bindRecycledPool(holder, layoutId);//必须放到initComponent之前设置pool
            initComponentAndCheck(holder, parent, layoutId);
            makeRecycledPoolWorkPerfect(holder);
        }
        return holder;
    }

    /**
     * rv初始化必须放在initComponent中完成
     */
    private void checkInitComponentValid(ViewHolderForRecyclerView holder) throws InvalidMethodException {
        SparseArrayCompat<RecyclerView> nestedRecyclerViews = holder.getNestedRecyclerViews();
        if (nestedRecyclerViews == null) {
            return;
        }
        int size = nestedRecyclerViews.size();
        for (int i = 0; i < size; i++) {
            RecyclerView recyclerView = nestedRecyclerViews.valueAt(i);
            if (recyclerView.getLayoutManager() == null || recyclerView.getAdapter() == null) {
                throw new InvalidMethodException();
            }
        }
    }

    private void makeRecycledPoolWorkPerfect(ViewHolderForRecyclerView holder) {
        RecyclerView.LayoutManager llm = recyclerView.getLayoutManager();
        llm.setItemPrefetchEnabled(true);
        SparseArrayCompat<RecyclerView> nestedRecyclerViews = holder.getNestedRecyclerViews();
        if (nestedRecyclerViews == null || nestedRecyclerViews.size() == 0) {
            return;
        }
        int size = nestedRecyclerViews.size();
        for (int i = 0; i < size; i++) {
            RecyclerView recyclerView = nestedRecyclerViews.valueAt(i);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                ((LinearLayoutManager) layoutManager).setRecycleChildrenOnDetach(true);
            }
        }
    }

//    protected View useMandyLibInflateView(ViewGroup parent, int layoutId) {
//        return null;
//    }

    private void bindRecycledPool(ViewHolderForRecyclerView holder, int viewType) {
        List<RecyclerView> nestedRecyclerViews = findNestedRecyclerView(holder.getRootView());
        if (nestedRecyclerViews.isEmpty()) {
            return;
        }
        if (pool == null) {
            pool = new HashMap<>();
        }
        for (RecyclerView rv : nestedRecyclerViews) {
            if (rv.getId() == View.NO_ID) {
                rv.setId(View.generateViewId());
            }
            String key = viewType + "_" + rv.getId();
            holder.addRecyclerView(rv.getId(), rv);
            RecyclerView.RecycledViewPool recycledViewPool = pool.get(key);
            if (recycledViewPool == null) {
                recycledViewPool = new RecyclerView.RecycledViewPool();
                pool.put(key, recycledViewPool);
            }
            rv.setRecycledViewPool(recycledViewPool);
        }
    }

    protected List<RecyclerView> findNestedRecyclerView(View rootView) {
        List<RecyclerView> list = new ArrayList<>();
        if (rootView instanceof RecyclerView) {
            list.add((RecyclerView) rootView);
            return list;
        }
        if (!(rootView instanceof ViewGroup)) {
            return list;
        }
        final ViewGroup parent = (ViewGroup) rootView;
        final int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            list.addAll(findNestedRecyclerView(child));
        }
        return list;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderForRecyclerView holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            onRefreshLocal(holder, payloads, position, getItemViewType(position));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderForRecyclerView holder, int position) {
        holder.getRootView().setTag(R.id.position, position);
        if (loadMore && dataSource.size() == position) {
            return;
        }
        onBindView(holder, dataSource.get(position), position, getItemViewType(position));
//        if (holder.getRootView().getTag(R.id.simpleAnimation) instanceof Boolean && (Boolean) (holder.getRootView().getTag(R.id.simpleAnimation))) {
//            holder.getRootView().setScaleX(0.8f);
//            holder.getRootView().setScaleY(0.8f);
//            holder.getRootView().animate().scaleX(1).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
//            holder.getRootView().animate().scaleX(1).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
//        }
    }

    private void initComponentAndCheck(ViewHolderForRecyclerView holder, @NonNull ViewGroup parent, int layoutId) {
        initComponent(holder, parent, layoutId);
        checkInitComponentValid(holder);
    }

    /**
     * 初始化操作，常见于item中嵌套rv，viewpager，不要在onBindView中每次初始化这些rv，viewpager
     * 而导致重复调用setAdapter等方法，建议将rv，viewpager初始化放到initComponent
     */
    protected void initComponent(ViewHolderForRecyclerView holder, @NonNull ViewGroup parent, int layoutId) {
    }

    /**
     * 加载更多
     */
    protected void loadMore() {
    }

    protected void onItemClick(ViewHolderForRecyclerView viewHolder, int position, MultiTypeItem data) {
    }

    /**
     * 使用自定义的加载更多布局
     */
    protected View createLoadMoreView() {
        return null;
    }

    @Override
    public int getItemCount() {
        int count = dataSource != null ? dataSource.size() : 0;
        return loadMore ? count + 1 : count;
    }

    /**
     * 嵌套recyclerView的初始化尽量不要放在这里，setAdapter也不要放这里！！
     */
    protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
    }

    protected void onRefreshLocal(ViewHolderForRecyclerView holder, @NonNull List<Object> payloads, int position, int layoutId) {
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (gestureDetectorCompat != null) {
            gestureDetectorCompat.onTouchEvent(e);
        }
        return false;
    }

    /**
     * 在multiTypeAdapter外再包一层wrapperAdapter是否可行需要测试
     */
    public View getChild(int position) {
        if (recyclerView == null) {
            return null;
        }
        return recyclerView.getChildAt(position);
    }

    public ViewHolderForRecyclerView getChildViewHolder(int position) {
        if (recyclerView == null) {
            return null;
        }
        View child = recyclerView.getChildAt(position);
        if (child == null) {
            return null;
        }
        return (ViewHolderForRecyclerView) recyclerView.getChildViewHolder(child);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private class SingleClick extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view == null) {
                return false;
            }
            ViewHolderForRecyclerView viewHolder = (ViewHolderForRecyclerView) recyclerView.getChildViewHolder(view);
            int position = recyclerView.getChildAdapterPosition(view);
            if (!loadMore || position < dataSource.size()) {
                onItemClick(viewHolder, position, dataSource.get(position));
            }
            return true;
        }
    }

    public void setFooter(boolean setFooterEnable) {
        this.setFooterEnable = setFooterEnable;
    }

    private void setFooter() {
        if (!setFooterEnable) {
            return;
        }
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        setFooter(layoutManager);
    }

    private void setFooter(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (dataSource == null || dataSource.isEmpty()) {
                        return 0;
                    }
                    return position != dataSource.size() - 1 ? 1 : gridLayoutManager.getSpanCount();
                }
            });
        }
    }

//    public void setData(List<MultiTypeItem> data) {
//        setData(data, true);
//    }

    public void clearAndReset(@NonNull List<MultiTypeItem> items) {
        dataSource.clear();
        dataSource.addAll(items);
    }

    /**
     * 忽略，已经没什么用，到时候删除
     * */
    @BindingAdapter(value = {"multiAdapter:multiAdapterPath", "multiAdapter:multiAdapterError"})
    public static void loadImg(View view, String path, Drawable error) {
        if (imgLoader == null) {
            return;
        }
        Logger.log("dataBinding loadImg");
        imgLoader.loadImg(view, path, error);
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolderForRecyclerView holder) {
        Logger.log("onViewDetachedFromWindow pos==" + holder.getAdapterPosition());
        super.onViewDetachedFromWindow(holder);
        SparseArrayCompat<RecyclerView> nestedRecyclerViews = holder.getNestedRecyclerViews();
        if (!saveSate || nestedRecyclerViews == null || nestedRecyclerViews.size() == 0) {
            return;
        }
        int pos = holder.getAdapterPosition();
        if (pos == RecyclerView.NO_POSITION) {
            return;
        }
        int size = nestedRecyclerViews.size();
        for (int i = 0; i < size; i++) {
            RecyclerView recyclerView = nestedRecyclerViews.valueAt(i);
            if (recyclerView.getChildCount() == 0) {
                continue;
            }
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
                if (llm.getOrientation() == RecyclerView.HORIZONTAL) {
                    View child = recyclerView.getChildAt(0);
                    ViewHolderForRecyclerView childViewHolder = (ViewHolderForRecyclerView) recyclerView.getChildViewHolder(child);
                    saveInstanceStateOrNot(pos, recyclerView, childViewHolder);
                }
            }
        }
    }

    @SuppressLint("UseSparseArrays")
    private void saveInstanceStateOrNot(int pos, RecyclerView rv, ViewHolderForRecyclerView holder) {
        View rootView = holder.getRootView();
        RecyclerView.LayoutManager llm = rv.getLayoutManager();
        if (!(llm instanceof LinearLayoutManager)) {
            return;
        }
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) rootView.getLayoutParams();
        int left = llm.getDecoratedLeft(rootView) - layoutParams.leftMargin;
        if (holder.getAdapterPosition() == 0 && left == 0) {
            return;
        }
        if (states == null) {
            states = new HashMap<>();
        }
        Parcelable parcelable = llm.onSaveInstanceState();
        if (parcelable == null) {
            return;
        }
        SparseArrayCompat<Parcelable> parcelables = states.get(pos);
        if (parcelables == null) {
            parcelables = new SparseArrayCompat<>();
            states.put(pos, parcelables);
        }
        parcelables.put(rv.getId(), parcelable);
        Logger.log("parcelables size==" + states.size());
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolderForRecyclerView holder) {
        Logger.log("onViewAttachedToWindow");
        super.onViewAttachedToWindow(holder);

        if (loadMore && !loading && holder.getAdapterPosition() == dataSource.size()) {
            loading = true;
            loadMore();
            return;
        }
        SparseArrayCompat<RecyclerView> nestedRecyclerViews = holder.getNestedRecyclerViews();
        if (!saveSate || states == null || states.size() == 0 || nestedRecyclerViews == null || nestedRecyclerViews.size() == 0) {
            return;
        }
        int pos = holder.getAdapterPosition();
        int size = nestedRecyclerViews.size();
        for (int i = 0; i < size; i++) {
            RecyclerView recyclerView = nestedRecyclerViews.valueAt(i);
            int id = recyclerView.getId();
            SparseArrayCompat<Parcelable> parcelables = states.get(pos);
            if (parcelables != null) {
                Parcelable parcelable = parcelables.get(id);
                recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
            }
        }
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        public void onChanged() {
            Logger.log("onChanged");
            if (states == null || states.size() == 0) {
                return;
            }
            states.clear();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            Logger.log("onItemRangeChanged positionStart==" + positionStart + " itemCount==" + itemCount);
            if (states == null || states.size() == 0) {
                return;
            }
            for (int i = positionStart; i < positionStart + itemCount; i++) {
                states.remove(i);
            }
        }

        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            Logger.log("onItemRangeChanged positionStart==" + positionStart + " itemCount==" + itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            Logger.log("onItemRangeInserted positionStart==" + positionStart + " itemCount==" + itemCount);
            if (states == null || states.size() == 0) {
                return;
            }
            List<Integer> positions = new ArrayList<>();
            Integer start = positionStart;
            for (Integer integer : states.keySet()) {
                if (integer >= start) {
                    positions.add(integer);
                }
            }
            if (positions.isEmpty()) {
                return;
            }
            Collections.sort(positions, new Comparator<Integer>() {
                @Override
                public int compare(Integer integer, Integer t1) {
                    return t1 - integer;
                }
            });
            for (Integer position : positions) {
                SparseArrayCompat<Parcelable> remove = states.remove(position);
                states.put(position + itemCount, remove);
            }
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (removeByAdapter) {
                removeData(positionStart, itemCount);
                removeByAdapter = false;
            }
            Logger.log("onItemRangeRemoved positionStart==" + positionStart + " itemCount==" + itemCount);
            if (states == null || states.size() == 0) {
                return;
            }
            List<Integer> positions = new ArrayList<>();
            List<Integer> removes = new ArrayList<>(itemCount);
            Integer start = positionStart;
            for (Integer integer : states.keySet()) {
                if (integer >= start + itemCount) {
                    positions.add(integer);
                } else if (integer >= start && integer < start + itemCount) {
                    removes.add(integer);
                }
            }
            for (Integer remove : removes) {
                states.remove(remove);
            }
            if (positions.isEmpty()) {
                return;
            }
            Collections.sort(positions, new Comparator<Integer>() {
                @Override
                public int compare(Integer integer, Integer t1) {
                    return integer - t1;
                }
            });
            for (Integer position : positions) {
                SparseArrayCompat<Parcelable> remove = states.remove(position);
                states.put(position - itemCount, remove);
            }
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            /*
             * onItemRangeMoved中逻辑没写，用到的时候再添加
             * */
            Logger.log("onItemRangeMoved fromPosition==" + fromPosition +
                    " toPosition==" + toPosition + " itemCount==" + itemCount);
        }
    }

    /**
     * @param removeByAdapter 删除数据操作是否由MultipleTypeAdapter来完成
     */
    void notifyItemRangeRemoved(int start, int itemCount, boolean removeByAdapter) {
        this.removeByAdapter = removeByAdapter;
        notifyItemRangeRemoved(start, itemCount);
    }

//    public void notifyItemRemoved(int position, boolean removeByAdapter) {
//        notifyItemRangeRemoved(position, 1, removeByAdapter);
//    }

    /**
     * 完成数据添加到data操作，不需要外部自己添加
     */
//    public void notifyItemRangeInserted(int position, @NonNull List<MultiTypeItem> addData) {
//        dataSource.addAll(position, addData);
//        notifyItemRangeInserted(position, addData.size());
//    }

//    public void notifyItemInserted(int position, MultiTypeItem addData) {
//        dataSource.add(position, addData);
//        notifyItemRangeInserted(position, 1);
//    }
    private void removeData(int start, int needDeleteCount) {
        int deletedCount = 0;
        for (int i = 0; i < dataSource.size(); i++) {
            if (i == start) {
                dataSource.removeInternal(i);
                i--;
                deletedCount++;
            }
            if (deletedCount == needDeleteCount) {
                break;
            }
        }
    }

    /**
     * 当上拉加载完成后使用该方法进行刷新，内部使用
     *
     * @param list 上拉加载获取到的数据，不是原数据列表
     */
    void notifyItemDataFromLoad(List<MultiTypeItem> list) {
        if (list == null || list.isEmpty() || dataSource == null) {
            loadComplete();
            return;
        }
        dataSource.addAll(list);
        loadComplete();
    }

    public void activateLoadMore(boolean loadMore) {
        configRecyclerViewBehavior(withoutAnimation, saveSate, loadMore);
    }

    /**
     * 在网络请求失败的情况下需要手动调用该方法，达到重置loading的目的
     * 否则重新上拉加载将无效
     */
    public void loadComplete() {
        loading = false;
    }

    /**
     * @param saveSate 是否保存嵌套recyclerView的滑动位置
     *                 调用该方法并且withoutAnimation设置为true后，不要在外部调用
     *                 recyclerView.setItemAnimator否则withoutAnimation可能要失效
     */
    public void configRecyclerViewBehavior(boolean withoutAnimation, boolean saveSate, boolean loadMore) {
        this.withoutAnimation = withoutAnimation;
        if (recyclerView != null && withoutAnimation) {
            recyclerView.setItemAnimator(null);
        }
        this.saveSate = saveSate;
        if (!saveSate && states != null) {
            states.clear();
        }
        this.loadMore = loadMore;
        loading = false;
    }
}