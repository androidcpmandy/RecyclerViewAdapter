package zjonline.com.purerecyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mandy.recyclerview.adapter.DataSource;
import com.mandy.recyclerview.adapter.MultiTypeAdapter;
import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.viewholder.ViewHolderForRecyclerView;

public class WrapRvActivity extends AppCompatActivity {

    private DataSource dataSource;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_layout);

        dataSource = new DataSource.AdapterConfig().applyConfig();
        for (int i = 0; i < 3; i++) {
            dataSource.add(new MultiTypeItem(R.layout.testlayout1, "hello" + i));
        }

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MultiTypeAdapter(dataSource){
            @Override
            protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
                Object data = item.getData();
                holder.setText(R.id.tv, (String) data);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        dataSource.add(new MultiTypeItem(R.layout.testlayout1, "add one"));
    }
}
