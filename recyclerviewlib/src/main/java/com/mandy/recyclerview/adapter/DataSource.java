package com.mandy.recyclerview.adapter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.interfaces.State;
import com.mandy.recyclerview.itemanimator.CustomDefaultItemAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 对adapter要操作的data数据源进行封装，通过该类实现对data和adapter的操作
 */
public class DataSource {

    public final static List<MultiTypeItem> ERROR = null;
    @SuppressWarnings("unchecked")
    public final static List<MultiTypeItem> NO_MORE = Collections.EMPTY_LIST;

    private List<MultiTypeItem> data;
    private MultiTypeAdapter adapter;
    private Configuration config;

    private List<Task> pendingTasks;
    private boolean operationDisallow;
    private int offset;

    private DataSource(List<MultiTypeItem> data) {
        this.data = data;
    }

    void setAdapter(MultiTypeAdapter adapter, int offset) {
        this.adapter = adapter;
        this.offset = offset;
    }

    public void subscribeOn(@NonNull MultiTypeAdapter adapter) {
        adapter.setDataSource(this);
        setAdapter(adapter, adapter.offset());
        if (config == null) {
            config = new Configuration();
        }
        applyConfig();
    }

    private void clear() {
        checkOperationValid();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                data.clear();
                if (adapter != null) {
                    transformLoadMoreState(State.HIDE);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        runOrPend(OTHER, r);
    }

//    public void clearDataOnly() {
//        clearAndHide(false);
//    }

    //    /**
//     * @param hideLoadMore true:加载更多布局一起移除掉 false:只移除data数据
//     */
//    private void clearAndHide() {
//        data.clear();
//        if (adapter != null) {
//            transformLoadMoreState(State.HIDE);
//            adapter.notifyDataSetChanged();
//        }
//    }

    public void add(final int position, final MultiTypeItem item) {
        checkOperationValid();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                data.add(position, item);
                if (adapter != null) {
                    adapter.notifyItemInserted(position + offset);
                }
            }
        };
        runOrPend(OTHER, r);
    }

    /**
     * 填充dataSource数据时调用，涉及到加载更多时调用loadMore方法
     */
    public void add(@NonNull final MultiTypeItem item) {
        checkOperationValid();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                int index = data.size();
                data.add(item);
                if (adapter != null) {
                    adapter.notifyItemInserted(index + offset);
                }
            }
        };
        runOrPend(OTHER, r);
    }

