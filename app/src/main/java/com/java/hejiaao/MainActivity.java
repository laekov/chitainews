package com.java.hejiaao;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import android.util.Log;
import android.content.Intent;
import android.view.LayoutInflater;
import android.content.ServiceConnection;
import android.widget.Toast;
import com.java.hejiaao.History;

import com.java.hejiaao.FetchService;

public class MainActivity extends AppCompatActivity {

    private ArrayList<CategoryItem> ctnts = new ArrayList();

    private ArrayAdapter<CategoryItem> categoryListAdapter;

    private ArrayList<String> likes_url = new ArrayList();
    private ArrayList<FetchXML.DataItem> likes = new ArrayList();

    FetchService mfetcher;

    History history;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            findViewById(R.id.category_container).setVisibility(View.GONE);
            findViewById(R.id.recmd_container).setVisibility(View.GONE);
            findViewById(R.id.manage_container).setVisibility(View.GONE);
            switch (item.getItemId()) {
                case R.id.navigation_recommandation:
                    findViewById(R.id.recmd_container).setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_categories:
                    findViewById(R.id.category_container).setVisibility(View.VISIBLE);
					if (mfetcher != null) {
						mfetcher.update();
					}
                    return true;
                case R.id.navigation_manage:
                    findViewById(R.id.manage_container).setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };


    protected void initCatagoryList() {
        ListView lv = (ListView) findViewById(R.id.category_list);
        categoryListAdapter = new ArrayAdapter<CategoryItem>(getApplicationContext(), R.layout.list_item, ctnts) {
            @Override
            public View getView(int position, View corr, ViewGroup parent) {
                final CategoryItem item = getItem(position);
                View ov = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item, null);
                ov.findViewById(R.id.content_text).setVisibility(View.GONE);
                ((TextView) ov.findViewById(R.id.title_text)).setText(item.title);
                ((ImageButton)ov.findViewById(R.id.likestar)).setVisibility(View.GONE);

                ov.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent newActivity = new Intent(MainActivity.this, CategoryList.class);
                        newActivity.putExtra("url", item.url);
                        startActivity(newActivity);
                    }
                });
                return ov;
            }
        };
        lv.setAdapter(categoryListAdapter);
    }

    ArrayAdapter<FetchXML.DataItem> mladapter;

    protected void initLikeList() {
        ListView lv = (ListView) findViewById(R.id.recmd_list);
        likes.add(new FetchXML.DataItem("", "", "喜爱列表"));
        likes_url = history.getStars();
        for (String url : likes_url) {
            FetchXML.DataItem d = history.getCache(url);
            if (d.url.length() > 0) {
                likes.add(d);
            }
        }
        likes.add(new FetchXML.DataItem("", "ref", "刷新"));
        mladapter = new ArrayAdapter<FetchXML.DataItem>(getApplicationContext(), R.layout.list_item, likes) {
            @Override
            public View getView(int position, View corr, ViewGroup parent) {
                final FetchXML.DataItem item = getItem(position);
                View ov = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item, null);
                if (item.title.length() > 0) {
                    ((TextView) ov.findViewById(R.id.title_text)).setText(item.title);
                } else {
                    ov.findViewById(R.id.title_text).setVisibility(View.GONE);
                }
                ((TextView) ov.findViewById(R.id.content_text)).setText(item.content);
                ((ImageButton) ov.findViewById(R.id.likestar)).setVisibility(View.GONE);
                ov.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.url.equals("ref")) {
                            likes.clear();
                            likes.add(new FetchXML.DataItem("", "", "喜爱列表"));
                            likes_url = history.getStars();
                            for (String url : likes_url) {
                                FetchXML.DataItem d = history.getCache(url);
                                if (d.url.length() > 0) {
                                    likes.add(d);
                                }
                            }
                            likes.add(new FetchXML.DataItem("", "ref", "刷新"));
                            mladapter.notifyDataSetChanged();
                        } else if (item.url.length() > 0) {
                            Intent newActivity = new Intent(MainActivity.this, NewsView.class);
                            newActivity.putExtra("url", item.url);
                            startActivity(newActivity);
                        }
                    }
                });
                return ov;
            }
        };
        lv.setAdapter(mladapter);
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mfetcher = ((FetchService.LocalBinder)service).getService();
            mfetcher.addUpdateList(categoryListAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mfetcher = null;
        }
    };

    private void initManageList() {
        ArrayList<String> btns = new ArrayList();
        btns.add("管理目录");
        ArrayAdapter<String> arrad = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, btns);
        ((ListView)findViewById(R.id.manage_list)).setAdapter(arrad);
        ((ListView)findViewById(R.id.manage_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, EditList.class);
                    startActivity(intent);
                }
            }
        });
    }

    private BroadcastReceiver mrecv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.history = History.getInstance(getApplicationContext().getFilesDir().getAbsolutePath());

        this.initCatagoryList();
        this.initLikeList();
        Intent srvint = new Intent(this, FetchService.class);
        bindService(srvint, conn, BIND_AUTO_CREATE);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        findViewById(R.id.category_container).setVisibility(View.GONE);
        findViewById(R.id.manage_container).setVisibility(View.GONE);

        mrecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("update_like")) {
                    categoryListAdapter.notifyDataSetChanged();
                    mladapter.notifyDataSetChanged();
                } else if (intent.getAction().equals("update_list")) {
                    mfetcher.update();
                }
            }
        };

        this.initManageList();

        IntentFilter itf = new IntentFilter("update_like");
        itf.addAction("update_list");
        registerReceiver(mrecv, itf);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mrecv);
        unbindService(conn);
        super.onDestroy();
    }
}
