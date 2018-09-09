package com.java.hejiaao;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class EditList extends AppCompatActivity {
    History history;

    ArrayAdapter<CategoryItem> shown_adapter;
    ArrayAdapter<CategoryItem> hidden_adapter;
    ArrayList<CategoryItem> shown_c, hidden_c;

    public void initLists() {
        shown_c  = history.getCategory("shown_c");;
        hidden_c = history.getCategory("hidden_c");
        shown_adapter = new ArrayAdapter<CategoryItem>(getApplicationContext(), R.layout.category_item, shown_c) {
            @Override
            public View getView(int position, View corr, ViewGroup parent) {
                final CategoryItem item = getItem(position);
                View ov = LayoutInflater.from(getApplicationContext()).inflate(R.layout.category_item, null);
                ((TextView)ov.findViewById(R.id.title_txt)).setText(item.title);
                ((TextView)ov.findViewById(R.id.url_text)).setText(item.url);
                ImageButton ib = (ImageButton)ov.findViewById(R.id.move_action);
                ib.setImageResource(android.R.drawable.arrow_down_float);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hidden_adapter.add(item);
                        shown_adapter.remove(item);
                        history.del("shown_c", item.url);
                        history.addCategory("hidden_c", item.url, item.title);
                    }
                });
                return ov;
            }
        };
        ((ListView)findViewById(R.id.shownList)).setAdapter(shown_adapter);
        hidden_adapter = new ArrayAdapter<CategoryItem>(getApplicationContext(), R.layout.category_item, hidden_c) {
            @Override
            public View getView(int position, View corr, ViewGroup parent) {
                final CategoryItem item = getItem(position);
                View ov = LayoutInflater.from(getApplicationContext()).inflate(R.layout.category_item, null);
                ((TextView)ov.findViewById(R.id.title_txt)).setText(item.title);
                ((TextView)ov.findViewById(R.id.url_text)).setText(item.url);
                ImageButton ib = (ImageButton)ov.findViewById(R.id.move_action);
                ib.setImageResource(android.R.drawable.arrow_up_float);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shown_adapter.add(item);
                        hidden_adapter.remove(item);
                        history.del("hidden_c", item.url);
                        history.addCategory("shown_c", item.url, item.title);
                    }
                });
                return ov;
            }
        };
        ((ListView)findViewById(R.id.hiddenList)).setAdapter(hidden_adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        history = History.getInstance("qaq");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        this.initLists();

        ((ImageButton)findViewById(R.id.action_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = ((EditText)findViewById(R.id.edit_url)).getText().toString();
                String title = ((EditText)findViewById(R.id.edit_title)).getText().toString();
                CategoryItem ci = new CategoryItem(title, url);
                history.addCategory("shown_c", url, title);
                shown_adapter.add(ci);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent("update_list");
        sendBroadcast(intent);
        super.onDestroy();
    }
}
