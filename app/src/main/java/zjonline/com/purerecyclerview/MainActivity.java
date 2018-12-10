package zjonline.com.purerecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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

public class MainActivity extends Activity {

    private RecyclerView rv;

    //    private static RecyclerView.RecycledViewPool pool = new RecyclerView.RecycledViewPool();
    private ArrayList<MultiTypeItem> list;
    private DataSource data;

    private ArrayList<MultiTypeItem> newData;
    private MultiTypeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**************************************************/
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("mandy", "loadMore");
//                List<MultiTypeItem> list = new ArrayList<>();
//                list.add(new MultiTypeItem(R.layout.testlayout, "new one"));
//                list.add(new MultiTypeItem(R.layout.testlayout, "new two"));
//                        list.add(new MultiTypeItem(R.layout.testlayout, "new three"));
//                adapter.notifyItemRangeInserted(data.size(), list);
//                data.addAll(list);
//                adapter.notifyItemRangeInserted(data.size(),list);
//                adapter.notifyItemRangeRemoved(0, 5, true);
//            }
//        }, 2000);
        /**************************************************/


//        /*
        data = new DataSource(new ArrayList<MultiTypeItem>());
//        data.add(new MultiTypeItem(R.layout.testlayout, "old1"));
//        data.add(new MultiTypeItem(R.layout.testlayout1, "old2"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "old3"));
//        data.add(new MultiTypeItem(R.layout.testlayout1, "old4"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "old5"));
//        data.add(new MultiTypeItem(R.layout.testlayout1, "old6"));

//        data.add(new MultiTypeItem(R.layout.testlayout, "hello7"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "hello4"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "hello4"));

//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
        data.add(new MultiTypeItem(R.layout.testlayout1, "hello1"));
        data.add(new MultiTypeItem(R.layout.testlayout1, "hello2"));
        data.add(new MultiTypeItem(R.layout.testlayout1, "hello3"));
        data.add(new MultiTypeItem(R.layout.testlayout2, "test5,test6,test7,test8"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello4"));
        data.add(new MultiTypeItem(R.layout.testlayout1, "hello5"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello6"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello7"));
        data.add(new MultiTypeItem(R.layout.testlayout1, "hello8"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello9"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello10"));
        data.add(new MultiTypeItem(R.layout.testlayout2, "test5,test6,test7,test8"));
        data.add(new MultiTypeItem(R.layout.testlayout1, "hello8"));
        data.add(new MultiTypeItem(R.layout.testlayout1, "hello9"));
        data.add(new MultiTypeItem(R.layout.testlayout1, "hello10"));


        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager;
        rv.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        rv.setAdapter(adapter = new MultiTypeAdapter(data) {
            @Override
            protected void initComponent(ViewHolderForRecyclerView holder, @NonNull ViewGroup parent, int layoutId) {
                if (layoutId == R.layout.testlayout) {
                    holder.setViewClickListener(R.id.tv, new ViewHolderForRecyclerView.OnClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            MultiTypeItem multiTypeItem = data.get(position);
//                            Log.e("mandy", "click==" + multiTypeItem.getData().toString());
                        }
                    });
                } else if (layoutId == R.layout.testlayout1) {
                    holder.setViewClickListener(R.id.tv, new ViewHolderForRecyclerView.OnClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            MultiTypeItem multiTypeItem = data.get(position);
                        }
                    });
                } else if (layoutId == R.layout.testlayout2) {
                    RecyclerView rv = holder.getView(R.id.rv);
                    LinearLayoutManager ll;
                    rv.setLayoutManager(ll = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    List<MultiTypeItem> list = new ArrayList<>();
                    rv.setAdapter(new MultiTypeAdapter(new DataSource(list)) {

                        @Override
                        protected void initComponent(ViewHolderForRecyclerView holder, @NonNull ViewGroup parent, int layoutId) {
                        }

                        @Override
                        protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
                            String str = (String) item.getData();
                            holder.setText(R.id.tv, str);
                        }
                    });
                }
            }

