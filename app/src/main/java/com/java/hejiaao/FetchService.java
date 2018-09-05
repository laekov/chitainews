package com.java.hejiaao;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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
	public class DataItem {
		public String title;
		public String url;
		DataItem(String title_, String url_) {
			title = title_;
			url = url_;
		}
		public int compareTo(DataItem other) {
			return this.url.compareTo(other.url);
		}
		public int hashCode() {
			return title.hashCode() ^ url.hashCode();
		}
		public boolean equals(DataItem other) {
			return this.url.equals(other.url);
		}
	};

    public class LocalBinder extends Binder {
        FetchService getService() {
            return FetchService.this;
        }
    }
    private LocalBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
		if (th == null) {
			th = new Thread() {
				@Override
				public void run() {
					threadMain();
				}
			};
			th.start();
		}
        return binder;
    }

    HashSet<DataItem> shown = new HashSet();
	HashSet<DataItem> hidden = new HashSet();

	ArrayList<ArrayAdapter> adapters = new ArrayList();

	Thread th;

    public FetchService() {
    }

	public void update() {
		// Log.w("Fetchu", "Size " + this.adapters.size() + " " + this.shown.size());
		for (ArrayAdapter l : this.adapters) {
			l.clear();
			for (DataItem d : this.shown) {
				// Log.w("update", d.title);
				l.add(d);
			}
		}
}

    synchronized private void fetchCategory(String urlString) {
		shown.add(new DataItem("国内新闻", "http://news.qq.com/newsgn/rss_newsgn.xml"));
		shown.add(new DataItem("电影", "http://ent.qq.com/movie/rss_movie.xml"));
	}

    synchronized private void fetchRSS(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection tc = url.openConnection();
			InputStream inps = tc.getInputStream();
			BufferedReader inr = new BufferedReader(new InputStreamReader(inps));
			String inputLine;
			Pattern p = Pattern.compile("href=\"\\S*\"");
			while ((inputLine = inr.readLine()) != null) {
				Matcher m = p.matcher(inputLine);
				while (m.find()) {
					String s = m.group();
					s = s.toLowerCase();
					shown.add(new DataItem(s, "???"));
				}
			}
		} catch (Exception e) {
			Log.e("Fetch", e.getMessage());
		}
	}

    private void threadMain() {
    	while (true) {
    		try {
				fetchCategory("http://rss.qq.com/index.shtml");
				th.sleep(1000);
			} catch (Exception e) {
			}
			break;
		}
	}

	public void addUpdateList(ArrayAdapter l) {
		adapters.add(l);
	}

	@Override
	public void onDestroy() {
		th.stop();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (th == null) {
			th = new Thread() {
				@Override
				public void run() {
					threadMain();
				}
			};
			th.start();
		}
		return super.onStartCommand(intent, flags, startId);
	}
}
