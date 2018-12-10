package com.mandy.recyclerview.adapter;


import android.support.annotation.NonNull;

import com.mandy.recyclerview.bean.MultiTypeItem;

import java.util.Iterator;
import java.util.List;

/**
 * 对data的操作通过DataSource来完成，最好不要自己操作data
 */
public class DataSource {
    private String t = "adada";
    private List<MultiTypeItem> data;
    private MultiTypeAdapter adapter;

    public DataSource(List<MultiTypeItem> data) {
        this.data = data;
    }

    void setAdapter(MultiTypeAdapter adapter) {
        this.adapter = adapter;
    }

    public void clear() {
        assertData();
        data.clear();
        if (adapter != null) {
            adapter.activateLoadMore(false);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 如果涉及到多次add操作，使用addAll
     */
    public void add(int position, MultiTypeItem item) {
        assertData();
        data.add(position, item);
        if (adapter != null) {
            adapter.notifyItemInserted(position);
        }
    }

    public void add(@NonNull MultiTypeItem item) {
        assertData();
        int index = data.size();
        data.add(item);
        if (adapter != null) {
            adapter.notifyItemInserted(index);
        }
    }

    /**
     * @param fromLoadMore 如果是加载更多获取到的数据，一定要设置为true，其他情况为false
     */
    public void addAll(@NonNull List<MultiTypeItem> list, boolean fromLoadMore) {
        if (list.isEmpty()) {
            return;
        }
        assertData();
        if (fromLoadMore) {
            if (adapter != null) {
                adapter.notifyItemDataFromLoad(list);
            }
        } else {
            int index = data.size();
            data.addAll(list);
            if (adapter != null) {
                adapter.notifyItemRangeInserted(index, list.size());
            }
        }
    }

    public void addAll(@NonNull List<MultiTypeItem> list) {
        addAll(list, false);
    }

    public void addAll(int position, @NonNull List<MultiTypeItem> list) {
        if (list.isEmpty()) {
            return;
        }
        assertData();
        data.addAll(position, list);
        if (adapter != null) {
            adapter.notifyItemRangeInserted(position, list.size());
        }
    }

    public MultiTypeItem remove(int position) {
        MultiTypeItem item;
        assertData();
        item = data.remove(position);
        if (adapter != null) {
            adapter.notifyItemRemoved(position);
        }
        return item;
    }

    public void set(int position, MultiTypeItem item) {
        assertData();
        data.set(position, item);
        if (adapter != null) {
            adapter.notifyItemChanged(position);
        }
    }

    void removeInternal(int position) {
        if (data != null) {
            data.remove(position);
        }
    }

    public void remove(int position, int itemCount) {
        assertData();
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
        if (adapter != null) {
            adapter.notifyItemRangeRemoved(position, itemCount, true);
        }
    }

    public boolean isEmpty() {
        assertData();
        return data.isEmpty();
    }

    public int size() {
        assertData();
        return data.size();
    }

    public MultiTypeItem get(int position) {
        assertData();
        return data.get(position);
    }

    /**
     * 如果调用iterator后最终使用到了iterator.remove方法，在必要情况下调用notifyDataChange方法刷新
     */
    public Iterator<MultiTypeItem> iterator() {
        assertData();
        return data.iterator();
    }

    private void assertData() {
        if (data == null) {
            throw new IllegalStateException("data is null");
        }
    }
}
