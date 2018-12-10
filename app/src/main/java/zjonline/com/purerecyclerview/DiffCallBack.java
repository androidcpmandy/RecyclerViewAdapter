package zjonline.com.purerecyclerview;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.text.TextUtils;
import android.util.Log;

import com.mandy.recyclerview.bean.MultiTypeItem;

import java.util.List;

public class DiffCallBack extends DiffUtil.Callback {

    private List<MultiTypeItem> oldData;
    private List<MultiTypeItem> newData;

    public DiffCallBack(List<MultiTypeItem> oldData, List<MultiTypeItem> newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public int getOldListSize() {
        return oldData.size();
    }

    @Override
    public int getNewListSize() {
        return newData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        boolean result = oldData.get(oldItemPosition).getType() == newData.get(newItemPosition).getType();
        Log.e("mandy", "areItemsTheSame oldItemPosition==" + oldItemPosition +
                " newItemPosition==" + newItemPosition + " result==" + result);
        return result;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        boolean result = TextUtils.equals(oldData.get(oldItemPosition).getData().toString()
                , newData.get(newItemPosition).getData().toString());
        Log.e("mandy", "areContentsTheSame oldItemPosition==" + oldItemPosition +
                " newItemPosition==" + newItemPosition + " result==" + result);
        return result;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
//        Log.e("mandy", "getChangePayload oldItemPosition==" + oldItemPosition + " newItemPosition==" + newItemPosition);
//        if (oldItemPosition == 0 && newItemPosition == 0) {
//            return "change!!!";
//        }
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
