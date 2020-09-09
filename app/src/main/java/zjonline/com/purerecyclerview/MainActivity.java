package zjonline.com.purerecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.mandy.recyclerview.adapter.DataSource;
import com.mandy.recyclerview.adapter.MultiTypeAdapter;
import com.mandy.recyclerview.bean.MultiTypeItem;
import com.mandy.recyclerview.itemanimator.CustomDefaultItemAnimator;
import com.mandy.recyclerview.log.Logger;
import com.mandy.recyclerview.view.AbstractLoadMoreView;
import com.mandy.recyclerview.viewholder.ViewHolderForRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends Activity {

    private RecyclerView rv;

    private ArrayList<MultiTypeItem> list;
    private DataSource data;

    private ArrayList<MultiTypeItem> newData;
    private MultiTypeAdapter adapter;
    private int loadTime;
    private Handler handler;


    private CustomDefaultItemAnimator itemAnimator;


//    private RecyclerView innerRv;//临时rv会变动

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
//        data = new DataSource(new ArrayList<MultiTypeItem>());
        data = new DataSource.Configuration().state(false).loadingAlways(true).saveSate(true).applyConfig();
//        data.add(new MultiTypeItem(R.layout.testlayout, "old1"));
//        data.add(new MultiTypeItem(R.layout.testlayout1, "old2"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "old3"));
//        data.add(new MultiTypeItem(R.layout.testlayout1, "old4"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "old5"));
//        data.add(new MultiTypeItem(R.layout.testlayout1, "old6"));

//        data.add(new MultiTypeItem(R.layout.testlayout, "hello7"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
        data.add(new MultiTypeItem(R.layout.testlayout2, generateDs(true, false)));
        data.add(new MultiTypeItem(R.layout.testlayout2, generateDs(true, false)));
        data.add(new MultiTypeItem(R.layout.testlayout2, generateDs(true, false)));
        data.add(new MultiTypeItem(R.layout.testlayout2, generateDs(true, false)));
        data.add(new MultiTypeItem(R.layout.testlayout2, generateDs(true, false)));
        data.add(new MultiTypeItem(R.layout.testlayout2, generateDs(true, false)));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello4"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello4"));

        data.add(new MultiTypeItem(R.layout.testlayout, "hello1"));
//        data.add(new MultiTypeItem(R.layout.testlayout1, "hello2"));
//        data.add(new MultiTypeItem(R.layout.testlayout1, "hello3"));
//        data.add(new MultiTypeItem(R.layout.testlayout2, "test1,test2,test3,test4"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello4"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello5"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello6"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello7"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello8"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello9"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello10"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "hello11"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "hello12"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "hello13"));
//        data.add(new MultiTypeItem(R.layout.testlayout, "hello14"));
        data.add(new MultiTypeItem(R.layout.testlayout2, generateDs(false, false)));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello8"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello9"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello10"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello10"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello10"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello10"));
        data.add(new MultiTypeItem(R.layout.testlayout, "hello10"));


        rv = findViewById(R.id.rv);

        /************************/

        rv.postDelayed(new Runnable() {
            @Override
            public void run() {
//                TextView tv = rv.getChildAt(0).findViewById(R.id.tv);
//                tv.setText("longlonglonglonglonglonglonglong");
//                data.update(0,new MultiTypeItem(R.layout.testlayout,"new one!!"));
            }
        }, 3000);


        /************************/

