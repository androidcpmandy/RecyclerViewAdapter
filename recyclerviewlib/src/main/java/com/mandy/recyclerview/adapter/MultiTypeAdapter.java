package com.mandy.recyclerview.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.mandy.recyclerview.R;
import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.exception.InvalidMethodException;
import com.mandy.recyclerview.interfaces.State;
import com.mandy.recyclerview.itemanimator.CustomDefaultItemAnimator;
import com.mandy.recyclerview.layoutmanager.SmoothScroller;
import com.mandy.recyclerview.log.Logger;
import com.mandy.recyclerview.view.AbstractLoadMoreView;
import com.mandy.recyclerview.view.DefaultLoadMoreView;
import com.mandy.recyclerview.view.StubView;
import com.mandy.recyclerview.viewholder.LoadMoreViewHolder;
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
public class MultiTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerView.OnItemTouchListener {

    private boolean debuggable;
    //    public final static int SIMPLE_ANIMATION = R.id.simpleAnimation;
    public final static int LOAD_MORE_TYPE = 11100819;
    private DataSource dataSource;
    private GestureDetectorCompat gestureDetectorCompat;
    RecyclerView recyclerView;
    private boolean setFooterEnable;
    public RecyclerView.RecycledViewPool pool;
    private Map<Integer, SparseArrayCompat<Parcelable>> states;

    private DataObserver observer = new DataObserver();

    private final static int INVALID = -1;
    private boolean removeByAdapter;
    private boolean loading;
    private boolean withoutAnimation;
    private boolean saveSate;
    private int state;
    private Runnable loadMoreRunnable;
    private int rvDirection = INVALID;
    private boolean loadingAlways;
    private boolean loadSuccess;

    /**
     * 通过该标志位来判断是否子view已经填充满rv，
     * 在adapter绑定rv或者调用datasource的clearAndReset
     * 时将会重新设置isFillUp
     */
    private boolean isFillUp = true;//是否子view能够填充满rv

    /**
     * notifyDataSetChanged触发onChange调用，
     * 防止无限循环注册onPreDrawListener
     */
    private boolean registerListener = true;

    /**
     * 在加载更多item执行动画的时候禁止rv可以滑动，
     * 可能存在一种情况，在加载更多item还未到达底部
     * 此时再次快速滑到底部，最终会导致加载更多item
     * 动画到底部后不会触发onViewDetachedFromWindow.
     * <p>
     * 基于上述原因在加载更多动画执行时禁止rv可以滑动
     */
    private boolean intercept;
    private boolean loadMoreAttach;

    public MultiTypeAdapter() {
    }

