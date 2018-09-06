package com.java.hejiaao;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.Locale;
import android.util.Log;
import android.content.Intent;
import android.view.LayoutInflater;
import android.content.ServiceConnection;
import android.widget.Toast;

import com.java.hejiaao.FetchService;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private ArrayList<FetchService.DataItem> ctnts = new ArrayList();

    private ArrayAdapter<FetchService.DataItem> categoryListAdapter;

    FetchService mfetcher;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            findViewById(R.id.message).setVisibility(View.GONE);
            findViewById(R.id.category_container).setVisibility(View.GONE);
            switch (item.getItemId()) {
                case R.id.navigation_recommandation:
                    mTextMessage.setText(R.string.ic_recommand);
                    return true;
                case R.id.navigation_categories:
                    findViewById(R.id.category_container).setVisibility(View.VISIBLE);
					if (mfetcher != null) {
						mfetcher.update();
					}
                    mTextMessage.setText(R.string.ic_category);
                    return true;
                case R.id.navigation_search:
                    mTextMessage.setText(android.R.string.search_go);
                    return true;
                case R.id.navigation_manage:
                    mTextMessage.setText(R.string.ic_manage);
                    return true;
            }
            return false;
        }
    };


    protected void initCatagoryList() {
        ListView lv = (ListView) findViewById(R.id.category_list);
        categoryListAdapter = new ArrayAdapter<FetchService.DataItem>(getApplicationContext(), R.layout.list_item, ctnts) {
            @Override
            public View getView(int position, View corr, ViewGroup parent) {
                final FetchService.DataItem item = getItem(position);
                View ov = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item, null);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initCatagoryList();
        Intent srvint = new Intent(this, FetchService.class);
        bindService(srvint, conn, BIND_AUTO_CREATE);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}
