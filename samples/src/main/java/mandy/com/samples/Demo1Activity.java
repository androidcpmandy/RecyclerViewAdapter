package mandy.com.samples;

import android.os.Bundle;
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

/**
 * 最基本MultiTypeAdapter使用方法
 * */
public class Demo1Activity extends AppCompatActivity {

    private DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

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
        });
    }

    private void initData() {
        dataSource = new DataSource.Configuration()
                .withoutAnimation(false)//rv没有增删改的动画效果
                .loadingAlways(false)
                .state(false)//拉到底部展示加载更多
                .saveSate(false)
                .applyConfig();
        for (int i = 0; i < 10; i++) {
            dataSource.add(new MultiTypeItem(R.layout.item_type1, "item_type1 "));
            dataSource.add(new MultiTypeItem(R.layout.item_type2, "item_type2 "));
        }
    }
}