    public MultiTypeAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public final int getItemViewType(int position) {
        int count = dataSource.size();
        if (position == count && showLoadMore()) {
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
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        Logger.log("onAttachedToRecyclerView");
        super.onAttachedToRecyclerView(recyclerView);
        registerAdapterDataObserver(observer);
        if (dataSource != null) {
            dataSource.setAdapter(this, offset());
            dataSource.applyConfig();
        }
        this.recyclerView = recyclerView;
        addPreDrawListener();

        setHasFixedSize(recyclerView);
        recyclerView.addOnItemTouchListener(this);
        if (withoutAnimation) {
            recyclerView.setItemAnimator(null);
        } else {
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (itemAnimator instanceof DefaultItemAnimator) {
                recyclerView.setItemAnimator(new CustomDefaultItemAnimator());
                CustomDefaultItemAnimator animator = (CustomDefaultItemAnimator) recyclerView.getItemAnimator();
                animator.setSupportsChangeAnimations(false);
                animator.setChangeDuration(0);
            }
        }
        gestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), new SingleClick());
        ViewCompat.postOnAnimation(recyclerView, new Runnable() {
            @Override
            public void run() {
                setFooter();
            }
        });
        Logger.setDebuggable(debuggable && isApkInDebug(recyclerView.getContext()));
    }

    private void setHasFixedSize(RecyclerView recyclerView) {
        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        boolean result = layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT || layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT;
        recyclerView.setHasFixedSize(!result);
    }

    private void addPreDrawListener() {
        /*
         * 在state状态位支持"加载更多"布局时才进一步操作
         * */
        if (maybeShowLoadMore()) {
            recyclerView.getViewTreeObserver().addOnPreDrawListener(new InnerPreDrawListener());
        }
    }

    /**
     * 判断子view是否能够填充满rv，依次来决定是否放置"加载更多"布局
     * <p>
     * 目前isFillUp只考虑了垂直滚动的情况，后期视情况扩充
     */
    private boolean isFillUp() {
        boolean b = recyclerView.canScrollVertically(1);
        if (b) {
            return true;
        }
        int childCount = recyclerView.getChildCount();
        if (childCount == 0) {
            return false;
        }
        View child = recyclerView.getChildAt(childCount - 1);
        int rvHeight = recyclerView.getHeight();
        int paddingBottom = recyclerView.getPaddingBottom();
        int rvBottom = rvHeight - paddingBottom;

        RecyclerView.LayoutManager llm = recyclerView.getLayoutManager();
        int bottomDecorationHeight = llm.getBottomDecorationHeight(child);

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
        int bottomMargin = layoutParams.bottomMargin;
        int bottom = child.getBottom();
        int totalHeight = bottom + bottomMargin + bottomDecorationHeight;

        return rvBottom <= totalHeight;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
//        Logger.log("onDetachedFromRecyclerView");
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(observer);
        recyclerView.removeOnItemTouchListener(this);
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int layoutId) {
        if (layoutId == INVALID) {
            throw new IllegalArgumentException("layoutId should not be INVALID");
        }
        View rootView;
        ViewHolderForRecyclerView holder;
        if (rvDirection == INVALID) {
            rvDirection = takeDirection();
        }
        boolean vertical = rvDirection == LinearLayoutManager.VERTICAL;
        if (layoutId == LOAD_MORE_TYPE) {
            View loadMoreView = createLoadMoreView(recyclerView);
            int loadMoreId = vertical ? R.layout.default_multi_adapter_loading_vertical :
                    R.layout.default_multi_adapter_loading_horizontal;
            rootView = loadMoreView == null ? new DefaultLoadMoreView(recyclerView, loadMoreId) : loadMoreView;
            holder = new LoadMoreViewHolder(rootView);
        } else {
            rootView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            holder = new ViewHolderForRecyclerView(rootView, offset());

//            holder = new ViewHolderForRecyclerView(generateStubView(layoutId), offset());
        }

        if (layoutId != LOAD_MORE_TYPE) {
            bindRecycledPool(holder);
            initComponentAndCheck(holder, parent, layoutId);
            bindRecycledPool(holder);
            makeRecycledPoolWorkPerfect(holder);
        }
        return holder;
    }

    private int takeDirection() {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            throw new IllegalStateException("layoutManager only can be LinearLayoutManager");
        }
        LinearLayoutManager llm = (LinearLayoutManager) layoutManager;
        return llm.getOrientation();
    }

    /**
     * 有问题，弃用
     */
    private StubView generateStubView(int layoutId) {
        StubView stubView = new StubView(recyclerView.getContext());
        LayoutInflater.from(recyclerView.getContext()).inflate(layoutId, stubView, true);
        View child = stubView.getChildAt(0);
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(lp.width, lp.height);
        stubView.setLayoutParams(params);
        return stubView;
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

    @SuppressLint("ObsoleteSdkInt")
    private void bindRecycledPool(ViewHolderForRecyclerView holder) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return;
        }
        List<RecyclerView> nestedRecyclerViews = findNestedRecyclerView(holder.getRootView());
        if (nestedRecyclerViews.isEmpty()) {
            return;
        }
        if (pool == null) {
            pool = new RecyclerView.RecycledViewPool();
        }
        for (RecyclerView rv : nestedRecyclerViews) {
            if (rv.getId() == View.NO_ID) {
                rv.setId(View.generateViewId());
            }
//            String key = viewType + "_" + rv.getId();
            if (holder.getNestedRecyclerView(rv.getId()) == null) {
                holder.addRecyclerView(rv.getId(), rv);
            }
//            RecyclerView.RecycledViewPool recycledViewPool = pool.get(key);
//            if (recycledViewPool == null) {
//                recycledViewPool = new RecyclerView.RecycledViewPool();
//                pool.put(key, recycledViewPool);
//            }
//            rv.setRecycledViewPool(recycledViewPool);

//            rv.setHasFixedSize(true);
//            RecyclerView.ItemAnimator itemAnimator = rv.getItemAnimator();
//            if (itemAnimator instanceof SimpleItemAnimator) {
//                ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
//            }
            rv.setRecycledViewPool(pool);
        }
    }

    private List<RecyclerView> findNestedRecyclerView(View rootView) {
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
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        try {
            if (!(holder instanceof ViewHolderForRecyclerView)) {
                return;
            }
            if (payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {
                onRefreshLocal((ViewHolderForRecyclerView) holder, payloads, position, getItemViewType(position));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("mandy", "exception when onBindViewHolder!!!!!!!!!!!!!!!!!!");
        }
    }

    /**
     * 开启预加载的情况下，在滑到最后一个item(非加载更多)的时候就会提前调用
     * onBindViewHolder加载"加载更多"
     */
    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == invalidPosition() || !(holder instanceof ViewHolderForRecyclerView)) {
            return;
        }
        ViewHolderForRecyclerView temp = (ViewHolderForRecyclerView) holder;
        if (temp.getDataSource() != dataSource) {
            temp.setDataSource(dataSource);
        }
        boolean result = showLoadMore();
        if (result && dataSource.size() == position && holder instanceof LoadMoreViewHolder) {
            return;
        }
        onBindView((ViewHolderForRecyclerView) holder, dataSource.get(position), position, getItemViewType(position));
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
     * 而导致重复调用setAdapter等方法，建议将rv，viewpager初始化放到initComponent。
     * 另外需要将设置点击事件放到该方法中实现。
     */
    protected void initComponent(ViewHolderForRecyclerView holder, @NonNull ViewGroup parent, int layoutId) {
    }

    /**
     * 加载更多
     */
    protected void loadMore() {
        Logger.log("加载更多");
    }

    /**
     * 加载过程中，loadMore布局被移除
     * 在该方法中进行中断网络请求操作
     */
    @CallSuper
    protected void abortLoadMore() {
        Logger.log("中断加载");
        dataSource.abort(true);
    }

    /**
     * 网络加载失败的时候调用
     */
    protected void reload() {
        Logger.log("重新加载");
    }

    /**
     * 对于整个item设置点击可使用该方法，否则建议调用holder的setViewClickListener方法
     */
    protected void onItemClick(ViewHolderForRecyclerView viewHolder, int position, MultiTypeItem data) {
    }

    /**
     * 使用自定义的加载更多布局
     */
    protected AbstractLoadMoreView createLoadMoreView(RecyclerView recyclerView) {
        return null;
    }

    @Override
    public int getItemCount() {
        int count = dataSource != null ? dataSource.size() : 0;
        /*
         * 当只有一个或没有数据时，"加载更多"直接不显示
         * 只有一个数据的情况通常为一个默认空白页面,
         * */
        if (count == 1 || count == 0) {
            return count;
        }
        boolean result = showLoadMore();
        return result ? count + 1 : count;
    }

    /**
     * 嵌套recyclerView的初始化尽量不要放在这里，setAdapter也不要放这里！！
     */
    protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
    }

    @CallSuper
    protected void onRefreshLocal(ViewHolderForRecyclerView holder, @NonNull List<Object> payloads, int position, int layoutId) {
        if (holder.getDataSource() != dataSource) {
            holder.setDataSource(dataSource);
        }
    }

    /**
     * 单使用MultiTypeAdapter时用不到该方法，当MultiTypeAdapter在wrapAdapter内部时
     * 可能需要重写该方法以获取正确的item位置信息
     */
    protected int offset() {
        return 0;
    }

    private int getAdjustPosition(RecyclerView.ViewHolder holder) {
        return holder.getAdapterPosition() - offset();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (intercept) {
            return true;
        }
        if (gestureDetectorCompat != null) {
            gestureDetectorCompat.onTouchEvent(e);
        }
        return false;
    }

