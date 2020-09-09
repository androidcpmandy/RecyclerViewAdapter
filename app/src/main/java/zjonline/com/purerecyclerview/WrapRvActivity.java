package zjonline.com.purerecyclerview;

import android.os.Bundle;

import com.mandy.recyclerview.adapter.DataSource;
import com.mandy.recyclerview.adapter.MultiTypeAdapter;
import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.viewholder.ViewHolderForRecyclerView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WrapRvActivity extends AppCompatActivity {

    private DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrap_layout);

        dataSource = new DataSource.Configuration().applyConfig();
        for (int i = 0; i < 3; i++) {
            dataSource.add(new MultiTypeItem(R.layout.testlayout1, "hello" + i));
        }

        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MultiTypeAdapter(dataSource){
            @Override
            protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
                Object data = item.getData();
                holder.setText(R.id.tv, (String) data);
            }
        });
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        dataSource.add(new MultiTypeItem(R.layout.testlayout1, "add one"));
    }
}