//        rv.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager;
        rv.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                Log.e("mandy", "onLayoutChildren");
                super.onLayoutChildren(recycler, state);
            }
        });
        rv.setAdapter(adapter = new MultiTypeAdapter(data) {

            public int currentIndex;
            /***********/
            int rvIndex;

            /***********/

            @Override
            protected void onItemClick(ViewHolderForRecyclerView viewHolder, int position, MultiTypeItem data) {
                if (data.getType() != R.layout.testlayout2) {
                    Logger.log("onItemClick position==" + position);
                }
            }

            @Override
            protected void abortLoadMore() {
                super.abortLoadMore();
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            protected void initComponent(ViewHolderForRecyclerView holder, @NonNull ViewGroup parent, int layoutId) {
                if (layoutId == R.layout.testlayout) {
                    holder.setViewClickListener(R.id.tv, new ViewHolderForRecyclerView.OnClickListener() {
                        @Override
                        public void onClick(View view, MultiTypeItem item, int position) {
//                            MultiTypeItem multiTypeItem = data.get(position);
                            Logger.log("mandy", "testlayout click==" + position + " view==" +
                                    view + " item==" + item);
                        }
                    });
                } else if (layoutId == R.layout.testlayout1) {
                    holder.setViewClickListener(R.id.tv, new ViewHolderForRecyclerView.OnClickListener() {
                        @Override
                        public void onClick(View view, MultiTypeItem item, int position) {
                            MultiTypeItem multiTypeItem = data.get(position);
                            Logger.log("mandy", "testlayout1 click position==" + position);
                        }
                    });
                } else if (layoutId == R.layout.testlayout2) {
                    final RecyclerView innerRv = holder.getView(R.id.rv);
                    LinearLayoutManager ll;
                    innerRv.setLayoutManager(ll = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
//                    List<MultiTypeItem> list = new ArrayList<>();
                    MultiTypeAdapter interAdapter;

                    /****************/
//                    innerRv.setHasFixedSize(true);
                    /****************/

                    innerRv.setAdapter(interAdapter = new MultiTypeAdapter() {

                        @Override
                        protected void initComponent(ViewHolderForRecyclerView holder, @NonNull ViewGroup parent, int layoutId) {
                            holder.setViewClickListener(R.id.tv, new ViewHolderForRecyclerView.OnClickListener() {
                                @Override
                                public void onClick(View view, MultiTypeItem item, int position) {
                                    Logger.log("inner adapter click position==" + position);
                                }
                            });
                        }

                        @Override
                        protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
                            String str = (String) item.getData();
                            holder.setText(R.id.tv, str);
                        }

                        @Override
                        protected void loadMore() {
                            super.loadMore();
                            load(((MultiTypeAdapter) innerRv.getAdapter()).getDataSource(), true, currentIndex);
                        }

                        @Override
                        protected void reload() {
                            super.reload();
                            load(((MultiTypeAdapter) innerRv.getAdapter()).getDataSource(), true, currentIndex);
                        }

                        @Override
                        protected void abortLoadMore() {
                            super.abortLoadMore();
                            handler.removeCallbacksAndMessages(null);
                        }

                        //                        @Override
//                        protected View createLoadMoreView(RecyclerView recyclerView) {
//                            return new InnerLoadMoreView(recyclerView, R.layout.innercustomloadmore);
//                        }
                    });

                    Logger.log("innerAdapter==" + innerRv.getAdapter());
                }
            }

            @Override
            protected void onBindView(ViewHolderForRecyclerView holder, MultiTypeItem item, int position, int layoutId) {
                Log.e("mandy", "position==" + position);
                if (layoutId == R.layout.testlayout) {
                    String text = (String) item.getData();
                    holder.setText(R.id.tv, text);
                } else if (layoutId == R.layout.testlayout1) {
                    String text = (String) item.getData();
                    holder.setText(R.id.tv, text);
                } else if (layoutId == R.layout.testlayout2) {
                    RecyclerView recyclerView = holder.getView(R.id.rv);

                    MultiTypeAdapter adapter = (MultiTypeAdapter) recyclerView.getAdapter();
                    Logger.log("adapter==" + recyclerView.getAdapter());
                    DataSource ds = (DataSource) item.getData();
//                    String[] split = str.split(",");
//                    ArrayList<MultiTypeItem> list = new ArrayList<>();
//                    for (String s : split) {
//                        list.add(new MultiTypeItem(R.layout.subitem, s));
//                    }
//                    adapter.getDataSource().clearAndReset(list);


                    currentIndex = position;
                    ds.subscribeOn(adapter);
                    rvIndex++;
//                    if (rvIndex == 1) {
//                        adapter.configRecyclerViewBehavior(false, true, State.LOAD_MORE);
//                    } else if (rvIndex == 2) {
//                        adapter.configRecyclerViewBehavior(false, true, State.LOAD_MORE);
//                    }
                }
            }

            @Override
            protected void onRefreshLocal(ViewHolderForRecyclerView holder, @NonNull List<Object> payloads, int position, int layoutId) {
                super.onRefreshLocal(holder, payloads, position, layoutId);
//                Log.e("mandy", "onRefreshLocal pos==" + position + " payloads==" + payloads.size());
                if (layoutId == R.layout.testlayout) {
                    String text = (String) payloads.get(0);
                    holder.setText(R.id.tv, text);
                }
            }

            @Override
            protected void loadMore() {
                super.loadMore();
                load(adapter.getDataSource(), false, currentIndex);
            }

            @Override
            protected void reload() {
                super.reload();
                load(adapter.getDataSource(), false, currentIndex);
            }

            @Override
            protected AbstractLoadMoreView createLoadMoreView(RecyclerView recyclerView) {
//                return new SimpleLoadMoreView(MainActivity.this,R.layout.customloadmore);
                return new TestLoadMoreView(recyclerView, R.layout.customloadmore);
            }
        });

//        adapter.activateLoadMore(false);
//        adapter.transformLoadMoreState(State.LOAD_MORE);
//        data.configRecyclerViewBehavior(true, true, State.LOAD_MORE);
        Logger.log("out adapter==" + adapter);
//        */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MyApplication.getRefWatcher().watch(this);
    }

    private int index;

    @Override
    public void onBackPressed() {
        data.remove(1);

//        data.set(1,new MultiTypeItem(R.layout.testlayout2,"test1,test2,test3,test4,new one,new two"));

//        data.transformLoadMoreState(State.LOAD_MORE);
//        data.clear();
//        adapter.notifyDataSetChanged();
//        super.onBackPressed();
//        data.update(0,new MultiTypeItem(R.layout.testlayout1,"new one"),false);


//        rv.getItemAnimator().setChangeDuration(0);

//        data.getOriginData().remove(0);
//        data.getOriginData().add(0, new MultiTypeItem(R.layout.testlayout1, "new one"));
//        adapter.notifyItemRangeChanged(0, 1);


//        data.add(new MultiTypeItem(R.layout.testlayout, "new one"));

//        List<MultiTypeItem> list = new ArrayList<>();
//        list.add(new MultiTypeItem(R.layout.testlayout, "add one"));
//        list.add(new MultiTypeItem(R.layout.testlayout, "add two"));
//        data.addAll(list,true);


        /*************************清除重置*****************************/
//        final List<MultiTypeItem> list = new ArrayList<>();
//        list.add(new MultiTypeItem(R.layout.testlayout, "new1"));
//        list.add(new MultiTypeItem(R.layout.testlayout2, generateDs(true, true)));
//        list.add(new MultiTypeItem(R.layout.testlayout, "new2"));
//        list.add(new MultiTypeItem(R.layout.testlayout, "new3"));
//        list.add(new MultiTypeItem(R.layout.testlayout2, generateDs(true, true)));
//        list.add(new MultiTypeItem(R.layout.testlayout1, "new4"));
//        list.add(new MultiTypeItem(R.layout.testlayout1, "new5"));
//        list.add(new MultiTypeItem(R.layout.testlayout1, "new6"));
//        list.add(new MultiTypeItem(R.layout.testlayout1, "new7"));
//        list.add(new MultiTypeItem(R.layout.testlayout1, "new8"));
//        list.add(new MultiTypeItem(R.layout.testlayout1, "new9"));
//        list.add(new MultiTypeItem(R.layout.testlayout1, "new10"));
//        data.beginTransaction();
//        data.add(new MultiTypeItem(R.layout.testlayout1,"last one"));
//        data.endTransaction(new DataSource.Callback() {
//            @Override
//            public void callback() {
//        data.clearAndReset(list, false);
//            }
//        });

        /****************************************************/


        /*********************内部adapgter操作***************************/
//        MultiTypeItem multiTypeItem = data.get(1);
//        final DataSource ds = (DataSource) multiTypeItem.getData();
//        ds.add(new MultiTypeItem(R.layout.subitem, "new add1"));
//        ds.add(new MultiTypeItem(R.layout.subitem, "new add2"));
//        ds.add(new MultiTypeItem(R.layout.subitem, "new add3"));
//        ds.set(1, new MultiTypeItem(R.layout.testlayout1, "update!!"));
        /*********************内部adapgter操作***************************/


        /***************************更新，增加******************************/
//        itemAnimator.setStable(true);
//        data.update(2, new MultiTypeItem(R.layout.testlayout1, "change one!!"));
//        rv.postOnAnimation(new Runnable() {
//            @Override
//            public void run() {
//                itemAnimator.setStable(false);
//                data.add(1, new MultiTypeItem(R.layout.testlayout, "new!!!"));
//            }
//        });
        /***************************更新，增加******************************/


        /***************************更新，增加 plus******************************/
//        data.update(1, new MultiTypeItem(R.layout.testlayout1, "change one!!"));

//        data.beginTransaction();
//        data.remove(0);
//        data.add(1, new MultiTypeItem(R.layout.testlayout, "new!!!"));

//        data.update(2, new MultiTypeItem(R.layout.testlayout1, "change two!!"), false);
//        data.add(1, new MultiTypeItem(R.layout.testlayout, "new!!!"));
//        data.add(3, new MultiTypeItem(R.layout.testlayout, "new!!!"));
//        data.remove(3);
//        data.endTransaction();

        /***************************更新，增加 plus******************************/

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ds.set(1, new MultiTypeItem(R.layout.testlayout1, "update too"));
//            }
//        },3000);

//        super.onBackPressed();
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

//        adapter.transformLoadMoreState(State.LOAD_MORE);
    }

    private void load(final DataSource data, final boolean inner, final int index) {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Random random = new Random();
                int i = random.nextInt(2);
                if (i == 0) {
                    Logger.log("success!!");
//                    adapter.transformLoadMoreState(State.LOAD_MORE);
                    List<MultiTypeItem> list = new ArrayList<>();
                    if (!inner) {
                        list.add(new MultiTypeItem(R.layout.testlayout, "new one"));
//                        list.add(new MultiTypeItem(R.layout.testlayout, "new two"));
                    } else {
                        if (index == 1) {
                            list.add(new MultiTypeItem(R.layout.subitem, "first one"));
                            list.add(new MultiTypeItem(R.layout.subitem, "first two"));
                        } else {
                            list.add(new MultiTypeItem(R.layout.subitem, "second one"));
                            list.add(new MultiTypeItem(R.layout.subitem, "second two"));
                        }
                    }
                    data.loadMore(list);
                } else if (i == 1) {
                    Logger.log("fail!!");
                    data.loadMore(null);
//                    adapter.transformLoadMoreState(State.ERROR);
//                    data.transformLoadMoreState(State.HIDE);
                }

//                        List<MultiTypeItem> list = new ArrayList<>();
//                        list.add(new MultiTypeItem(R.layout.testlayout, "new one"));
//                        list.add(new MultiTypeItem(R.layout.testlayout, "new two"));
//                        data.addAll(list, true);

//                        adapter.notifyItemRangeChanged(data.size() - list.size(), list.size());

//                        notifyItemDataFromLoad(list);

            }
        }, 2000);
    }


    private DataSource generateDs(boolean canLoad, boolean reset) {
//        List<MultiTypeItem> list = new ArrayList<>();
//        DataSource ds = new DataSource(list);
        DataSource ds = new DataSource.Configuration().state(true).loadingAlways(true).applyConfig();
        ds.enableLoadMore(canLoad);
        ds.add(new MultiTypeItem(R.layout.subitem, reset ? "new1" : "test1"));
        ds.add(new MultiTypeItem(R.layout.subitem, reset ? "new1" : "test2"));
        ds.add(new MultiTypeItem(R.layout.subitem, reset ? "new1" : "test3"));
        ds.add(new MultiTypeItem(R.layout.subitem, reset ? "new1" : "test4"));
        return ds;
    }
}