            @Override
            protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
                if (layoutId == R.layout.testlayout) {
                    String text = (String) item.getData();
                    holder.setText(R.id.tv, text);
                } else if (layoutId == R.layout.testlayout1) {
                    String text = (String) item.getData();
                    holder.setText(R.id.tv, text);
                } else if (layoutId == R.layout.testlayout2) {
                    RecyclerView recyclerView = (RecyclerView) holder.getView(R.id.rv);

                    MultiTypeAdapter adapter = (MultiTypeAdapter) recyclerView.getAdapter();
//                    List<MultiTypeItem> multiTypeItems = adapter.getData();
//                    multiTypeItems.clear();
                    String str = (String) item.getData();
                    String[] split = str.split(",");
                    ArrayList<MultiTypeItem> list = new ArrayList<>();
                    for (String s : split) {
                        list.add(new MultiTypeItem(R.layout.subitem, s));
                    }
//                    adapter.setData(multiTypeItems);
                    adapter.clearAndReset(list);
                }
            }

            @Override
            protected void onRefreshLocal(ViewHolderForRecyclerView holder, @NonNull List<Object> payloads, int position, int layoutId) {
//                Log.e("mandy", "onRefreshLocal pos==" + position + " payloads==" + payloads.size());
                if (layoutId == R.layout.testlayout) {
                    String text = (String) payloads.get(0);
                    holder.setText(R.id.tv, text);
                }
            }

            @Override
            protected void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("mandy", "loadMore");
                        List<MultiTypeItem> list = new ArrayList<>();
                        list.add(new MultiTypeItem(R.layout.testlayout, "new one"));
                        list.add(new MultiTypeItem(R.layout.testlayout, "new two"));
//                        notifyItemRangeInserted(data.size(), list);
                        data.addAll(list, true);
//                        adapter.notifyItemRangeChanged(data.size() - list.size(), list.size());

//                        notifyItemDataFromLoad(list);
                    }
                }, 2000);
            }

            @Override
            protected View createLoadMoreView() {
//                return new SimpleLoadMoreView(MainActivity.this,R.layout.customloadmore);
                return new TestLoadMoreView(MainActivity.this, R.layout.customloadmore);
            }
        });

        adapter.configRecyclerViewBehavior(false, true, true);
//        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher().watch(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        data.add(1, new MultiTypeItem(R.layout.testlayout, "new one"));
//        data.add(2, new MultiTypeItem(R.layout.testlayout, "new two"));
//        data.add(3, new MultiTypeItem(R.layout.testlayout, "new three"));
//        data.add(4, new MultiTypeItem(R.layout.testlayout, "new four"));
//        data.add(5, new MultiTypeItem(R.layout.testlayout, "new five"));


//        List<MultiTypeItem> list = new ArrayList<>();
//        list.add(new MultiTypeItem(R.layout.testlayout, "new one"));
//        list.add(new MultiTypeItem(R.layout.testlayout, "new two"));
//        list.add(new MultiTypeItem(R.layout.testlayout, "new three"));
//        list.add(new MultiTypeItem(R.layout.testlayout, "new four"));
//        list.add(new MultiTypeItem(R.layout.testlayout, "new five"));
//        adapter.notifyItemRangeInserted(0, list);

        /*
        Random random = new Random();
        int index = random.nextInt(data.size());
        int type = random.nextInt(2);
        int itemType = random.nextInt(3);
        if (type == 0) {
            MultiTypeItem item = null;
            if (itemType == 0) {
                item = new MultiTypeItem(R.layout.testlayout, "add new one");
            } else if (itemType == 1) {
                item = new MultiTypeItem(R.layout.testlayout1, "add new one");
            } else if (itemType == 2) {
                item = new MultiTypeItem(R.layout.testlayout2, "index1,index2,index3");
            }
//            adapter.notifyItemInserted(index, item);
            data.add(index,item);
        } else if (type == 1) {
//            adapter.notifyItemRemoved(index, true);
            data.remove(index);
        }
        Log.e("mandy", "type==" + type + " itemType==" + itemType);
        */

//        data.remove(1,4);

    }
}
