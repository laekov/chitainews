package com.java.hejiaao;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.java.hejiaao.FetchXML;

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
                mfetcher.addUpdateList(newsListAdapter);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mfetcher = null;
        }
    };

    protected void initNewsList() {
        ListView lv = (ListView) findViewById(R.id.newslist);
        newsListAdapter = new ArrayAdapter<FetchXML.DataItem>(getApplicationContext(), R.layout.list_item, ctnts) {
            @Override
            public View getView(int position, View corr, ViewGroup parent) {
                final FetchXML.DataItem item = getItem(position);
                View ov = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item, null);
                ((TextView)ov.findViewById(R.id.title_text)).setText(item.title);
                ((TextView)ov.findViewById(R.id.content_text)).setText(item.content);
                return ov;
            }
        };
        if (mfetcher != null) {
            mfetcher.addUpdateList(newsListAdapter);
        }
		lv.setAdapter(newsListAdapter);
    }


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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				if (mfetcher != null){
					mfetcher.update();
				}
                //finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

}
