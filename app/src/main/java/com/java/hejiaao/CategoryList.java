package com.java.hejiaao;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.java.hejiaao.FetchXML;
import com.java.hejiaao.NewsView;

import java.util.ArrayList;

public class CategoryList extends AppCompatActivity {

	FetchXML mfetcher;
	
    private ArrayAdapter<FetchXML.DataItem> newsListAdapter;
	private ArrayList<FetchXML.DataItem> ctnts = new ArrayList();


    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mfetcher = ((FetchXML.LocalBinder)service).getService();
            if (newsListAdapter != null) {
                mfetcher.addUpdateList(newsListAdapter, ctnts);
			}
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mfetcher = null;
        }
    };

    protected void initNewsList() {
        final History history = History.getInstance(getApplicationContext().getFilesDir().getAbsolutePath());
        ListView lv = (ListView) findViewById(R.id.newslist);
        ctnts.add(new FetchXML.DataItem("", "", "加载中"));
        newsListAdapter = new ArrayAdapter<FetchXML.DataItem>(getApplicationContext(), R.layout.list_item, ctnts) {
            @Override
            public View getView(int position, View corr, ViewGroup parent) {

                final FetchXML.DataItem item = getItem(position);
                View ov = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item, null);

                TextView title_tv = ((TextView)ov.findViewById(R.id.title_text));
				if (item.title.length() > 0) {
                    if (history.has("history", item.url)) {
                        title_tv.setTextColor(Color.parseColor("#888888"));
                    } else {
                        title_tv.setTextColor(Color.parseColor("#000000"));
                    }
                    title_tv.setText(item.title);
                    title_tv.setVisibility(View.VISIBLE);
                } else {
					title_tv.setVisibility(View.GONE);
				}
				if (history.has("like", item.url)) {
                    ((ImageButton)ov.findViewById(R.id.likestar)).setImageResource(android.R.drawable.btn_star_big_on);
                }
                ((TextView)ov.findViewById(R.id.content_text)).setText(item.content);


				ov.setOnClickListener(new View.OnClickListener() {
					@Override
                    public void onClick(View v) {
					    if (item.url.length() == 0) {
					        return;
                        }
                        history.add("history", item.url);
                        Intent newActivity = new Intent(CategoryList.this, NewsView.class);
                        newActivity.putExtra("url", item.url);
                        newActivity.putExtra("title", item.title);
                        newActivity.putExtra("content", item.content);
                        startActivity(newActivity);
                    }
                });
               
                return ov;
            }
        };
        if (mfetcher != null) {
            mfetcher.addUpdateList(newsListAdapter, ctnts);
        }
		lv.setAdapter(newsListAdapter);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// Log.i("scroll", "down on action " + view.getLastVisiblePosition() + "  " + newsListAdapter.getCount());
				if (view.getLastVisiblePosition() + 1 >= newsListAdapter.getCount()) {
                    if (mfetcher != null) {
                        mfetcher.loadMore(newsListAdapter.getCount() + 5);
					}
                }

            }
        });
    }

    private class mBroadcastRecv extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("list_load_done")) {
				mfetcher.loadMoreMain();
            } else if (intent.getAction().equals("update_like")) {
                newsListAdapter.notifyDataSetChanged();
            }
        }
    }

    private mBroadcastRecv mrecv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        Intent srvint = new Intent(this, FetchXML.class);
		srvint.putExtra("url", url);
        bindService(srvint, conn, BIND_AUTO_CREATE);

		this.initNewsList();

		final EditText et = (EditText)findViewById(R.id.filterText);
        final ImageButton af = (ImageButton)findViewById(R.id.applyFilter);

        af.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filter = et.getText().toString();
                mfetcher.applySearch(filter);
            }
        });

        mrecv = new mBroadcastRecv();
        IntentFilter itf = new IntentFilter("list_load_done");
        itf.addAction("update_like");
        registerReceiver(mrecv, itf);
    }


    @Override
    public void onDestroy() {
        unbindService(conn);
        super.onDestroy();
        unregisterReceiver(mrecv);
    }

}
