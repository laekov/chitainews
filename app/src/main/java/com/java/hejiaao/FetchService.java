package com.java.hejiaao;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class FetchService extends Service {
	private class DataItem {
		public String title;
		public String url;
		DataItem(String title_, String url_) {
			title = title_;
			url = url_;
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
        return binder;
    }

    public static final String ACTION = "java.hejiaao.FetchService";

    ArrayList<DataItem> shown = new ArrayList();
	ArrayList<DataItem> hidden = new ArrayList();

	Thread th;

    public FetchService() {
		shown.add(new DataItem("abc", "def"));
		shown.add(new DataItem("ggg", "sss"));
    }

    private void threadMain() {
    	while (true) {
    		try {
    			th.sleep(1000);
			} catch (Exception e) {
			}
		}
	}

	public void updateList(ArrayAdapter l) {
    	for (DataItem d : this.shown) {
    		l.add(d.title);
		}
	}

    @Override
	public void onCreate() {
		th = new Thread() {
			@Override
			public void run() {
				threadMain();
			}
		};
		th.start();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		th.stop();
		super.onDestroy();
	}
}
