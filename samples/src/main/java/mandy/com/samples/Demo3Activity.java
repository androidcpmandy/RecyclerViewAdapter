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
import com.mandy.recyclerview.view.AbstractLoadMoreView;
import com.mandy.recyclerview.viewholder.ViewHolderForRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mandy.com.samples.views.LoadMoreView;

/**
 * 在Demo2Activity基础上增加了可自定义loadMore
 */
public class Demo3Activity extends AppCompatActivity {

    private DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        final Handler handler = new Handler();
        initData();
        RecyclerView recyclerView = findViewById(R.id.rv);
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
                        rightLoad();
                    }
                }, 3000);
            }

            @Override
            protected void abortLoadMore() {
                super.abortLoadMore();
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            protected AbstractLoadMoreView createLoadMoreView(RecyclerView recyclerView) {
                Log.e("mandy", "createLoadMoreView");
                return new LoadMoreView(recyclerView, R.layout.customloadmore);
            }
        });
    }

    private void initData() {
        dataSource = new DataSource.Configuration()
                .withoutAnimation(false)
//                .loadingAlways(false)//加载更多被移除屏幕后即调用abortLoadMore
                .loadingAlways(false)//加载更多被移除屏幕后 不会 调用abortLoadMore
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
     * 正确添加方式
     */
    private void rightLoad() {
        Random random = new Random();
        boolean result = random.nextBoolean();

        result=true;

        if (result) {//表示成功加载
            List<MultiTypeItem> data = new ArrayList<>();
            Random r = new Random();
            int size = r.nextInt(3) + 1;//随机产生数据长度

            size = 1;

            for (int i = 0; i < size; i++) {
                data.add(new MultiTypeItem(R.layout.item_type1, "new data "));
            }
            dataSource.loadMore(data);
        } else {//表示成功失败
            dataSource.loadMore(DataSource.ERROR);
        }
    }
}