//    /**
//     * @param list         需要加载更多的数据，可以传入一个常量EMPTY表示没有更多数据可以加载
//     *                     传入null表示数据异常
//     * @param fromLoadMore 如果是加载更多获取到的数据，一定要设置为true，其他情况为false
//     */
//    public void addAll(@Nullable final List<MultiTypeItem> list, final boolean fromLoadMore) {
//        checkOperationValid();
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                addAllInternal(INVALID, list, fromLoadMore);
//            }
//        };
//        runOrPend(OTHER, r);
//    }

    /**
     * @param list 需要加载更多的数据，可以传入一个常量EMPTY表示没有更多数据可以加载
     *             传入null表示数据异常
     */
    public void loadMore(@Nullable final List<MultiTypeItem> list) {
        checkOperationValid();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                addAllInternal(INVALID, list, true);
            }
        };
        runOrPend(OTHER, r);
    }

    /**
     * 刷新数据时调用
     */
    public void refresh(List<MultiTypeItem> list) {
        data.clear();
        if (!(list == null || list.isEmpty())) {
            data.addAll(list);
        }
        if (adapter != null) {
            adapter.moveToTop();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 普通添加item方法，如果是加载更多使用loadMore
     */
    public void addAll(@NonNull final List<MultiTypeItem> list) {
        checkOperationValid();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                addAllInternal(INVALID, list, false);
            }
        };
        runOrPend(OTHER, r);
    }

    public void addAll(final int position, @NonNull final List<MultiTypeItem> list) {
        checkOperationValid();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                addAllInternal(position, list, false);
            }
        };
        runOrPend(OTHER, r);
    }

    /**
     * @param position     -1表示插入数据到底部
     * @param list         需要加载更多的数据，可以传入一个常量EMPTY表示没有更多数据可以加载
     *                     传入null表示数据异常
     * @param fromLoadMore 如果是加载更多获取到的数据，一定要设置为true，其他情况为false
     */
    private void addAllInternal(int position, @Nullable List<MultiTypeItem> list, boolean fromLoadMore) {
        if (fromLoadMore && config != null && config.abortData) {
            return;
        }
        if (list == null) {
            if (loadMoreCheck(fromLoadMore)) {
                transformLoadMoreState(State.ERROR);
            }
            return;
        }
        if (list.isEmpty()) {
            if (loadMoreCheck(fromLoadMore)) {//没有更多数据
                transformLoadMoreState(State.NO_MORE);
            }
            return;
        }
        if (loadMoreCheck(fromLoadMore)) {
            if (adapter != null) {
                transformLoadMoreState(State.LOAD_MORE);
                adapter.notifyItemDataFromLoad(list);
            }
        } else if (!fromLoadMore && position == INVALID) {
            int index = data.size();
            data.addAll(list);
            if (adapter != null) {
                adapter.notifyItemRangeInserted(index + offset, list.size());
            }
        } else if (!fromLoadMore) {
            data.addAll(position, list);
            if (adapter != null) {
                adapter.notifyItemRangeInserted(position + offset, list.size());
            }
        }
    }

    private boolean loadMoreCheck(boolean loadMore) {
        if (config == null) {
            config = new Configuration();
        }
        return loadMore && (config.state == State.LOAD_MORE || config.state == State.RELOAD);
    }

    public void remove(final int position) {
        checkOperationValid();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    data.remove(position);
                } else {
                    adapter.notifyItemRangeRemoved(position + offset, 1, true);
                }
            }
        };
        runOrPend(OTHER, r);
    }

    /**
     * @param withTransaction 如果update在beginTransaction内部操作设置为false，其他情况建议
     *                        设置为true
     */
    public void update(final int position, final MultiTypeItem item, boolean withTransaction) {
        checkOperationValid();
        if (withTransaction) {
            beginTransaction();
            updateInternal(position, item, false);
            endTransaction();
        } else {
            updateInternal(position, item, false);
        }
    }

    /**
     * item局部刷新
     */
    public void updateLocal(int position, ItemCallback callback) {
        MultiTypeItem item = get(position);
        if (callback != null) {
            callback.callback(item);
        }
        updateInternal(position, item, true);
    }

    public void update(final int position, final MultiTypeItem item) {
        update(position, item, true);
    }

    private void updateInternal(final int position, final MultiTypeItem item, final boolean local) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                data.set(position, item);
                if (adapter != null) {
                    if (!local) {
                        adapter.notifyItemChanged(position + offset);
                    } else {
                        adapter.notifyItemChanged(position + offset, item);
                    }
                }
            }
        };
        runOrPend(UPDATE, r);
    }

    private void runOrPend(int op, Runnable r) {
        Task task = new Task(op, r);
        if (!transaction) {
            task.execute();
        } else {
            if (op == UPDATE && hasFlag(OTHER)) {
                throw new IllegalStateException("invoke update first! do not invoke add,remove before update");
            }
            flag |= op;
            pendingTasks.add(task);
        }
    }

    private boolean hasFlag(int op) {
        return (flag & op) != 0;
    }

    void removeInternal(int position) {
        if (data != null) {
            data.remove(position);
        }
    }

    private void removeInternal(int position, int itemCount) {
        if (data.isEmpty()) {
            return;
        }
        int lastIndex = data.size() - 1;
        if (position > lastIndex) {
            return;
        }
        int end = position + itemCount - 1;
        if (end > lastIndex) {
            end = lastIndex;
        }
        itemCount = end - position + 1;
        if (adapter == null) {
            removeData(position, itemCount);
        } else {
            adapter.notifyItemRangeRemoved(position + offset, itemCount, true);
        }
    }

    private void removeData(int start, int needDeleteCount) {
        int deletedCount = 0;
        for (int i = 0; i < data.size(); i++) {
            if (i == start) {
                removeInternal(i);
                i--;
                deletedCount++;
            }
            if (deletedCount == needDeleteCount) {
                break;
            }
        }
    }

    public void remove(final int position, final int itemCount) {
        checkOperationValid();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                removeInternal(position, itemCount);
            }
        };
        runOrPend(OTHER, r);
    }

    /**
     * 开启beginTransaction的情况下，需要在callBack中调用
     */
    public boolean isEmpty() {
        checkOperationValid();
        return data.isEmpty();
    }

    /**
     * 开启beginTransaction的情况下，需要在callBack中调用，size在adapter的getitemcount有被使用到，确认下没问题
     */
    public int size() {
        checkOperationValid();
        return data.size();
    }

    /**
     * 开启beginTransaction的情况下，需要在callBack中调用
     */
    public MultiTypeItem get(int position) {
        checkOperationValid();
        return data.get(position);
    }

    /**
     * 内部使用，无视该方法
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void getInternal(final int position, final ItemCallback callback) {
        if (operationDisallow) {
            adapter.recyclerView.postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    callback.callback(get(position));
                }
            });
        } else {
            callback.callback(get(position));
        }
    }

    public interface ItemCallback {
        void callback(MultiTypeItem item);
    }

    /**
     * 如果调用iterator后最终使用到了iterator.remove方法，在必要情况下调用notifyDataChange方法刷新
     */
    public Iterator<MultiTypeItem> iterator() {
        return data.iterator();
    }

    /**
     * 一般情况下不推荐直接使用data进行操作，
     * 开启beginTransaction的情况下，需要在callBack中调用
     */
    public List<MultiTypeItem> getOriginData() {
        checkOperationValid();
        return data;
    }

