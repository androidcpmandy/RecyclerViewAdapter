package mandy.com.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mandy.recyclerview.adapter.DataSource;
import com.mandy.recyclerview.adapter.MultiTypeAdapter;
import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.viewholder.ViewHolderForRecyclerView;

/**
 * 展示空页面
 */
public class Demo4Activity extends AppCompatActivity {

    private DataSource dataSource;
//    private OptionsPickerView<String> pvOptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        initData();
        final RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MultiTypeAdapter(dataSource) {

            @Override
            protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
                switch (layoutId) {
                    case R.layout.item_empty:
                        Log.e("mandy", "空白页面");
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
                .state(true)
                .saveSate(false)
                .applyConfig();
        dataSource.add(new MultiTypeItem(R.layout.item_empty, "empty"));
    }
}
