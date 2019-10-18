package mandy.com.samples;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mandy.recyclerview.adapter.DataSource;
import com.mandy.recyclerview.adapter.MultiTypeAdapter;
import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.viewholder.ViewHolderForRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 最基本MultiTypeAdapter使用方法
 */
public class Demo2Activity extends AppCompatActivity {

    private DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        final Handler handler = new Handler();
        initData();
        RecyclerView recyclerView = findViewById(R.id.rv);


//        LinearLayoutManager layoutManager = new LinearLayoutManager(this){
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };layoutManager.
//        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MultiTypeAdapter(dataSource) {

            @Override
            protected void initComponent(ViewHolderForRecyclerView holder, @NonNull ViewGroup parent, int layoutId) {
                /*
                 * 给每一个item都设置了点击事件，放到initComponent处理
                 * */
                holder.setViewClickListener(R.id.tv, new ViewHolderForRecyclerView.OnClickListener() {
                    @Override
                    public void onClick(View view, MultiTypeItem item, int position) {
                        Log.e("mandy", "pos===" + position + " data==" + item.getData().toString());
                    }
                });
            }

            @Override
            protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
                switch (layoutId) {
                    case R.layout.item_type1:
                        holder.setText(R.id.tv, item.getData().toString() + "" + position);
                        break;
                    case R.layout.item_type2:
                        holder.setText(R.id.tv, item.getData().toString() + "" + position);
                        break;
                    default:
                        break;
                }
            }

            @Override
            protected void loadMore() {
                super.loadMore();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

//                        wrongLoad();参考方法说明

                        rightLoad();
                    }
                }, 3000);
            }

            @Override
            protected void abortLoadMore() {
                super.abortLoadMore();
                handler.removeCallbacksAndMessages(null);
            }
        });
    }

    private void initData() {
        dataSource = new DataSource.Configuration()
                .withoutAnimation(false)
//                .loadingAlways(false)//加载更多被移除屏幕后即调用abortLoadMore
                .loadingAlways(true)//加载更多被移除屏幕后 不会 调用abortLoadMore
                .state(true)
                .debug(true)//开启debug模式，可以打印出adapter内部日志
                .saveSate(false)
                .applyConfig();
        for (int i = 0; i < 10; i++) {
            dataSource.add(new MultiTypeItem(R.layout.item_type1, "item_type1 "));
            dataSource.add(new MultiTypeItem(R.layout.item_type2, "item_type2 "));
        }
    }

    /**
     * 当加载多条数据时，不要通过add方法逐条添加，
     * 通过add方法展示加载更多另一个问题就是每次
     * loadmore布局被移除时都会触发abortLoadMore
     * */
    private void wrongLoad(){
        for (int i = 0; i < 4; i++) {
            dataSource.add(new MultiTypeItem(R.layout.item_type1,"new data "));
        }
    }

    /**
     * 正确添加方式
     * */
    private void rightLoad(){
        /*
         * 三种情况
         * （1）正常加载
         * （2）没有更多数据
         * （3）网络异常
         * */

//        正常加载
        List<MultiTypeItem> data = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            data.add(new MultiTypeItem(R.layout.item_type1, "new data "));
        }
        dataSource.loadMore(data);

//        没有更多数据
//        dataSource.loadMore(DataSource.NO_MORE);

//        网络异常
//        dataSource.loadMore(DataSource.ERROR);
    }
}
