package com.java.hejiaao;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.Locale;
import android.util.Log;
import android.view.LayoutInflater;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private ArrayList<String> ctnts = new ArrayList();

    private ArrayAdapter categoryListAdapter;


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
        ctnts.add("ABC");
        ctnts.add("DEF");
        ListView lv = (ListView)findViewById(R.id.category_list);
        categoryListAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item, ctnts) {
            @Override
            public View getView(int position, View corr, ViewGroup parent) {
                String item = (String)getItem(position);
                View ov = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item, null);
                ((TextView)ov.findViewById(R.id.title_text)).setText(item);
                return ov;
            }
        };
        lv.setAdapter(categoryListAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initCatagoryList();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