//    public View getChild(int position) {
//        if (recyclerView == null) {
//            return null;
//        }
//        return recyclerView.getChildAt(position);
//    }

//    public ViewHolderForRecyclerView getChildViewHolder(int position) {
//        if (recyclerView == null) {
//            return null;
//        }
//        View child = recyclerView.getChildAt(position);
//        if (child == null) {
//            return null;
//        }
//        return (ViewHolderForRecyclerView) recyclerView.getChildViewHolder(child);
//    }

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
            final RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (!(viewHolder instanceof ViewHolderForRecyclerView)) {
                return false;
            }
            final int position = getAdjustPosition(viewHolder);
            if (position == invalidPosition()) {
                return false;
            }
            boolean result = state == State.HIDE;
            if (result || position < dataSource.size()) {
                dataSource.getInternal(position, new DataSource.ItemCallback() {
                    @Override
                    public void callback(MultiTypeItem item) {
                        onItemClick((ViewHolderForRecyclerView) viewHolder, position, item);
                    }
                });
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

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
//        Logger.log("onViewDetachedFromWindow pos==" + holder.getAdapterPosition());
        super.onViewDetachedFromWindow(holder);

        /*
         * wrapAdapter调用onViewDetachedFromWindow传递进来的holder
         * 不一定是ViewHolderForRecyclerView，可能是wrapAdapter自带
         * holder
         * */
        if (!(holder instanceof ViewHolderForRecyclerView)) {
            return;
        }
        int position = getAdjustPosition(holder);
        if ((position == getItemCount() - 1 || position == -1) && showLoadMore()
                && holder instanceof LoadMoreViewHolder) {

            loadMoreAttach = false;
            intercept = false;

            if (state == State.ERROR) {
                transformLoadMoreState(State.RELOAD, false);
            } else if (!loadingAlways && (state == State.LOAD_MORE || state == State.RELOAD)) {
                if (!loadSuccess) {
                    loadComplete();
                    abortLoadMore();
                } else {
                    loadSuccess = false;
                }
            }
            ((LoadMoreViewHolder) holder).stopLoading(state);
            return;
        }

        SparseArrayCompat<RecyclerView> nestedRecyclerViews = ((ViewHolderForRecyclerView) holder).getNestedRecyclerViews();
        if (!saveSate || nestedRecyclerViews == null || nestedRecyclerViews.size() == 0) {
            return;
        }
        int pos = getAdjustPosition(holder);
        if (pos == invalidPosition()) {
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
        if (getAdjustPosition(holder) == 0 && left == 0) {
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
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
//        Logger.log("onViewAttachedToWindow");
        super.onViewAttachedToWindow(holder);
        if (!(holder instanceof ViewHolderForRecyclerView)) {
            return;
        }
        boolean result = state == State.LOAD_MORE || state == State.ERROR || state == State.RELOAD;
        //holder.getAdapterPosition() == datasource.size修改成如下，没问题吧？
        if (result && getAdjustPosition(holder) == getItemCount() - 1 && holder instanceof LoadMoreViewHolder) {

            loadMoreAttach = true;

            /*
             * 以下五行是下滑移除loadMore后网络请求仍然能进行的逻辑
             * */
            if (loadingAlways) {
                if (state == State.ERROR) {
                    state = State.RELOAD;
                }
            }
            transformLoadMoreState(state, false);
            loadData((LoadMoreViewHolder) holder);
            return;
        }
        SparseArrayCompat<RecyclerView> nestedRecyclerViews = ((ViewHolderForRecyclerView) holder).getNestedRecyclerViews();
        if (!saveSate || states == null || states.size() == 0 || nestedRecyclerViews == null || nestedRecyclerViews.size() == 0) {
            return;
        }
        int pos = getAdjustPosition(holder);
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

    private void loadData(LoadMoreViewHolder holder) {
        /*
         * 保证每次加载更多item出现时startLoading都会被调用
         * 否则加载更多的转圈动画不会被执行，在如下操作下
         * "加载更多出现-->隐藏-->再出现"
         * */
        holder.startLoading();

        if (!loading) {
            if (state == State.RELOAD || state == State.LOAD_MORE) {
                loading = true;

                loadSuccess = false;
                dataSource.abort(false);

                final int temp = state;
                if (loadMoreRunnable == null) {
                    loadMoreRunnable = new Runnable() {
                        @Override
                        public void run() {

                            /*
                             * 首次刷新时，超过一屏并且"加载更多"允许显示，
                             * 再次刷新时，不足一屏情况下，"加载更多"会先
                             * 布局到屏幕上，然后notifydatachange被调用
                             * "加载更多"再次被移除屏幕。
                             * 当"加载更多"被添加到屏幕时会导致loadData被
                             * 调用，所以需要通过isFillUp禁止loadMoreRunnable
                             * 内部逻辑被执行
                             * */
                            if (!isFillUp) {
                                return;
                            }


                            if (temp == State.RELOAD) {
                                reload();
                            } else {
                                loadMore();
                            }
                        }
                    };
                }
                recyclerView.post(loadMoreRunnable);
            }
        }
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        public void onChanged() {
            Logger.log("onChanged");
            if (registerListener) {
                addPreDrawListener();
            }
            if (states == null || states.size() == 0) {
                return;
            }
            states.clear();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            int pos = positionStart - offset();
            Logger.log("onItemRangeChanged positionStart==" + positionStart + " itemCount==" + itemCount);
            if (states == null || states.size() == 0) {
                return;
            }
            for (int i = pos; i < pos + itemCount; i++) {
                states.remove(i);
            }
        }

        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            Logger.log("onItemRangeChanged positionStart==" + positionStart + " itemCount==" + itemCount);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            addPreDrawListener();
            int pos = positionStart - offset();
            Logger.log("onItemRangeInserted positionStart==" + positionStart + " itemCount==" + itemCount);
            if (states == null || states.size() == 0) {
                return;
            }
            List<Integer> positions = new ArrayList<>();
            Integer start = pos;
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
            addPreDrawListener();
            int pos = positionStart - offset();
            if (removeByAdapter) {
                removeData(pos, itemCount);
                removeByAdapter = false;
            }
            Logger.log("onItemRangeRemoved pos==" + pos + " itemCount==" + itemCount);
            if (states == null || states.size() == 0) {
                return;
            }
            List<Integer> positions = new ArrayList<>();
            List<Integer> removes = new ArrayList<>(itemCount);
            Integer start = pos;
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

        if (loadMoreAttach) {
            intercept = true;
        }

        loadSuccess = true;
        dataSource.addAll(list);
        loadComplete();
    }

    /**
     * 在网络请求失败的情况下调用该方法，达到重置loading的目的
     * 否则重新上拉加载将无效
     */
    private void loadComplete() {
        loading = false;
    }

    void configRecyclerViewBehavior(boolean withoutAnimation, boolean saveSate, @State int state,
                                    boolean loadingAlways, boolean debuggable) {
        this.withoutAnimation = withoutAnimation;
        this.loadingAlways = loadingAlways;
        this.debuggable = debuggable;
        if (recyclerView != null) {
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            if (withoutAnimation) {
                recyclerView.setItemAnimator(null);
            } else if (itemAnimator instanceof DefaultItemAnimator) {
                CustomDefaultItemAnimator animator = new CustomDefaultItemAnimator();
                recyclerView.setItemAnimator(animator);
            }
        }
        this.saveSate = saveSate;
        if (!saveSate && states != null) {
            states.clear();
        }

        this.state = state;
        Logger.log("初始化state==" + state);
        loadComplete();
    }

    void transformLoadMoreState(@State int loadMoreState) {
        transformLoadMoreState(loadMoreState, true);
    }

    private void transformLoadMoreState(@State int loadMoreState, final boolean resetLoading) {
        if (resetLoading) {
            loadComplete();
        }
        if (loadMoreState == State.HIDE) {
            if (state != State.HIDE) {
                state = State.HIDE;
                /*
                 * adapter内部有涉及到调用transformLoadMoreState，所以dataSource中state的变化
                 * 需要放到transformLoadMoreState中处理
                 * */
                dataSource.updateState(state);
                notifyItemRangeRemoved(getItemCount(), 1);
            }
            return;
        }
        int oldState = state;
        state = loadMoreState;
//        boolean statusChange = oldState != state;
        dataSource.updateState(state);

        if (recyclerView == null) {
            return;
        }
        int childCount = recyclerView.getChildCount();

        /*
         * 获取最后一个item的index
         * */
        int lastIndex = childCount - 1;
        View child = recyclerView.getChildAt(lastIndex);
        while (child != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(child);
            if (holder instanceof ViewHolderForRecyclerView) {
                break;
            }
            child = recyclerView.getChildAt(--lastIndex);
        }

        if (child != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(child);
            if (!(holder instanceof ViewHolderForRecyclerView)) {
                return;
            }
            /*
             * if分支保证加载更多item还在屏幕上
             * */
            if (holder instanceof LoadMoreViewHolder) {
                boolean result = showLoadMore();
                if (result) {
                    LoadMoreViewHolder loadMore = (LoadMoreViewHolder) holder;
                    loadMore.stateChange(state);
                }
            } else {
                if (oldState != State.HIDE) {
                    notifyItemRangeChanged(getItemCount() - 1, 1);
                } else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    notifyItemRangeInserted(getItemCount() - 1, 1);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastItem == getItemCount() - 2) {
                        SmoothScroller scroller = new SmoothScroller(
                                recyclerView.getContext(), ((LinearLayoutManager) recyclerView.getLayoutManager()));
                        scroller.setTargetPosition(getItemCount() - 1);
                        scroller.layoutManager.startSmoothScroll(scroller);
                    }
                } else {
                    notifyDataSetChanged();
                }
            }
        }
    }

    private int invalidPosition() {
        return RecyclerView.NO_POSITION - offset();
    }

    private boolean showLoadMore() {
        return maybeShowLoadMore() && isFillUp;
    }

    /**
     * 判断状态位理论上是否需要展示"加载更多"布局
     */
    private boolean maybeShowLoadMore() {
        return state == State.LOAD_MORE || state == State.NO_MORE
                || state == State.ERROR || state == State.RELOAD;
    }

    void moveToTop() {
        if (recyclerView != null && getItemCount() > 0) {
            recyclerView.scrollToPosition(0);
        }
    }

    private class InnerPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        @Override
        public boolean onPreDraw() {
            Logger.log("onPreDraw");
            recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
            boolean current = isFillUp();
            boolean change = (isFillUp != current);
            isFillUp = current;
            if (change) {
                Logger.log("调用notifyDataSetChanged");
                registerListener = false;
                /*
                 * 触发rv内部重新调用getItemCount方法
                 * */
                notifyDataSetChanged();
                registerListener = true;
            }
            return !isFillUp;
        }
    }
}