//    /**
//     * 删除原来的所有数据，添加items中数据
//     *
//     * @param loadMore 重置后是否需要加载更多功能
//     */
//    public void clearAndReset(List<MultiTypeItem> items, boolean loadMore) {
//        clear();
//        if (items == null || items.isEmpty()) {
//            loadMore = false;
//        } else {
//            addAll(items);
//        }
//        transformLoadMoreState(loadMore ? State.LOAD_MORE : State.HIDE);
//        if (adapter != null) {
//            adapter.moveToTop();
//            adapter.addPreDrawListener();
//        }
//    }


    /**
     * @param saveSate      是否保存嵌套recyclerView的滑动位置
     *                      调用该方法并且withoutAnimation设置为true后，不要自己手动调用
     *                      recyclerView.setItemAnimator否则withoutAnimation可能要失效
     * @param loadingAlways 默认值为true，控制loadMore布局移出屏幕是否调用abortLoadMore
     */
    private void configRecyclerViewBehavior(boolean withoutAnimation, boolean saveSate,
                                            @State int state, boolean loadingAlways, boolean debuggable) {
        if (config == null) {
            config = new Configuration();
        }
        config.withoutAnimation = withoutAnimation;
        config.saveSate = saveSate;
        config.state = state;
        config.loadingAlways = loadingAlways;
        config.debuggable = debuggable;
    }

    void applyConfig() {
        if (adapter != null) {
            adapter.configRecyclerViewBehavior(config.withoutAnimation, config.saveSate,
                    config.state, config.loadingAlways, config.debuggable);
        }
    }

    public void enableLoadMore(boolean enable) {
        transformLoadMoreState(enable ? State.LOAD_MORE : State.HIDE);
    }

    private void transformLoadMoreState(@State final int loadMoreState) {
        if (config == null) {
            config = new Configuration();
        }
        config.state = loadMoreState;
        if (adapter != null) {
            adapter.transformLoadMoreState(loadMoreState);
        }
    }

    void updateState(@State int loadMoreState) {
        if (config == null) {
            config = new Configuration();
        }
        config.state = loadMoreState;
    }

    void abort(boolean abortData) {
        if (config == null) {
            config = new Configuration();
        }
        config.abortData = abortData;
    }


    /*******************************************************/

    private int flag;
    private final int UPDATE = 0x01;
    private final int OTHER = 0x10;
    private boolean transaction;
    private final static int INVALID = -1;


    /**
     * 下一帧执行时如果只涉及到单一增，删，改的调用，不需要使用该方法，
     * 如果下一帧执行时涉及到改和其他操作，最好使用beginTransaction，调用
     * beginTransaction后，改的操作优先执行，否则抛异常
     */
    public void beginTransaction() {
        if (transaction) {
            throw new IllegalStateException("do not nest invoke beginTransaction");
        }
        if (adapter == null) {
            throw new NullPointerException("dataSource bind to adapter first");
        }
        if (adapter.recyclerView == null) {
            throw new NullPointerException("invoke rv setAdapter first");
        }
        RecyclerView.ItemAnimator itemAnimator = adapter.recyclerView.getItemAnimator();
        if (itemAnimator == null || !(itemAnimator instanceof CustomDefaultItemAnimator)) {
            return;
        }
        setItemStable(false);
        flag = 0;
        transaction = true;
        if (pendingTasks == null) {
            pendingTasks = new ArrayList<>();
        }
        pendingTasks.clear();
    }

    public void endTransaction() {
        endTransaction(null);
    }

    /**
     * @param callback 在callback中执行和data相关的方法
     */
    public void endTransaction(final Callback callback) {
        if (!transaction) {
            return;
        }
        transaction = false;
        int index = INVALID;
        final List<Task> copy = new ArrayList<>(pendingTasks.size());
        try {
            for (int i = 0; i < pendingTasks.size(); i++) {
                copy.add((Task) pendingTasks.get(i).clone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (copy.isEmpty()) {
            return;
        }
        setItemStable(true);
        for (int i = 0; i < copy.size(); i++) {
            Task task = copy.get(i);
            if (task.flag != UPDATE) {
                index = i;
                break;
            }
            task.execute();
        }

        final int tempIndex = index;
        operationDisallow = tempIndex != INVALID;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                setItemStable(false);
                operationDisallow = false;
                if (tempIndex == INVALID) {
                    return;
                }
                for (int i = tempIndex; i < copy.size(); i++) {
                    Task task = copy.get(i);
                    if (task.flag == OTHER) {
                        copy.get(i).execute();
                    }
                }
                if (callback != null) {
                    callback.callback();
                }
                copy.clear();
            }
        };
        adapter.recyclerView.postOnAnimation(r);
    }

    private void setItemStable(boolean stable) {
        /*
         * 已进行过判空处理，不需要再处理
         * */
        CustomDefaultItemAnimator itemAnimator = (CustomDefaultItemAnimator) adapter.recyclerView.getItemAnimator();
        itemAnimator.setStable(stable);
    }

    private void checkOperationValid() {
        if (operationDisallow) {
            throw new IllegalStateException("should invoke in callback");
        }
    }

    /*******************************************************/

    public static class Configuration {
        boolean withoutAnimation;
        boolean debuggable;//打印日志，开启后可能会影响到滑动流畅
        boolean saveSate;//item中嵌套rv开启后可以保存rv滑动位置，否则设置为false即可
        int state = State.HIDE;
        boolean loadingAlways = true;//控制loadMore布局移出屏幕是否调用abortLoadMore

        boolean abortData;

        public Configuration withoutAnimation(boolean withoutAnimation) {
            this.withoutAnimation = withoutAnimation;
            return this;
        }

        public Configuration debug(boolean debuggable) {
            this.debuggable = debuggable;
            return this;
        }

        public Configuration saveSate(boolean saveSate) {
            this.saveSate = saveSate;
            return this;
        }

        public Configuration state(boolean loadMore) {
            this.state = loadMore ? State.LOAD_MORE : State.HIDE;
            return this;
        }

        public Configuration loadingAlways(boolean loadingAlways) {
            this.loadingAlways = loadingAlways;
            return this;
        }

        public DataSource applyConfig() {
            DataSource ds = new DataSource(new ArrayList<MultiTypeItem>());
            ds.configRecyclerViewBehavior(withoutAnimation, saveSate, state, loadingAlways, debuggable);
            return ds;
        }
    }

    private class Task implements Cloneable {
        private int flag;
        private Runnable r;

        Task(int flag, Runnable r) {
            this.flag = flag;
            this.r = r;
        }

        void execute() {
            if (r != null) {
                r.run();
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    public interface Callback {
        void callback();
    }

    /**
     * 根据指定的view找到该view对应的MultiTypeItem
     * <p>
     * offset未考虑
     */
    public MultiTypeItem getMultiTypeItem(View view) {
        if (adapter != null && adapter.recyclerView != null) {
            RecyclerView.ViewHolder holder = adapter.recyclerView.findContainingViewHolder(view);
            if (holder != null && data != null) {
                int position = holder.getAdapterPosition();
                if (position + 2 <= data.size()) {//data未存放"加载更多"
                    return data.get(position);
                }
            }
        }
        return null;
    }
}
