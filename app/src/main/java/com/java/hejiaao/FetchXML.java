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

import javax.xml.parsers.*;
import org.w3c.dom.*;

public class FetchXML extends Service {
	public boolean isActive;
	public class DataItem {
		public String title;
		public String url;
		public String content;
		DataItem(String title_, String url_, String content_) {
			title = title_;
			url = url_;
			content = content_;
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

	private ArrayList<ArrayAdapter> adapters = new ArrayList();
	private HashSet<DataItem> newslist = new HashSet();
	private String rss_url;

    public FetchXML() {
    }

    public class LocalBinder extends Binder {
        FetchXML getService() {
            return FetchXML.this;
        }
    }

    private LocalBinder binder = new LocalBinder();

    Thread th;

	public void addUpdateList(ArrayAdapter l) {
		adapters.add(l);
	}

	public void update() {
		for (ArrayAdapter l : this.adapters) {
			l.clear();
			for (DataItem d : this.newslist) {
				l.add(d);
			}
		}
	}

	synchronized private void fetchRSS(String rss_url) {
		try {
			URL url = new URL(rss_url);
			URLConnection tc = url.openConnection();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(tc.getInputStream());
			NodeList nl = dom.getElementsByTagName("item");
			for (int i = 0; i < nl.getLength(); ++ i) {
				Element it = (Element)nl.item(i);
				String title = it.getElementsByTagName("title").item(0).getTextContent();
				String uurl = it.getElementsByTagName("link").item(0).getTextContent();
				String content = it.getElementsByTagName("description").item(0).getTextContent();
				this.newslist.add(new DataItem(title, uurl, content));
			}
		} catch (Exception e) {
		}
	}

    private void threadMain() {
		this.isActive = true;
		fetchRSS(rss_url);
    	while (this.isActive) {
    		try {
				th.sleep(1000);
			} catch (Exception e) {
			}
			break;
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
