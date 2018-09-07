package com.java.hejiaao;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.ArrayList;

public class FetchService extends Service {
	private History history;
	public boolean isActive;


    public class LocalBinder extends Binder {
        FetchService getService() {
            return FetchService.this;
        }
    }
    private LocalBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    ArrayList<CategoryItem> shown = new ArrayList<>();

	ArrayList<ArrayAdapter> adapters = new ArrayList();

	Thread th;

    public FetchService() {
    	history = History.getInstance("qaq");
    	if (history.getCategory("shown_c").size() + history.getCategory("hidden_c").size() == 0) {

			history.addCategory("shown_c", "http://news.qq.com/newsgn/rss_newsgn.xml", "国内新闻");
			history.addCategory("shown_c", "http://ent.qq.com/movie/rss_movie.xml", "电影");
		}
    }

	public void update() {
    	this.shown = history.getCategory("shown_c");
		// Log.w("Fetchu", "Size " + this.adapters.size() + " " + this.shown.size());
		for (ArrayAdapter l : this.adapters) {
			l.clear();
			for (CategoryItem d : this.shown) {
				// Log.w("update", d.title);
				l.add(d);
			}
		}
}

    synchronized private void fetchCategory(String urlString) {
	}


	public void addUpdateList(ArrayAdapter l) {
		adapters.add(l);
	}

	@Override
	public void onDestroy() {
		this.isActive = false;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
}
