package mandy.com.samples;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.mandy.recyclerview.adapter.DataSource;
import com.mandy.recyclerview.adapter.MultiTypeAdapter;
import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.viewholder.ViewHolderForRecyclerView;

import java.util.List;

/**
 * item中的布局刷新
 */
public class Demo5Activity extends AppCompatActivity {

    private DataSource dataSource;
    private MultiTypeAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        initData();
        final RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new MultiTypeAdapter(dataSource) {

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
            protected void onRefreshLocal(ViewHolderForRecyclerView holder, @NonNull List<Object> payloads, int position, int layoutId) {
                Log.e("mandy", "onRefreshLocal");
                super.onRefreshLocal(holder, payloads, position, layoutId);
                MultiTypeItem item = (MultiTypeItem) payloads.get(0);
                TextView tv = holder.getView(R.id.tv);
                tv.setText(item.getData().toString());
            }
        });

    }

    private void initData() {
        dataSource = new DataSource.Configuration()
                .withoutAnimation(false)//rv没有增删改的动画效果
                .loadingAlways(false)
                .state(true)//拉到底部展示加载更多
                .saveSate(false)
                .debug(true)
                .applyConfig();
        for (int i = 0; i < 6; i++) {
            dataSource.add(new MultiTypeItem(R.layout.item_type1, "item_type1 "));
            dataSource.add(new MultiTypeItem(R.layout.item_type2, "item_type2 "));
        }

        //模拟刷新操作
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataSource.updateLocal(4, new DataSource.ItemCallback() {
                    @Override
                    public void callback(MultiTypeItem item) {
                        item.data = "change!!!!!!!!!!";
                    }
                });
            }
        }, 3000);
    }

}
