package com.java.hejiaao;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.util.TimeUtils;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.ArrayList;

import javax.xml.parsers.*;
import org.w3c.dom.*;

public class FetchXML extends Service {
	public boolean isActive;
	static public class DataItem implements Comparable<DataItem> {
		public String title;
		public String url;
		public String content;
		public double weight;
		DataItem(String title_, String url_, String content_) {
			title = title_;
			url = url_;
			content = content_;
		}
		public int compareTo(DataItem other) {
			return - (new Double(weight)).compareTo(new Double(other.weight));
		}
		public int hashCode() {
			return title.hashCode() ^ url.hashCode();
		}
		public boolean equals(DataItem other) {
			return this.url.equals(other.url);
		}
	};

	private int cds = 0;
	private ArrayList adapter_data;
	private ArrayAdapter adapter;

	private ArrayList<DataItem> rawnewslist = new ArrayList();
	private ArrayList<DataItem> newslist = new ArrayList();
	private String rss_url;
	protected History history;

    public FetchXML() {
    	history = History.getInstance("qaq");
    }

    public class LocalBinder extends Binder {
        FetchXML getService() {
            return FetchXML.this;
        }
    }

    private LocalBinder binder = new LocalBinder();

    Thread th;

    String filter = "";

	public void addUpdateList(ArrayAdapter l, ArrayList a) {
		adapter = l;
		adapter_data = a;
	}

	private void applyFilter() {
		this.newslist.clear();
		for (DataItem d : rawnewslist) {
			if (filterCheck(d)) {
				this.newslist.add(d);
			}
		}
		this.cds = 0;
	}

	synchronized private void fetchRSS(String rss_url) {
		try {
			URL url = new URL(rss_url);
			URLConnection tc = url.openConnection();

			Log.i("fetch", "fetching " + rss_url);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(tc.getInputStream());
			NodeList nl = dom.getElementsByTagName("item");
			Log.i("fetch", "downloaded");
			for (int i = 0; i < nl.getLength(); ++ i) {
				Element it = (Element)nl.item(i);
				String title = it.getElementsByTagName("title").item(0).getTextContent();
				String uurl = it.getElementsByTagName("link").item(0).getTextContent();
				String content = it.getElementsByTagName("description").item(0).getTextContent();
				this.history.addCache(uurl, title, content);
				this.rawnewslist.add(new DataItem(title, uurl, content));
			}
			this.applyFilter();
			Log.i("fetch", "fetch done");
		} catch (Exception e) {
			Log.e("rss fetch error", e.getMessage());
		}
		if (this.rawnewslist.size() == 0) {
			this.rawnewslist.add(new DataItem("出错啦", "", "这类新闻被腾讯爷爷删掉了 QwQ"));
			this.applyFilter();
		}
	}

	public void loadMoreCache(int z) {
		this.cds += 5;
		if (this.cds > this.newslist.size()) {
		    this.cds = this.newslist.size();
		}
		if (this.cds > z) {
		    this.cds = z;
		}
		// Log.w("cache", "load more triggered " + this.cds);
	}

	private boolean filterCheck(DataItem d) {
		return d.title.indexOf(this.filter) > -1 || d.content.indexOf(this.filter) > -1;
	}

	public void loadMoreMain() {
		if (adapter_data.size() - 1 < this.cds) {
			int i = adapter_data.size();
			adapter_data.remove(i - 1);
			for (int j = -- i; j < i + 5 && j < this.cds; ++ j) {
				adapter_data.add(this.newslist.get(j));
			}
			if (adapter_data.size() == this.newslist.size()) {
				adapter_data.add(new DataItem("", "", "没有更多啦ovo"));
			} else {
				// Log.i("count", adapter_data.size() + " " + this.newslist.size());
				adapter_data.add(new DataItem("", "", "正在加载更多"));
			}
			adapter.notifyDataSetChanged();
		}
	}

	public void loadMore(int x) {
		final int z = x;
		Thread ths = new Thread() {
			public void run() {
				try {
					this.sleep(300);
				} catch (Exception e) {
				}
				loadMoreCache(z);
				Intent i = new Intent("list_load_done");
				sendBroadcast(i);
			}
		};
		ths.start();
	}

	public void applySearch(String f) {
		this.filter = f;
		this.applyFilter();
		adapter_data.clear();
		adapter_data.add(new DataItem("", "", "正在加载更多"));
		loadMoreMain();
		adapter.notifyDataSetChanged();
	}

    private void threadMain() {
		this.isActive = true;
		fetchRSS(rss_url);
		this.loadMore(5);
		while (this.isActive) {
    		try {
				th.sleep(1000);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void onDestroy() {
		this.isActive = false;
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

    @Override
    public IBinder onBind(Intent intent) {
		rss_url = intent.getStringExtra("url");
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
}
